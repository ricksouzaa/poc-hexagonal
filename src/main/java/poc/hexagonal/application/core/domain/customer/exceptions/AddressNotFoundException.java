package poc.hexagonal.application.core.domain.customer.exceptions;

import poc.hexagonal.application.core.exceptions.CoreException;

public class AddressNotFoundException extends CoreException {
  public AddressNotFoundException() {
    super("Address not found");
  }
}
