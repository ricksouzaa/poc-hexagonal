package poc.hexagonal.application.core.domains.customer.exceptions;

import poc.hexagonal.application.core.exceptions.CoreException;

public class InvalidZipCodeException extends CoreException {
  public InvalidZipCodeException() {
    super("Invalid zip code (It must have size to 8)");
  }
}
