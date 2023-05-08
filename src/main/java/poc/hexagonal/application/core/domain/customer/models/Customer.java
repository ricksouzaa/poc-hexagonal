package poc.hexagonal.application.core.domain.customer.models;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Customer {
  private String id;
  private String name;
  private String taxIdNumber;
  private LocalDate birthday;
  private Address address;
}
