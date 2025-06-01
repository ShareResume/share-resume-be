package share.resume.com.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import share.resume.com.entities.CommentEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, UUID> {

    List<CommentEntity> findAllByResume_Id(UUID resumeId);
    
    @Query("SELECT c FROM CommentEntity c WHERE c.resume.author.id = :authorId")
    List<CommentEntity> findAllByResumeAuthor_Id(@Param("authorId") UUID authorId);
}
