package poc.hexagonal.application.core.domain.customer.exceptions;

public class AddressNotFoundException
    extends Exception {
  public AddressNotFoundException() {
    super("Address not found");
  }
}
