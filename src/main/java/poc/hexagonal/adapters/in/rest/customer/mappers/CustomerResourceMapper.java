package poc.hexagonal.adapters.in.rest.customer.mappers;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;
import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import org.mapstruct.Mapper;

import poc.hexagonal.adapters.in.rest.customer.dtos.request.CustomerRequest;
import poc.hexagonal.adapters.in.rest.customer.dtos.response.CustomerResponse;
import poc.hexagonal.application.core.domain.customer.models.Customer;

@Mapper(
    componentModel = SPRING,
    injectionStrategy = CONSTRUCTOR
)
public interface CustomerResourceMapper {
  CustomerResponse toDto(Customer customer);

  Customer toModel(CustomerRequest customerRequest);
}
