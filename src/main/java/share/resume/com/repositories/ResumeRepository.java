package share.resume.com.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import share.resume.com.entities.ResumeEntity;
import share.resume.com.entities.UserEntity;
import share.resume.com.entities.enums.ResumeStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResumeRepository extends JpaRepository<ResumeEntity, UUID>, JpaSpecificationExecutor<ResumeEntity> {

    List<ResumeEntity> findAllByAuthor(UserEntity author);

    Optional<ResumeEntity> findByIdAndAuthor(UUID id, UserEntity author);

    Optional<ResumeEntity> findByIdAndStatus(UUID id, ResumeStatus status);
}
