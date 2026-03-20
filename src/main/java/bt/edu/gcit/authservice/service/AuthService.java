package bt.edu.gcit.authservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // Works with the new dependency
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
            userRepository.save(user);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid OTP");
        }
    }

    public String login(String indexNumber, String password) {
        // 1. Find user in the local 'users' collection
        User user = userRepository.findByIndexNumber(indexNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Credentials"));

        // 2. Check if user is verified
        if (!user.isVerified()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Please complete registration first");
        }

        // 3. Verify hashed password
        if (passwordEncoder.matches(password, user.getPassword())) {
            return jwtUtil.generateToken(indexNumber);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Credentials");
        }
    }
}