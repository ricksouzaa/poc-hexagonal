package poc.hexagonal.adapters.out.persistence.customer.mappers;

import org.mapstruct.Mapper;
import poc.hexagonal.adapters.out.persistence.customer.entities.CustomerEntity;
import poc.hexagonal.application.core.domains.customer.models.Customer;

@Mapper
public interface CustomerEntityMapper {
  CustomerEntity toEntity(Customer customer);

  Customer toModel(CustomerEntity customerEntity);
}
