package poc.hexagonal.application.core.domain.customer.ports.out;

import java.util.Optional;

import poc.hexagonal.application.core.domain.customer.models.Address;

public interface AddressLocatorPort {
  Optional<Address> findByZipCode(String zipCode);
}
