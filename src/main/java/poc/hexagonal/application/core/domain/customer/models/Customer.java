package poc.hexagonal.application.core.domain.customer.models;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class Customer {
  private String    id;
  private String    name;
  private String    taxIdNumber;
  private LocalDate birthday;
  private Address   address;
}
