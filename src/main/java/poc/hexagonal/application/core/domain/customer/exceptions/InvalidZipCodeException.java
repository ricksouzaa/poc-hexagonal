package poc.hexagonal.application.core.domain.customer.exceptions;

public class InvalidZipCodeException extends Exception {
  public InvalidZipCodeException() {
    super("Invalid zip code (It must have size to 8)");
  }
}
