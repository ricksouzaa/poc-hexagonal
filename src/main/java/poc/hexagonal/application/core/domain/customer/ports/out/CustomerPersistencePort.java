package poc.hexagonal.application.core.domain.customer.ports.out;

import java.util.List;
import java.util.Optional;

import poc.hexagonal.application.core.domain.customer.models.Customer;

public interface CustomerPersistencePort {
  void save(Customer customer);
  void delete(String id);
  Optional<Customer> findById(String id);
  List<Customer> findAll();
}
