package poc.hexagonal.infrastructure.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import poc.hexagonal.application.core.domain.customer.CustomerService;
import poc.hexagonal.application.core.domain.customer.ports.in.CustomerServicePort;
import poc.hexagonal.application.core.domain.customer.ports.out.AddressLocatorPort;
import poc.hexagonal.application.core.domain.customer.ports.out.CustomerPersistencePort;

@Configuration
public class CustomerConfig {
  @Bean
  CustomerServicePort customerServicePort(
      CustomerPersistencePort customerPersistencePort,
      AddressLocatorPort addressLocatorPort) {
    return new CustomerService(customerPersistencePort, addressLocatorPort);
  }
}
