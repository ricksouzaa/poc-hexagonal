package poc.hexagonal.application.core.domain.customer.exceptions;

public class AddressNotInformedException
    extends Throwable {
  public AddressNotInformedException() {
    super("Address not informed");
  }
}
