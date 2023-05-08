package poc.hexagonal.adapters.out;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import poc.hexagonal.adapters.out.clients.viacep.ViaCepRestClient;
import poc.hexagonal.adapters.out.clients.viacep.dtos.ViaCepAddressResponse;
import poc.hexagonal.adapters.out.clients.viacep.mappers.ViaCepAddressMapper;
import poc.hexagonal.application.core.domain.customer.models.Address;
import poc.hexagonal.application.core.domain.customer.ports.out.AddressLocatorPort;

@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class AddressLocatorAdapter
    implements AddressLocatorPort {

  private final ViaCepRestClient    viaCepRestClient;
  private final ViaCepAddressMapper viaCepAddressMapper;

  @Override
  public Optional<Address> findByZipCode(String zipCode) {
    ViaCepAddressResponse viaCepAddressResponse = viaCepRestClient.find(zipCode).getBody();

    Optional<Address> addressOptional = Optional.ofNullable(viaCepAddressMapper.toModel(viaCepAddressResponse));

    return addressOptional.filter(address -> address.getZipCode() != null)
                          .map(address -> {
                            address.setZipCode(address.getZipCode()
                                                      .replaceAll("-", ""));
                            return address;
                          });
  }
}
