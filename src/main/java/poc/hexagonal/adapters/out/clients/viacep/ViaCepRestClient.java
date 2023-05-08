package poc.hexagonal.adapters.out.clients.viacep;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import poc.hexagonal.adapters.out.clients.viacep.dtos.ViaCepAddressResponse;

@FeignClient(name = "findAddressByZipCode", url = "https://viacep.com.br")
public interface ViaCepRestClient {

  @GetMapping("/ws/{zipCode}/json")
  ResponseEntity<ViaCepAddressResponse> find(@PathVariable String zipCode);

}
