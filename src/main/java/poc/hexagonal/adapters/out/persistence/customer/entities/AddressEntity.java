package poc.hexagonal.adapters.out.persistence.customer.entities;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "address")
@DynamicInsert
@DynamicUpdate
public class AddressEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;
  @Column(name = "zip_code", length = 8)
  private String zipCode;
  private String street;
  private String complement;
  private String district;
  private String city;
  @Column(length = 2)
  private String state;

}
