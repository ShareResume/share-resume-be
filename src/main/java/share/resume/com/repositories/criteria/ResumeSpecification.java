package share.resume.com.repositories.criteria;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import share.resume.com.controllers.dto.RangeDto;
import share.resume.com.controllers.dto.ResumeFilterDto;
import share.resume.com.entities.ResumeEntity;
import share.resume.com.entities.ResumesCompaniesEntity;
import share.resume.com.entities.enums.SpecialityEnum;

import java.time.LocalDate;
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
            UUID companyId = filter.getCompanyId();
            Boolean isHrScreeningPassed = filter.getIsHrScreeningPassed();
            if (companyId != null || isHrScreeningPassed != null) {
                Join<ResumeEntity, ResumesCompaniesEntity> resumesCompanies = root.join("resumesCompanies");
                if (companyId != null && isHrScreeningPassed != null) {
                    predicates.add(criteriaBuilder.and(
                            criteriaBuilder.equal(resumesCompanies.get("company").get("id"), companyId),
                            criteriaBuilder.equal(resumesCompanies.get("isHrScreeningPassed"), isHrScreeningPassed)
                    ));
                } else {
                    if (companyId != null) {
                        predicates.add(criteriaBuilder.equal(resumesCompanies.get("company").get("id"), companyId));
                    }
                    if (isHrScreeningPassed != null) {
                        predicates.add(criteriaBuilder.equal(resumesCompanies.get("isHrScreeningPassed"), isHrScreeningPassed));
                    }
                }
            }
            SpecialityEnum speciality = filter.getSpeciality();
            if (speciality != null) {
                predicates.add(criteriaBuilder.equal(root.get("speciality"), speciality));
            }

            RangeDto yearOfExperienceRange = filter.getYearOfExperienceRange();
            if (yearOfExperienceRange != null) {
                Float min = yearOfExperienceRange.getMin();
                Float max = yearOfExperienceRange.getMax();
                if (min != null && max != null) {
                    predicates.add(criteriaBuilder.between(root.get("yearsOfExperience"), min, max));
                } else {
                    if (min != null) {
                        predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("yearsOfExperience"), min));
                    }
                    if (max != null) {
                        predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("yearsOfExperience"), max));
                    }
                }
            }

            LocalDate date = filter.getDate();
            if (date != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), date.atStartOfDay()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }));
    }
}
