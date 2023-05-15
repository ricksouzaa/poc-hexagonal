package poc.hexagonal.adapters.out;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import poc.hexagonal.adapters.out.clients.viacep.ViaCepRestClient;
import poc.hexagonal.adapters.out.clients.viacep.mappers.ViaCepAddressMapper;
import poc.hexagonal.application.core.domain.customer.models.Address;
import poc.hexagonal.application.core.domain.customer.ports.out.AddressLocatorPort;

import java.util.Optional;

@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class AddressLocatorAdapter
    implements AddressLocatorPort {

  private final ViaCepRestClient    viaCepRestClient;
  private final ViaCepAddressMapper viaCepAddressMapper;

  @Override
  public Optional<Address> findByZipCode(String zipCode) {
    Optional<Address> optionalAddress = viaCepRestClient.find(zipCode)
                                                        .filter(address -> address.getZipCode() != null)
                                                        .map(viaCepAddressMapper::toModel);

    return optionalAddress.map(address -> {
                            address.setZipCode(address.getZipCode()
                                                      .replace("-", ""));
                            return address;
                          });
  }
}
