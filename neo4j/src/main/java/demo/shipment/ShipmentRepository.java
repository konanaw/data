package demo.shipment;

import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface ShipmentRepository extends Neo4jRepository<Shipment, Long> {
}
