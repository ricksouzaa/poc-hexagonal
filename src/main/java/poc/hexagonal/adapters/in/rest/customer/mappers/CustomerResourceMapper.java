package poc.hexagonal.adapters.in.rest.customer.mappers;

import org.mapstruct.Mapper;
import poc.hexagonal.adapters.in.rest.customer.dtos.request.CustomerRequest;
import poc.hexagonal.adapters.in.rest.customer.dtos.response.CustomerResponse;
import poc.hexagonal.application.core.domain.customer.models.Customer;

@Mapper
public interface CustomerResourceMapper {
  CustomerResponse toDto(Customer customer);

  Customer toModel(CustomerRequest customerRequest);
}
