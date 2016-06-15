package jogLog;

import com.google.common.base.Predicate;
import static com.google.common.base.Predicates.or;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import springfox.documentation.builders.ApiInfoBuilder;
import static springfox.documentation.builders.PathSelectors.regex;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 *
 * @author Kish
 */
@SpringBootApplication
@EnableSwagger2
public class Application {

    private static final boolean SWAGGER_SWITCH = true;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("jogLog RESTful API")
                .description("Helps you manage your jogging activity")
                .termsOfServiceUrl("http://localhost:8080/terms.html")
                .contact("Arun Kish, arun.kish@outlook.com")
                .license("Proprietory License")
                .licenseUrl("http://localhost:8080/license")
                .version("1.0")
                .build();
    }

    @Bean
    public Docket authApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .enable(SWAGGER_SWITCH)
                .groupName("auth-api")
                .apiInfo(apiInfo())
                .select()
                .paths(authPaths())
                .build();
    }

    private Predicate<String> authPaths() {
        return or(
                regex("/api/auth.*")
        );
    }
    
    @Bean
    public Docket registerApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .enable(SWAGGER_SWITCH)
                .groupName("register-api")
                .apiInfo(apiInfo())
                .select()
                .paths(registerPaths())
                .build();
    }

    private Predicate<String> registerPaths() {
        return or(
                regex("/api/register.*")
        );
    }
    
    @Bean
    public Docket userApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .enable(SWAGGER_SWITCH)
                .groupName("user-api")
                .apiInfo(apiInfo())
                .select()
                .paths(userPaths())
                .build();
    }

    private Predicate<String> userPaths() {
        return or(
                regex("/api/users.*")
        );
    }
    
    @Bean
    public Docket entryApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .enable(SWAGGER_SWITCH)
                .groupName("entry-api")
                .apiInfo(apiInfo())
                .select()
                .paths(entryPaths())
                .build();
    }

    private Predicate<String> entryPaths() {
        return or(
                regex("/api/entries.*")
        );
    }
    
}

@Configuration
@ImportResource({"classpath:/spring-security.xml"})
class XmlImportingConfiguration {
}