package poc.hexagonal.infrastructure.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import poc.hexagonal.adapters.out.AddressLocatorAdapter;
import poc.hexagonal.adapters.out.CustomerPersistenceAdapter;
import poc.hexagonal.application.core.domain.customer.CustomerService;
import poc.hexagonal.application.core.domain.customer.ports.in.CustomerServicePort;

@Configuration
public class CustomerConfig {
  @Bean
  public CustomerServicePort customerServicePort(
      CustomerPersistenceAdapter customerPersistenceAdapter,
      AddressLocatorAdapter addressLocatorAdapter) {
    return new CustomerService(customerPersistenceAdapter, addressLocatorAdapter);
  }
}
