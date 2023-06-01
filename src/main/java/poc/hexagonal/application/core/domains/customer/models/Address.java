package poc.hexagonal.application.core.domains.customer.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Address {
  private String zipCode;
  private String street;
  private String complement;
  private String district;
  private String city;
  private String state;
}
