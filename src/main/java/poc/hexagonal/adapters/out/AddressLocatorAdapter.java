package poc.hexagonal.adapters.out;

import feign.FeignException;
import feign.RetryableException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import poc.hexagonal.adapters.out.clients.brasilapi.BrasilApiCepRestClient;
import poc.hexagonal.adapters.out.clients.brasilapi.mappers.BrasilApiCepMapper;
import poc.hexagonal.adapters.out.clients.viacep.ViaCepRestClient;
import poc.hexagonal.adapters.out.clients.viacep.mappers.ViaCepAddressMapper;
import poc.hexagonal.application.core.domains.customer.models.Address;
import poc.hexagonal.application.core.domains.customer.ports.out.AddressLocatorPort;

@Slf4j
@Component
@RequiredArgsConstructor
public class AddressLocatorAdapter implements AddressLocatorPort {

  private final ViaCepRestClient viaCepRestClient;
  private final ViaCepAddressMapper viaCepAddressMapper;
  private final BrasilApiCepRestClient brasilApiCepRestClient;
  private final BrasilApiCepMapper brasilApiCepMapper;

  @Override
  public Optional<Address> findByZipCode(String zipCode) {
    try {
      Optional<Address> primary = resolveViaCep(zipCode);
      if (primary.isPresent()) {
        log.debug("cep_provider=viacep");
        return primary;
      }
      return Optional.empty();
    } catch (FeignException ex) {
      if (!isTechnicalFailure(ex)) {
        return Optional.empty();
      }
      if (ex instanceof RetryableException) {
        log.debug("cep_fallback from=viacep to=brasilapi retryable");
      } else {
        log.debug("cep_fallback from=viacep to=brasilapi http_status={}", ex.status());
      }
      return resolveBrasilApi(zipCode);
    }
  }

  private Optional<Address> resolveViaCep(String zipCode) {
    return viaCepRestClient.find(zipCode)
        .filter(r -> !Boolean.TRUE.equals(r.getErro()))
        .filter(r -> r.getZipCode() != null)
        .map(viaCepAddressMapper::toModel)
        .map(this::normalizeZipDigits);
  }

  private Optional<Address> resolveBrasilApi(String zipCode) {
    try {
      Optional<Address> out = brasilApiCepRestClient.findByCep(zipCode)
          .filter(r -> r.getCep() != null)
          .map(brasilApiCepMapper::toModel)
          .map(this::normalizeZipDigits);
      if (out.isPresent()) {
        log.debug("cep_provider=brasilapi");
      }
      return out;
    } catch (FeignException ex) {
      log.debug("cep_provider=brasilapi failed: {}", ex.getMessage());
      return Optional.empty();
    }
  }

  private Address normalizeZipDigits(Address address) {
    if (address.getZipCode() != null) {
      address.setZipCode(address.getZipCode().replaceAll("\\D", ""));
    }
    return address;
  }

  /** Falhas que justificam a segunda API (ADR 0001). 4xx de cliente exceto 429/403 não acionam fallback. */
  static boolean isTechnicalFailure(FeignException ex) {
    if (ex instanceof RetryableException) {
      return true;
    }
    int status = ex.status();
    if (status == -1) {
      return true;
    }
    if (status == 429 || status == 403) {
      return true;
    }
    return status >= 500;
  }
}
