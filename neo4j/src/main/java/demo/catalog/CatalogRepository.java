package demo.catalog;

import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface CatalogRepository extends Neo4jRepository<Catalog, Long> {
}
