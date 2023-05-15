package poc.hexagonal.adapters.in.rest.customer.dtos.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CustomerRequest {
  @NotNull
  private String         name;
  @NotNull
  @Size(min = 11, max = 11)
  private String         taxIdNumber;
  private LocalDate      birthday;
  @NotNull
  private AddressRequest address;
}
