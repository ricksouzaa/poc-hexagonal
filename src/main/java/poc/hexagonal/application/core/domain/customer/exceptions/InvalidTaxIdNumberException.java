package poc.hexagonal.application.core.domain.customer.exceptions;

import poc.hexagonal.application.core.exceptions.CoreException;

public class InvalidTaxIdNumberException extends CoreException {
  public InvalidTaxIdNumberException() {
    super("Invalid tax id number (It must have size to 11)");
  }
}
