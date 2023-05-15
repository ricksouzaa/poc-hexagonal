package poc.hexagonal.application.core.domain.customer.ports.out;

import poc.hexagonal.application.core.domain.customer.models.Address;

import java.util.Optional;

public interface AddressLocatorPort {
  Optional<Address> findByZipCode(String zipCode);
}
