package share.resume.com.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import share.resume.com.entities.CompanyEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity, UUID> {

    @Query("SELECT c FROM CompanyEntity c WHERE c.name LIKE %:name%")
    List<CompanyEntity> findAllByNameLike(String name);
}
