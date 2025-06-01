package share.resume.com.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import share.resume.com.entities.DocumentEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface DocumentRepository extends JpaRepository<DocumentEntity, UUID> {
    
    List<DocumentEntity> findByResume_Author_Id(UUID authorId);
} 