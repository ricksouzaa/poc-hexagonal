package poc.hexagonal.adapters.out;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import poc.hexagonal.adapters.out.clients.viacep.ViaCepRestClient;
import poc.hexagonal.adapters.out.clients.viacep.mappers.ViaCepAddressMapper;
import poc.hexagonal.application.core.domains.customer.models.Address;
import poc.hexagonal.application.core.domains.customer.ports.out.AddressLocatorPort;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AddressLocatorAdapter implements AddressLocatorPort {

  private final ViaCepRestClient    viaCepRestClient;
  private final ViaCepAddressMapper viaCepAddressMapper;

  @Override
  public Optional<Address> findByZipCode(String zipCode) {
    var optionalAddress = viaCepRestClient.find(zipCode)
                                          .filter(address -> address.getZipCode() != null)
                                          .map(viaCepAddressMapper::toModel);

    return optionalAddress.map(address -> {
      address.setZipCode(address.getZipCode()
                                .replace("-", ""));
      return address;
    });
  }
}
