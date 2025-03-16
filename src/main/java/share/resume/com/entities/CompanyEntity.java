package share.resume.com.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import share.resume.com.controllers.dto.CompanyResponseDto;

import java.util.UUID;

@Getter
@Setter
@Table(name = "COMPANIES")
@Entity
@NoArgsConstructor
public class CompanyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private String logoUrl;

    public CompanyEntity(CompanyResponseDto companyResponseDto) {
        this.name = companyResponseDto.getName();
        this.logoUrl = companyResponseDto.getLogoUrl();
    }
}
