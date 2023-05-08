package poc.hexagonal.adapters.out.persistence.customer.entities;

import java.time.LocalDate;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "customer")
@DynamicInsert
@DynamicUpdate
public class CustomerEntity {

  @Id
  @GeneratedValue
  @UuidGenerator
  private String        id;
  private String        name;
  private LocalDate     birthday;
  @Column(name = "tax_id_number", length = 11)
  private String        taxIdNumber;
  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "id_address")
  private AddressEntity address;

}
