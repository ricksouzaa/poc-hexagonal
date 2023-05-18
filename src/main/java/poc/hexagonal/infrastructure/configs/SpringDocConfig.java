package poc.hexagonal.infrastructure.configs;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Hexangonal POC API",
        description = "This a POC from MadMax Team",
        contact = @Contact(
            name = "ClickBus",
            url = "https://www.clickbus.com",
            email = "tech@clickbus.com"
        )
    )
)
public class SpringDocConfig {
}
