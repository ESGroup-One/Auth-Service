package bt.edu.gcit.authservice.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String indexNumber;
    private String cid;
    private String fullName;
    private String email;
    private Object academicMarks; 
    private String password;
    private String otp;
    private boolean isVerified = false;
}