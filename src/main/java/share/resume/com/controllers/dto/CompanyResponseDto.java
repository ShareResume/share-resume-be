package share.resume.com.controllers.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import share.resume.com.entities.CompanyEntity;
import share.resume.com.entities.ResumesCompaniesEntity;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompanyResponseDto {
    private UUID id;
    private String name;
    private String logoUrl;
    private Boolean isHrScreeningPassed;

    public CompanyResponseDto(CompanyEntity companyEntity) {
        this.name = companyEntity.getName();
        this.logoUrl = companyEntity.getLogoUrl();
        this.id = companyEntity.getId();
    }

    public CompanyResponseDto(String name, String logoUrl) {
        this.name = name;
        this.logoUrl = logoUrl;
    }

    public CompanyResponseDto(ResumesCompaniesEntity resumesCompaniesEntity) {
        this.id = resumesCompaniesEntity.getCompany().getId();
        this.name = resumesCompaniesEntity.getCompany().getName();
        this.logoUrl = resumesCompaniesEntity.getCompany().getLogoUrl();
        this.isHrScreeningPassed = resumesCompaniesEntity.getIsHrScreeningPassed();
    }
}
