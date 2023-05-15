package poc.hexagonal.adapters.in.rest.customer.dtos.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CustomerResponse {
  private final String          id;
  private final String          name;
  private final String          taxIdNumber;
  private final LocalDate       birthday;
  private final AddressResponse address;
}
