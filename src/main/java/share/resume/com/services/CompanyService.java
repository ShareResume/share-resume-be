package share.resume.com.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import share.resume.com.controllers.dto.CompanyResponseDto;
import share.resume.com.entities.CompanyEntity;
import share.resume.com.exceptions.EntityNotFoundException;
import share.resume.com.repositories.CompanyRepository;
import share.resume.com.services.integrators.CompanyAPIIntegratorService;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CompanyService {
    private final CompanyAPIIntegratorService companyAPIIntegratorService;
    private final CompanyRepository companyRepository;

    @Transactional
    public List<CompanyResponseDto> getCompaniesByName(String name) {
        List<CompanyEntity> companies = companyRepository.findAllByNameLike(name);
        if (companies.isEmpty()) {
            List<CompanyResponseDto> companiesDto = companyAPIIntegratorService.getCompaniesByName(name);
            if (companiesDto.isEmpty()) {
                return Collections.emptyList();
            }
            companies.addAll(saveAll(companiesDto));
        }
        return companies.stream()
                .map(CompanyResponseDto::new)
                .toList();
    }

    private List<CompanyEntity> saveAll(List<CompanyResponseDto> companiesDto) {
        List<CompanyEntity> companies = companiesDto.stream()
                .map(CompanyEntity::new)
                .toList();
        return companyRepository.saveAll(companies);
    }

    @Transactional
    public List<CompanyEntity> getAllByIds(Collection<UUID> ids) {
        List<CompanyEntity> companies = companyRepository.findAllById(ids);
        if (companies.size() != ids.size()) {
            throw new EntityNotFoundException(String.format("One of company was not found"));
        }
        return companies;
    }

}
