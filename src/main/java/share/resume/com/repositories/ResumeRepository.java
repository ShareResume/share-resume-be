package share.resume.com.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import share.resume.com.entities.ResumeEntity;
import share.resume.com.entities.UserEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface ResumeRepository extends JpaRepository<ResumeEntity, UUID>, JpaSpecificationExecutor<ResumeEntity> {

    List<ResumeEntity> findAllByAuthor(UserEntity author);
}
