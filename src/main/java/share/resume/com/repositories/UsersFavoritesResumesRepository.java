package share.resume.com.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import share.resume.com.entities.UsersFavoritesResumesEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsersFavoritesResumesRepository extends JpaRepository<UsersFavoritesResumesEntity, UUID> {

    Optional<UsersFavoritesResumesEntity> findByUser_IdAndResume_Id(UUID userId, UUID resumeId);

    List<UsersFavoritesResumesEntity> findByUser_Id(UUID userId);

    void deleteByUser_IdAndResume_Id(UUID userId, UUID resumeId);
    
    void deleteByUser_Id(UUID userId);
}
