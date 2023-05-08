package poc.hexagonal.adapters.in.rest.customer.dtos.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AddressRequest {
  @NotNull
  @Size(min = 8, max = 8)
  private String zipCode;
}
