package poc.hexagonal.adapters.out.clients.brasilapi.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import poc.hexagonal.adapters.out.clients.brasilapi.dtos.BrasilApiCepResponse;
import poc.hexagonal.application.core.domains.customer.models.Address;

@Mapper(componentModel = "spring")
public interface BrasilApiCepMapper {

  @Mapping(target = "zipCode", source = "cep")
  @Mapping(target = "district", source = "neighborhood")
  @Mapping(target = "complement", constant = "")
  Address toModel(BrasilApiCepResponse response);
}
