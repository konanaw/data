package demo;

import demo.address.Address;
import demo.invoice.Invoice;
import demo.invoice.InvoiceRepository;
import demo.order.LineItem;
import demo.order.Order;
import demo.order.OrderRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Testcontainers(disabledWithoutDocker = true)
//@DataMongoTest(excludeAutoConfiguration = EmbeddedMongoAutoConfiguration.class) // нет в контексте @Component, поэтому подключаем @SpringBootTest
@SpringBootTest(classes = OrderApplication.class)
@ActiveProfiles(profiles = "test")
@ContextConfiguration(classes = MongoConfig.class)
public class OrderAppMongoDBTest {

    @Container
    private static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.2");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @BeforeEach
    @AfterEach
    public void reset() {
        this.orderRepository.deleteAll();
        this.invoiceRepository.deleteAll();
    }

    @Test
    public void orderTest() {

        Address shippingAddress = new Address("1600 Pennsylvania Ave NW", null, "DC", "Washington", "United States", 20500);
        Order order = new Order("12345", shippingAddress);
        order.addLineItem(new LineItem("Best. Cloud. Ever. (T-Shirt, Men's Large)", "SKU-24642", 1, 21.99, .06));
        order.addLineItem(new LineItem("Like a BOSH (T-Shirt, Women's Medium)", "SKU-34563", 3, 14.99, .06));
        order.addLineItem(new LineItem("We're gonna need a bigger VM (T-Shirt, Women's Small)", "SKU-12464", 4, 13.99, .06));
        order.addLineItem(new LineItem("cf push awesome (Hoodie, Men's Medium)", "SKU-64233", 2, 21.99, .06));

        order = orderRepository.save(order);
        assertNotNull(order.getOrderId());
        assertEquals(order.getLineItems().size(), 4);
        assertEquals(order.getLastModified(), order.getCreatedAt());

        order = orderRepository.save(order);
        assertNotEquals(order.getLastModified(), order.getCreatedAt());

        Address billingAddress = new Address("875 Howard St", null, "CA",
                "San Francisco", "United States", 94103);
        String accountNumber = "918273465";

        Invoice invoice = new Invoice(accountNumber, billingAddress);
        invoice.addOrder(order);
        invoice = invoiceRepository.save(invoice);
        System.out.println(invoice.getCreatedAt());
        assertEquals(invoice.getOrders().size(), 1);

        assertEquals(invoiceRepository.findByBillingAddress(billingAddress), invoice);
    }
}
