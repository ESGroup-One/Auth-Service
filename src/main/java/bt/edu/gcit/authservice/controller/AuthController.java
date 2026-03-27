package bt.edu.gcit.authservice.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bt.edu.gcit.authservice.dto.LoginRequest;
import bt.edu.gcit.authservice.dto.AuthResponse;
import bt.edu.gcit.authservice.entity.User;
import bt.edu.gcit.authservice.service.AuthService;
import bt.edu.gcit.authservice.util.JwtUtil;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register/initiate/{indexNumber}")
    public ResponseEntity<String> initiate(@PathVariable String indexNumber) {
        return ResponseEntity.ok(authService.initiateRegistration(indexNumber));
    }

    @PostMapping("/register/complete")
    public ResponseEntity<String> complete(@RequestBody Map<String, String> request) {
        authService.completeRegistration(
                request.get("indexNumber"),
                request.get("otp"),
                request.get("password"));
        return ResponseEntity.ok("Student registration successful.");
    }

    @PostMapping("/register/superadmin")
    public ResponseEntity<String> registerSuperAdmin(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");

        if (email == null || password == null) {
            return ResponseEntity.badRequest().body("Email and Password are required");
        }

        return ResponseEntity.ok(authService.registerSuperAdmin(email, password));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        User user = authService.login(request.getIndexNumber(), request.getPassword());
        String token = jwtUtil.generateToken(user.getIndexNumber(), user.getRole().name());
        user.setPassword(null);
        user.setOtp(null);
        return ResponseEntity.ok(new AuthResponse(token, user));
    }
}