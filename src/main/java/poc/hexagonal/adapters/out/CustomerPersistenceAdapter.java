package poc.hexagonal.adapters.out;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import poc.hexagonal.adapters.out.persistence.customer.CustomerRepository;
import poc.hexagonal.adapters.out.persistence.customer.mappers.CustomerEntityMapper;
import poc.hexagonal.application.core.domain.customer.models.Customer;
import poc.hexagonal.application.core.domain.customer.ports.out.CustomerPersistencePort;

@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class CustomerPersistenceAdapter
    implements CustomerPersistencePort {

  private final CustomerRepository   customerRepository;
  private final CustomerEntityMapper customerEntityMapper;

  @Override
  public void save(Customer customer) {
    customerRepository.save(customerEntityMapper.toEntity(customer));
  }

  @Override
  public void delete(String id) {
    customerRepository.deleteById(id);
  }

  @Override
  public Optional<Customer> findById(String id) {
    return customerRepository.findById(id)
                             .map(customerEntityMapper::toModel);
  }

  @Override
  public List<Customer> findAll() {
    return customerRepository.findAll()
                             .stream()
                             .map(customerEntityMapper::toModel)
                             .collect(Collectors.toList());
  }
}
