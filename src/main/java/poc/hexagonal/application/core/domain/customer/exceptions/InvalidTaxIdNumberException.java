package poc.hexagonal.application.core.domain.customer.exceptions;

public class InvalidTaxIdNumberException extends Exception {
  public InvalidTaxIdNumberException() {
    super("Invalid tax id number (It must have size to 11)");
  }
}
