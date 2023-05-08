package poc.hexagonal.adapters.in.rest.customer.dtos.response;

import lombok.Data;

@Data
public class AddressResponse {
  private final String zipCode;
  private final String street;
  private final String complement;
  private final String district;
  private final String city;
  private final String state;
}
