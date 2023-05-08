package poc.hexagonal.application.core.domain.customer;

import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import poc.hexagonal.application.core.domain.customer.exceptions.AddressNotFoundException;
import poc.hexagonal.application.core.domain.customer.exceptions.AddressNotInformedException;
import poc.hexagonal.application.core.domain.customer.exceptions.InvalidTaxIdNumberException;
import poc.hexagonal.application.core.domain.customer.exceptions.InvalidZipCodeException;
import poc.hexagonal.application.core.domain.customer.models.Address;
import poc.hexagonal.application.core.domain.customer.models.Customer;
import poc.hexagonal.application.core.domain.customer.ports.out.AddressLocatorPort;
import poc.hexagonal.application.core.domain.customer.ports.out.CustomerPersistencePort;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

  @Mock
  private CustomerPersistencePort customerPersistencePort;

  @Mock
  private AddressLocatorPort addressLocatorPort;

  @InjectMocks
  private CustomerService customerService;


  @Test
  void itShouldThrowsInvalidTaxIdNumerExceptionWhenItIsInvalid() {
    Customer customerFirstCase = Customer.builder().build();

    assertThrows(InvalidTaxIdNumberException.class, () -> customerService.save(customerFirstCase));

    Customer customerSecondCase = buildInvalidCustomer();

    assertThrows(InvalidTaxIdNumberException.class, () -> customerService.save(customerSecondCase));
  }

  @Test
  void itShouldThrowsAddressNotInformedExceptionWhenItIsNull() {
    final Customer customer = buildValidCustomer(null);

    assertThrows(AddressNotInformedException.class, () -> customerService.save(customer));
  }

  @Test
  void itShouldThrowsInvalidZipCodeExceptionWhenItIsInvalid() {
    final Customer customerFirstCase = buildValidCustomer(buildInvalidAddressNull());

    assertThrows(InvalidZipCodeException.class, () -> customerService.save(customerFirstCase));

    final Customer customerSecondCase = buildValidCustomer(buildInvalidAddress());

    assertThrows(InvalidZipCodeException.class, () -> customerService.save(customerSecondCase));
  }

  @Test
  void itShouldThrowsAddressNotFoundExceptionWhenItIsNotFound() {
    final Customer customer = buildValidCustomer(buildValidAddress());

    when(addressLocatorPort.findByZipCode(anyString())).thenReturn(Optional.empty());

    assertThrows(AddressNotFoundException.class, () -> customerService.save(customer));
  }


  @Test
  void itShouldDoesNotThrowsExceptionWhenAllItIsRight() {
    final Customer customer = buildValidCustomer(buildValidAddress());

    doNothing().when(customerPersistencePort).save(any(Customer.class));
    when(addressLocatorPort.findByZipCode(anyString())).thenReturn(of(customer.getAddress()));

    assertDoesNotThrow(() -> customerService.save(customer));
  }

  private static Customer buildValidCustomer(Address address) {
    return Customer.builder()
                   .taxIdNumber("12345678901")
                   .address(address)
                   .build();
  }

  private static Customer buildInvalidCustomer() {
    return Customer.builder()
                   .taxIdNumber("1234")
                   .build();
  }

  private static Address buildValidAddress() {
    return Address.builder()
                  .zipCode("12345678")
                  .build();
  }

  private static Address buildInvalidAddressNull() {
    return Address.builder().build();
  }

  private static Address buildInvalidAddress() {
    return Address.builder()
                  .zipCode("123")
                  .build();
  }

}
