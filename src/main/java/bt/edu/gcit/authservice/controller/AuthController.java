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
import bt.edu.gcit.authservice.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

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
        return ResponseEntity.ok("Registration Successful!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        String token = authService.login(request.getIndexNumber(), request.getPassword());
        return ResponseEntity.ok(new AuthResponse(token));
    }
}