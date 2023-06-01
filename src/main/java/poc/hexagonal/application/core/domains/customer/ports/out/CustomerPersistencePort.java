package poc.hexagonal.application.core.domains.customer.ports.out;

import poc.hexagonal.application.core.domains.customer.models.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerPersistencePort {
  void save(Customer customer);

  void delete(String id);

  Optional<Customer> findById(String id);

  List<Customer> findAll();
}
