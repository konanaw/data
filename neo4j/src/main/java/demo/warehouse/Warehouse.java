package demo.warehouse;

import demo.address.Address;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Node
public class Warehouse {

 @Id
 @GeneratedValue
 private Long id;

 private String name;

 @Relationship(type = "HAS_ADDRESS")
 private Address address;

 public Warehouse(String n, Address a) {
  this.name = n;
  this.address = a;
 }

 public Warehouse(String name) {
  this.name = name;
 }
}
