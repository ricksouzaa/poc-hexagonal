package poc.hexagonal.adapters.in.rest.customer.dtos.response;

import java.time.LocalDate;

import lombok.Data;

@Data
public class CustomerResponse {
  private final String          id;
  private final String          name;
  private final String          taxIdNumber;
  private final LocalDate       birthday;
  private final AddressResponse address;
}
