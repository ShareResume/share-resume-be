package share.resume.com.integrators;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import share.resume.com.controllers.dto.CompanyResponseDto;
import share.resume.com.services.integrators.APIService;
import share.resume.com.services.integrators.CompanyAPIIntegratorService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = CompanyAPIIntegratorServiceTest.Config.class)
@TestPropertySource("classpath:application.properties")
class CompanyAPIIntegratorServiceTest {

    @TestConfiguration
    static class Config {
        @Bean
        public APIService apiService() {
            return new APIService(new OkHttpClient(), new ObjectMapper());
        }

        @Bean
        public CompanyAPIIntegratorService companyAPIIntegratorService(APIService apiService) {
            return new CompanyAPIIntegratorService(apiService);
        }
    }

    @Autowired
    private CompanyAPIIntegratorService companyAPIIntegratorService;

    @Test
    void shouldReturnCompanyWhenItIsARealCompany() {
        List<CompanyResponseDto> companies = companyAPIIntegratorService.getCompaniesByName("Luxoft");

        assertEquals(1, companies.size());
        assertEquals("Luxoft", companies.get(0).getName());
        assertNotNull(companies.get(0).getLogoUrl());
    }
}
