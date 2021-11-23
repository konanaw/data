package demo.inventory;

import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface InventoryRepository extends Neo4jRepository<Inventory, Long> {
}
