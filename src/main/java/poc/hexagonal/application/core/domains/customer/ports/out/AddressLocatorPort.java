package poc.hexagonal.application.core.domains.customer.ports.out;

import poc.hexagonal.application.core.domains.customer.models.Address;

import java.util.Optional;

public interface AddressLocatorPort {
  Optional<Address> findByZipCode(String zipCode);
}
