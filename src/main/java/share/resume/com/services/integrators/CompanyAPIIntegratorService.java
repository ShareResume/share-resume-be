package share.resume.com.services.integrators;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import share.resume.com.controllers.dto.CompanyResponseDto;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompanyAPIIntegratorService {
    private final APIService apiService;

    @Value("${companies.integrator.url}")
    private String integratorUrl;

    @Value("${companies.integrator.token}")
    private String integratorToken;

    @Value("${companies.integrator.size.parameter}")
    private Integer sizeParameter;


    public List<CompanyResponseDto> getCompaniesByName(String companyName) {
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("name", companyName);
        requestParams.put("size", sizeParameter);
        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("Authorization", integratorToken);
        try {
            Map<String, Object> apiResponse = apiService.sendGetRequest(integratorUrl, requestParams, requestHeaders);
            return parseCompanyResponse(apiResponse);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    private List<CompanyResponseDto> parseCompanyResponse(Map<String, Object> response) {
        List<CompanyResponseDto> companiesDtos = new ArrayList<>();
        List<Map<String, Object>> companies = (List<Map<String, Object>>) response.get("companies");
        for (Map<String, Object> company : companies) {
            Map<String, Object> about = (Map<String, Object>) company.get("about");
            Map<String, Object> assets = (Map<String, Object>) company.get("assets");

            if (about != null) {
                String companyName = (String) about.get("name");
                String logoUrl = null;
                if (assets != null) {
                    Map<String, Object> logo = (Map<String, Object>) assets.get("logoSquare");
                    logoUrl = (logo != null) ? (String) logo.get("src") : "No logo found";
                }
                if (companyName != null && !companyName.isBlank()) {
                    companiesDtos.add(new CompanyResponseDto(companyName, logoUrl));
                }
            }
        }
        return companiesDtos;
    }
}
