package poc.hexagonal.application.core.domain.customer.exceptions;

import poc.hexagonal.application.core.exceptions.CoreException;

public class CustomerNotFoundException extends CoreException {
  public CustomerNotFoundException() {
    super("Customer not found");
  }
}
