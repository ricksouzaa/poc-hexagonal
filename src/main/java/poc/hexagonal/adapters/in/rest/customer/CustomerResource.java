package poc.hexagonal.adapters.in.rest.customer;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import poc.hexagonal.adapters.in.rest.customer.dtos.request.CustomerRequest;
import poc.hexagonal.adapters.in.rest.customer.dtos.response.CustomerResponse;
import poc.hexagonal.adapters.in.rest.customer.mappers.CustomerResourceMapper;
import poc.hexagonal.application.core.domain.customer.exceptions.AddressNotFoundException;
import poc.hexagonal.application.core.domain.customer.exceptions.AddressNotInformedException;
import poc.hexagonal.application.core.domain.customer.exceptions.CustomerNotFoundException;
import poc.hexagonal.application.core.domain.customer.exceptions.InvalidTaxIdNumberException;
import poc.hexagonal.application.core.domain.customer.exceptions.InvalidZipCodeException;
import poc.hexagonal.application.core.domain.customer.ports.in.CustomerServicePort;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.ResponseEntity.noContent;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
class CustomerResource {

  private final CustomerServicePort    customerServicePort;
  private final CustomerResourceMapper customerResourceMapper;

  @PostMapping
  ResponseEntity<Void> create(@RequestBody @Valid CustomerRequest customerRequest)
      throws AddressNotInformedException, AddressNotFoundException, InvalidZipCodeException, InvalidTaxIdNumberException {
    var customer = customerResourceMapper.toModel(customerRequest);
    customerServicePort.save(customer);
    return status(CREATED).build();
  }

  @PutMapping("/{id}")
  ResponseEntity<Void> update(@PathVariable String id, @RequestBody @Valid CustomerRequest customerRequest)
      throws AddressNotInformedException, AddressNotFoundException, InvalidZipCodeException, InvalidTaxIdNumberException {
    try {
      customerServicePort.findById(id);
      var customer = customerResourceMapper.toModel(customerRequest);
      customer.setId(id);
      customerServicePort.save(customer);
      return noContent().build();
    } catch (CustomerNotFoundException e) {
      return notFound().build();
    }
  }

  @DeleteMapping("/{id}")
  ResponseEntity<Void> deleteById(@PathVariable String id) {
    try {
      customerServicePort.delete(id);
      return noContent().build();
    } catch (CustomerNotFoundException e) {
      return notFound().build();
    }
  }

  @GetMapping("/{id}")
  ResponseEntity<CustomerResponse> findById(@PathVariable String id) {
    try {
      var customer = customerServicePort.findById(id);
      return ResponseEntity.ok(customerResourceMapper.toDto(customer));
    } catch (CustomerNotFoundException e) {
      return notFound().build();
    }
  }

  @GetMapping
  ResponseEntity<List<CustomerResponse>> listAll() {
    var response = customerServicePort.findAll()
                                      .stream()
                                      .map(customerResourceMapper::toDto)
                                      .toList();
    return CollectionUtils.isEmpty(response) ?
           noContent().build() : ResponseEntity.ok(response);
  }
}
