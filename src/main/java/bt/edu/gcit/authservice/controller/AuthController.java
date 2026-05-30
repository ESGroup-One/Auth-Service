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

    @PostMapping("/register/admin")
    public ResponseEntity<String> registerAdmin(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok(authService.registerAdminBySuperAdmin(request));
    }

    @PostMapping("/register/set-password")
    public ResponseEntity<String> setPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String password = request.get("password");

        if (token == null || password == null) {
            return ResponseEntity.badRequest().body("Token and Password are required");
        }

        authService.setAdminPassword(token, password);
        return ResponseEntity.ok("Password set successfully. You can now login.");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        User user = authService.login(request.getIdentifier(),
                request.getPassword());

        String tokenSubject = user.getIndexNumber() != null
                && !user.getIndexNumber().isBlank()
                        ? user.getIndexNumber()
                        : user.getEmail();

        String token = jwtUtil.generateToken(tokenSubject,
                user.getRole().name());

        user.setPassword(null);
        user.setOtp(null);
        user.setPasswordToken(null);

        return ResponseEntity.ok(new AuthResponse(token,
                user));
    }

    @PostMapping("/forgot-password/send-otp")
    public ResponseEntity<Map<String, String>> sendForgotPasswordOtp(@RequestBody Map<String, String> request) {
        String message = authService.sendForgotPasswordOtp(request);
        return ResponseEntity.ok(Map.of("message", message));
    }

    @PostMapping("/forgot-password/verify-otp")
    public ResponseEntity<Map<String, String>> verifyForgotPasswordOtp(@RequestBody Map<String, String> request) {
        String token = authService.verifyForgotPasswordOtp(
                request.get("identifier"),
                request.get("otp"));

        return ResponseEntity.ok(Map.of(
                "message", "OTP verified successfully.",
                "token", token));
    }

    @PostMapping("/forgot-password/set-new-password/{token}")
    public ResponseEntity<Map<String, String>> setForgotPassword(
            @PathVariable String token,
            @RequestBody Map<String, String> request) {
        authService.resetForgotPassword(token, request.get("password"));
        return ResponseEntity.ok(Map.of("message", "Password reset successfully. Please login."));
    }
}