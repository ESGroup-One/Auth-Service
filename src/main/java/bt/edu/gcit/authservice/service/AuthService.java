package bt.edu.gcit.authservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // Works with the new dependency
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.UUID;

import bt.edu.gcit.authservice.dao.RegistryLookupRepository;
import bt.edu.gcit.authservice.dao.UserRepository;
import bt.edu.gcit.authservice.dto.StudentRegistryDTO;
import bt.edu.gcit.authservice.entity.User;
import bt.edu.gcit.authservice.util.JwtUtil;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RegistryLookupRepository registryLookupRepository;
    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtUtil jwtUtil;

    // Standard BCrypt hasher
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public String initiateRegistration(String indexNumber) {
        StudentRegistryDTO student = registryLookupRepository.findByIndex(indexNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "no student found"));

        User newUser = new User();
        newUser.setIndexNumber(student.getIndexNumber());
        newUser.setFullName(student.getFullName());
        newUser.setEmail(student.getEmail());
        newUser.setCid(student.getCid());
        newUser.setAcademicMarks(student.getAcademicMarks());

        String otp = String.valueOf((int) (Math.random() * 900000) + 100000);
        newUser.setOtp(otp);
        userRepository.save(newUser);

        emailService.sendOtpEmail(newUser.getEmail(), otp);
        return "OTP sent to " + newUser.getEmail();
    }

    public void completeRegistration(String indexNumber, String otp, String password) {
        User user = userRepository.findByIndexNumber(indexNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (user.getOtp() != null && user.getOtp().equals(otp)) {
            // Hashing the password using BCrypt
            user.setPassword(passwordEncoder.encode(password));

            user.setVerified(true);
            user.setOtp(null);
            user.setRole(User.Role.student);
            userRepository.save(user);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid OTP");
        }
    }

    public String registerSuperAdmin(String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User with this email already exists");
        }

        User admin = new User();
        admin.setEmail(email);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setRole(User.Role.superadmin);
        admin.setVerified(true);

        userRepository.save(admin);
        return "SuperAdmin registered successfully";
    }

    public String registerAdminBySuperAdmin(Map<String, String> details) {
        if (userRepository.findByEmail(details.get("email")).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }

        User admin = new User();
        admin.setFullName(details.get("fullName"));
        admin.setEmail(details.get("email"));
        admin.setCollegeName(details.get("collegeName"));
        admin.setWebsiteUrl(details.get("websiteUrl"));
        admin.setContactInfo(details.get("contactInfo"));
        admin.setRole(User.Role.admin);
        admin.setVerified(false); // Verified only after setting password

        // Generate a unique token for the set-password link
        String token = UUID.randomUUID().toString();
        admin.setPasswordToken(token);

        userRepository.save(admin);

        String setupLink = "http://localhost:3000/set-password?token=" + token;
        emailService.sendAdminSetupEmail(admin.getEmail(), setupLink);

        return "Admin invited successfully";
    }

    public void setAdminPassword(String token, String newPassword) {
        // Find user by the temporary token
        User user = userRepository.findByPasswordToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid or expired token"));

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordToken(null); // Clear token so it can't be used again
        user.setVerified(true);
        userRepository.save(user);
    }

    public User login(String identifier, String password) {
        User user;

        if (identifier.contains("@")) {
            user = userRepository.findByEmail(identifier)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        } else {
            user = userRepository.findByIndexNumber(identifier)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        }
        if (!user.isVerified()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Please complete registration first");
        }
        if (passwordEncoder.matches(password, user.getPassword())) {
            return user;
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Credentials");
        }
    }
}