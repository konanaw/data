package demo.inventory;

import demo.product.Product;
import demo.warehouse.Warehouse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Node
public class Inventory {

 @Id
 @GeneratedValue
 private Long id;

 private String inventoryNumber;

 @Relationship(type = "PRODUCT_TYPE", direction = Relationship.Direction.OUTGOING)
 private Product product;

 @Relationship(type = "STOCKED_IN", direction = Relationship.Direction.OUTGOING)
 private Warehouse warehouse;

 private InventoryStatus status;

 public Inventory(String inventoryNumber, Product product, Warehouse warehouse,
  InventoryStatus status) {
  this.inventoryNumber = inventoryNumber;
  this.product = product;
  this.warehouse = warehouse;
  this.status = status;
 }

}
