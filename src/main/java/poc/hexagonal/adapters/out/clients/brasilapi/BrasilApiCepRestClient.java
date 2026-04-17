package poc.hexagonal.adapters.out.clients.brasilapi;

import java.util.Optional;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import poc.hexagonal.adapters.out.clients.brasilapi.dtos.BrasilApiCepResponse;

@FeignClient(
    name = "brasilApiCep",
    url = "${brasilapi.cep.base-url:https://brasilapi.com.br}"
)
public interface BrasilApiCepRestClient {

  @GetMapping("/api/cep/v1/{cep}")
  Optional<BrasilApiCepResponse> findByCep(@PathVariable("cep") String cep);
}
