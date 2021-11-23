package demo.address;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.GeneratedValue;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Node
public class Address {

 @Id @GeneratedValue
 private Long id;

 private String street1, street2, state, city, country;

 private Integer zipCode;

 public Address(String street1, String street2, String state, String city,
  String country, Integer zipCode) {
  this.street1 = street1;
  this.street2 = street2;
  this.state = state;
  this.city = city;
  this.country = country;
  this.zipCode = zipCode;
 }
}
