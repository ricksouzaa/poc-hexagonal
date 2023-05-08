package poc.hexagonal.application.core.domain.customer.exceptions;

public class CustomerNotFoundException
    extends Exception {
  public CustomerNotFoundException() {
    super("Customer not found");
  }
}
