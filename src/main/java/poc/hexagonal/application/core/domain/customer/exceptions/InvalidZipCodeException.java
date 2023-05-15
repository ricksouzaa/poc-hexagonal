package poc.hexagonal.application.core.domain.customer.exceptions;

public class InvalidZipCodeException extends Exception {
  public InvalidZipCodeException() {
    super("Invalid zip code (It should be 8 characters)");
  }
}
