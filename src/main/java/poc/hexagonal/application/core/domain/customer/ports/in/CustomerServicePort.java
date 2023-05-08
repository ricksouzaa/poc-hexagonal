package poc.hexagonal.application.core.domain.customer.ports.in;


import java.util.List;

import poc.hexagonal.application.core.domain.customer.exceptions.AddressNotFoundException;
import poc.hexagonal.application.core.domain.customer.exceptions.AddressNotInformedException;
import poc.hexagonal.application.core.domain.customer.exceptions.CustomerNotFoundException;
import poc.hexagonal.application.core.domain.customer.exceptions.InvalidTaxIdNumberException;
import poc.hexagonal.application.core.domain.customer.exceptions.InvalidZipCodeException;
import poc.hexagonal.application.core.domain.customer.models.Customer;

public interface CustomerServicePort {

  void save(Customer customer)
  throws InvalidTaxIdNumberException, AddressNotFoundException, InvalidZipCodeException, AddressNotInformedException;

  void delete(String id)
  throws CustomerNotFoundException;

  Customer findById(String id)
  throws CustomerNotFoundException;

  List<Customer> findAll();

}
