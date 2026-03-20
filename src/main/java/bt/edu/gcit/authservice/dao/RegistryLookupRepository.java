package bt.edu.gcit.authservice.dao;

import bt.edu.gcit.authservice.dto.StudentRegistryDTO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.Optional;

// This repository looks at the OTHER collection in the same DB
public interface RegistryLookupRepository extends MongoRepository<StudentRegistryDTO, String> {
    
    @Query("{ 'indexNumber' : ?0 }")
    Optional<StudentRegistryDTO> findByIndex(String indexNumber);
}