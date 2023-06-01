package poc.hexagonal.application.core.domains.customer.exceptions;

import poc.hexagonal.application.core.exceptions.CoreException;

public class AddressNotInformedException extends CoreException {
  public AddressNotInformedException() {
    super("Address not informed");
  }
}
