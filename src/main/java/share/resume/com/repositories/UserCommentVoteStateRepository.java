package share.resume.com.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import share.resume.com.entities.UserCommentVoteStateEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserCommentVoteStateRepository extends JpaRepository<UserCommentVoteStateEntity, UUID> {
    
    List<UserCommentVoteStateEntity> findByUser_Id(UUID userId);
    
    void deleteByUser_Id(UUID userId);
} 