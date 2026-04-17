package poc.hexagonal.adapters.out;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import feign.FeignException;
import feign.Request;
import feign.Request.HttpMethod;
import feign.Response;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import poc.hexagonal.adapters.out.clients.brasilapi.BrasilApiCepRestClient;
import poc.hexagonal.adapters.out.clients.brasilapi.dtos.BrasilApiCepResponse;
import poc.hexagonal.adapters.out.clients.brasilapi.mappers.BrasilApiCepMapper;
import poc.hexagonal.adapters.out.clients.viacep.ViaCepRestClient;
import poc.hexagonal.adapters.out.clients.viacep.dtos.ViaCepAddressResponse;
import poc.hexagonal.adapters.out.clients.viacep.mappers.ViaCepAddressMapper;
import poc.hexagonal.application.core.domains.customer.models.Address;

@ExtendWith(MockitoExtension.class)
class AddressLocatorAdapterTest {

  @Mock
  private ViaCepRestClient viaCepRestClient;

  @Mock
  private ViaCepAddressMapper viaCepAddressMapper;

  @Mock
  private BrasilApiCepRestClient brasilApiCepRestClient;

  @Mock
  private BrasilApiCepMapper brasilApiCepMapper;

  @InjectMocks
  private AddressLocatorAdapter adapter;

  private ViaCepAddressResponse viaOk;
  private Address mappedVia;

  @BeforeEach
  void setUp() {
    viaOk = new ViaCepAddressResponse();
    viaOk.setZipCode("01310-100");
    viaOk.setErro(false);
    mappedVia = Address.builder()
        .zipCode("01310-100")
        .street("Rua X")
        .city("São Paulo")
        .state("SP")
        .district("Bela Vista")
        .complement("")
        .build();
  }

  @Test
  void usesViaCepWhenSuccessfulAndDoesNotCallBrasilApi() {
    when(viaCepRestClient.find("01310100")).thenReturn(Optional.of(viaOk));
    when(viaCepAddressMapper.toModel(viaOk)).thenReturn(mappedVia);

    Optional<Address> result = adapter.findByZipCode("01310100");

    assertTrue(result.isPresent());
    assertEquals("01310100", result.get().getZipCode());
    verify(brasilApiCepRestClient, never()).findByCep(anyString());
  }

  @Test
  void doesNotFallbackWhenViaCepReturnsBusinessNotFound() {
    ViaCepAddressResponse erro = new ViaCepAddressResponse();
    erro.setErro(true);
    when(viaCepRestClient.find("00000000")).thenReturn(Optional.of(erro));

    Optional<Address> result = adapter.findByZipCode("00000000");

    assertFalse(result.isPresent());
    verify(brasilApiCepRestClient, never()).findByCep(anyString());
  }

  @Test
  void fallsBackToBrasilApiWhenViaCepReturns500() {
    when(viaCepRestClient.find("01310100")).thenThrow(feign500());
    BrasilApiCepResponse br = new BrasilApiCepResponse();
    br.setCep("01310-100");
    br.setStreet("Rua Y");
    br.setCity("São Paulo");
    br.setState("SP");
    br.setNeighborhood("Bela Vista");
    Address mappedBr = Address.builder()
        .zipCode("01310100")
        .street("Rua Y")
        .build();
    when(brasilApiCepRestClient.findByCep("01310100")).thenReturn(Optional.of(br));
    when(brasilApiCepMapper.toModel(br)).thenReturn(mappedBr);

    Optional<Address> result = adapter.findByZipCode("01310100");

    assertTrue(result.isPresent());
    assertEquals("01310100", result.get().getZipCode());
    verify(brasilApiCepRestClient).findByCep("01310100");
  }

  @Test
  void returnsEmptyWhenViaCep400WithoutFallback() {
    when(viaCepRestClient.find("01310100")).thenThrow(feign400());

    Optional<Address> result = adapter.findByZipCode("01310100");

    assertFalse(result.isPresent());
    verify(brasilApiCepRestClient, never()).findByCep(anyString());
  }

  @Test
  void returnsEmptyWhenBothProvidersFailTechnically() {
    when(viaCepRestClient.find("01310100")).thenThrow(feign500());
    when(brasilApiCepRestClient.findByCep("01310100")).thenThrow(feign500());

    Optional<Address> result = adapter.findByZipCode("01310100");

    assertFalse(result.isPresent());
  }

  @Test
  void isTechnicalFailure_matchesAdr() {
    assertTrue(AddressLocatorAdapter.isTechnicalFailure(feign500()));
    assertTrue(AddressLocatorAdapter.isTechnicalFailure(feign429()));
    assertFalse(AddressLocatorAdapter.isTechnicalFailure(feign400()));
    assertFalse(AddressLocatorAdapter.isTechnicalFailure(feign404()));
  }

  private static FeignException feign500() {
    return FeignException.errorStatus(
        "find",
        Response.builder()
            .status(500)
            .reason("error")
            .request(dummyRequest())
            .build());
  }

  private static FeignException feign400() {
    return FeignException.errorStatus(
        "find",
        Response.builder()
            .status(400)
            .reason("bad request")
            .request(dummyRequest())
            .build());
  }

  private static FeignException feign404() {
    return FeignException.errorStatus(
        "find",
        Response.builder()
            .status(404)
            .reason("not found")
            .request(dummyRequest())
            .build());
  }

  private static FeignException feign429() {
    return FeignException.errorStatus(
        "find",
        Response.builder()
            .status(429)
            .reason("too many")
            .request(dummyRequest())
            .build());
  }

  private static Request dummyRequest() {
    return Request.create(
        HttpMethod.GET,
        "/",
        Collections.emptyMap(),
        null,
        StandardCharsets.UTF_8);
  }
}
