package bt.edu.gcit.authservice.dto;

import lombok.Data;

@Data
public class OtpRequest {
    private String indexNumber;
    private String otp;
    private String password;
}
