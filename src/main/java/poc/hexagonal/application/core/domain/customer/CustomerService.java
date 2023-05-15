package poc.hexagonal.application.core.domain.customer;

import lombok.RequiredArgsConstructor;
import poc.hexagonal.application.core.domain.customer.exceptions.AddressNotFoundException;
import poc.hexagonal.application.core.domain.customer.exceptions.AddressNotInformedException;
import poc.hexagonal.application.core.domain.customer.exceptions.CustomerNotFoundException;
import poc.hexagonal.application.core.domain.customer.exceptions.InvalidTaxIdNumberException;
import poc.hexagonal.application.core.domain.customer.exceptions.InvalidZipCodeException;
import poc.hexagonal.application.core.domain.customer.models.Address;
import poc.hexagonal.application.core.domain.customer.models.Customer;
import poc.hexagonal.application.core.domain.customer.ports.in.CustomerServicePort;
import poc.hexagonal.application.core.domain.customer.ports.out.AddressLocatorPort;
import poc.hexagonal.application.core.domain.customer.ports.out.CustomerPersistencePort;

import java.util.List;

@RequiredArgsConstructor
public class CustomerService implements CustomerServicePort {

  private final CustomerPersistencePort customerPersistencePort;
  private final AddressLocatorPort      addressLocatorPort;

  @Override
  public void save(Customer customer) throws InvalidTaxIdNumberException, AddressNotFoundException, InvalidZipCodeException, AddressNotInformedException {
    validateTaxIdNumber(customer.getTaxIdNumber());
    locateAddress(customer);
    customerPersistencePort.save(customer);
  }

  private void locateAddress(Customer customer) throws AddressNotFoundException, InvalidZipCodeException, AddressNotInformedException {
    validateAddress(customer.getAddress());

    Address address = addressLocatorPort.findByZipCode(customer.getAddress().getZipCode())
                                        .orElseThrow(AddressNotFoundException::new);

    customer.setAddress(address);
  }

  private static void validateTaxIdNumber(String taxIdNumber) throws InvalidTaxIdNumberException {
    if (taxIdNumber == null || taxIdNumber.length() != 11) {
      throw new InvalidTaxIdNumberException();
    }
  }

  private static void validateAddress(Address address) throws InvalidZipCodeException, AddressNotInformedException {
    if (address == null) {
      throw new AddressNotInformedException();
    }
    String zipCode = address.getZipCode();
    if (zipCode == null || zipCode.length() != 8) {
      throw new InvalidZipCodeException();
    }
  }

  @Override
  public void delete(String id) throws CustomerNotFoundException {
    findById(id);
    customerPersistencePort.delete(id);
  }

  @Override
  public Customer findById(String id) throws CustomerNotFoundException {
    return customerPersistencePort.findById(id)
                                  .orElseThrow(CustomerNotFoundException::new);
  }

  @Override
  public List<Customer> findAll() {
    return customerPersistencePort.findAll();
  }
}
