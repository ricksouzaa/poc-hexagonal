package poc.hexagonal.adapters.out.clients.viacep.mappers;

import org.mapstruct.Mapper;

import poc.hexagonal.adapters.out.clients.viacep.dtos.ViaCepAddressResponse;
import poc.hexagonal.application.core.domain.customer.models.Address;

@Mapper
public interface ViaCepAddressMapper {
  Address toModel(ViaCepAddressResponse viaCepAddressResponse);
}
