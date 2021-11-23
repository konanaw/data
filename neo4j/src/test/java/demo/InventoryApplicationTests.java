package demo;

import demo.address.Address;
import demo.address.AddressRepository;
import demo.catalog.Catalog;
import demo.catalog.CatalogRepository;
import demo.inventory.Inventory;
import demo.inventory.InventoryRepository;
import demo.product.Product;
import demo.product.ProductRepository;
import demo.shipment.Shipment;
import demo.shipment.ShipmentRepository;
import demo.shipment.ShipmentStatus;
import demo.warehouse.Warehouse;
import demo.warehouse.WarehouseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.apache.commons.lang.exception.ExceptionUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static demo.inventory.InventoryStatus.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@Testcontainers
@SpringBootTest(classes = InventoryApplication.class)
public class InventoryApplicationTests {

    @Container
    private static Neo4jContainer neo4jContainer = new Neo4jContainer("neo4j:latest")
            .withAdminPassword(null); // Disable password

    @DynamicPropertySource
    static void neo4jProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.neo4j.uri", neo4jContainer::getBoltUrl);
//        registry.add("spring.neo4j.authentication.username", () -> "neo4j");
//        registry.add("spring.neo4j.authentication.password", () -> null);
        System.out.println("---------------- spring.neo4j.uri = " + neo4jContainer.getBoltUrl());
    }

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private CatalogRepository catalogRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @BeforeEach
    public void setup() {
        String boltUrl = neo4jContainer.getBoltUrl();
        try (Driver driver = GraphDatabase.driver(boltUrl, AuthTokens.none()); Session session = driver.session()) {
            session.run("MATCH (n) OPTIONAL MATCH (n)-[r]-() DELETE n, r;",
                    Collections.emptyMap());
                    //.queryResults();
        } catch (Exception e) {
            fail("can't connect to Neo4j! " + ExceptionUtils.getMessage(e));
        }
    }

    @Test
    void testSomethingUsingBolt() {
        // Retrieve the Bolt URL from the container
        String boltUrl = neo4jContainer.getBoltUrl();
        try (Driver driver = GraphDatabase.driver(boltUrl, AuthTokens.none()); Session session = driver.session()) {
            long one = session.run("RETURN 1", Collections.emptyMap()).next().get(0).asLong();
            assertEquals(one, 1L);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void inventoryTest() {

        System.out.println(neo4jContainer.isRunning());

        // <1>
        List<Product> products = Stream.of(
                new Product("Best. Cloud. Ever. (T-Shirt, Men's Large)", "SKU-24642", 21.99),
                new Product("Like a BOSH (T-Shirt, Women's Medium)", "SKU-34563", 14.99),
                new Product("We're gonna need a bigger VM (T-Shirt, Women's Small)", "SKU-12464", 13.99),
                new Product("cf push awesome (Hoodie, Men's Medium)", "SKU-64233", 21.99))
                .map(p -> productRepository.save(p))
                .collect(Collectors.toList());

        Product sample = products.get(0);
        assertEquals(productRepository.findById(sample.getId()).get().getUnitPrice(), sample.getUnitPrice());

        // <2>
        catalogRepository.save(new Catalog("Spring Catalog", products));

        // <3>
        Address warehouseAddress = addressRepository.save(new Address("875 Howard St",
                null, "CA", "San Francisco", "United States", 94103));
        Address shipToAddress = addressRepository.save(new Address(
                "1600 Amphitheatre Parkway", null, "CA", "Mountain View", "United States",
                94043));

        // <4>
        Warehouse warehouse = warehouseRepository.save(new Warehouse("Pivotal SF", warehouseAddress));
        Set<Inventory> inventories = products
                .stream()
                .map(p -> inventoryRepository.save(new Inventory(UUID.randomUUID().toString(), p, warehouse, IN_STOCK)))
                .collect(Collectors.toSet());
        Shipment shipment = shipmentRepository.save(new Shipment(inventories, shipToAddress, warehouse, ShipmentStatus.SHIPPED));
        assertEquals(shipment.getInventories().size(), inventories.size());
    }
}