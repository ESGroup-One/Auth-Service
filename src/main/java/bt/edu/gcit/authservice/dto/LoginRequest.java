package bt.edu.gcit.authservice.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String identifier;
    private String password;
}