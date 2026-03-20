package bt.edu.gcit.authservice.dto;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "student_registry")
public class StudentRegistryDTO {
    private String indexNumber;
    private String cid;
    private String fullName;
    private String email;
    private Object academicMarks;
}