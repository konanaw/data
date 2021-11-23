package demo.product;

import demo.catalog.Catalog;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Node
public class Product {

 @Id
 @GeneratedValue
 private Long id;

 private String name, productId;

 private Double unitPrice;

 public Product(String name, String productId, Double unitPrice) {
  this.name = name;
  this.productId = productId;
  this.unitPrice = unitPrice;
 }

}
