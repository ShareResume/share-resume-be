package share.resume.com.repositories.criteria;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import share.resume.com.controllers.dto.RangeDto;
import share.resume.com.controllers.dto.ResumeFilterDto;
import share.resume.com.entities.CompanyEntity;
import share.resume.com.entities.ResumeEntity;
import share.resume.com.entities.ResumesCompaniesEntity;
import share.resume.com.entities.enums.SpecialityEnum;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class ResumeSpecification {

    public Specification<ResumeEntity> filterBy(ResumeFilterDto filter) {
        return (((root, query, criteriaBuilder) -> {
            if (filter == null) {
                return criteriaBuilder.and();
            }
            List<Predicate> predicates = new ArrayList<>();

            SpecialityEnum speciality = filter.getSpeciality();
            if (speciality != null) {
                predicates.add(criteriaBuilder.equal(root.get("speciality"), speciality));
            }

            RangeDto yearOfExperienceRange = filter.getYearOfExperienceRange();
            if (yearOfExperienceRange != null) {
                Float min = yearOfExperienceRange.getMin();
                Float max = yearOfExperienceRange.getMax();
                predicates.add(criteriaBuilder.between(root.get("yearsOfExperience"), min, max));
            }

            LocalDate date = filter.getDate();
            if (date != null) {
                predicates.add(criteriaBuilder.between(root.get("createdAt"), date.atStartOfDay(), date.atTime(LocalTime.MAX)));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }));
    }
}
