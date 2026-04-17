package poc.hexagonal.adapters.out.clients.brasilapi.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BrasilApiCepResponse {

  private String cep;
  private String state;
  private String city;
  private String neighborhood;
  private String street;

  @JsonProperty("service")
  private String service;
}
