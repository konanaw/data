package demo.catalog;

import demo.product.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Node
public class Catalog {

 @Id @GeneratedValue
 private Long id;

 @Relationship(type = "HAS_PRODUCT", direction = Relationship.Direction.OUTGOING)
 private Set<Product> products = new HashSet<>();

 private String name;

 public Catalog(String n, Collection<Product> p) {
  this.name = n;
  this.products.addAll(p);
 }

 public Catalog(String name) {
  this.name = name;
 }

}
