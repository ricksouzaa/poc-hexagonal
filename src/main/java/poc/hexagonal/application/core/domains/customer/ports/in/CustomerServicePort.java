package poc.hexagonal.application.core.domains.customer.ports.in;


import poc.hexagonal.application.core.domains.customer.exceptions.AddressNotFoundException;
import poc.hexagonal.application.core.domains.customer.exceptions.AddressNotInformedException;
import poc.hexagonal.application.core.domains.customer.exceptions.CustomerNotFoundException;
import poc.hexagonal.application.core.domains.customer.exceptions.InvalidTaxIdNumberException;
import poc.hexagonal.application.core.domains.customer.exceptions.InvalidZipCodeException;
import poc.hexagonal.application.core.domains.customer.models.Customer;

import java.util.List;

public interface CustomerServicePort {

  void save(Customer customer) throws InvalidTaxIdNumberException, AddressNotFoundException, InvalidZipCodeException, AddressNotInformedException;

  void delete(String id) throws CustomerNotFoundException;

  Customer findById(String id) throws CustomerNotFoundException;

  List<Customer> findAll();

}
