package poc.hexagonal.adapters.out.clients.viacep.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ViaCepAddressResponse {

  /** Quando {@code true}, CEP inexistente na ViaCEP (não acionar fallback). */
  private Boolean erro;

  @JsonProperty("cep")
  private String zipCode;
  @JsonProperty("logradouro")
  private String street;
  @JsonProperty("complemento")
  private String complement;
  @JsonProperty("bairro")
  private String district;
  @JsonProperty("localidade")
  private String city;
  @JsonProperty("uf")
  private String state;
}
