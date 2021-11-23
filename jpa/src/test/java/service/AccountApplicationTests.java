package service;

import demo.AccountApplication;
import demo.account.Account;
import demo.address.Address;
import demo.address.AddressType;
import demo.creditcard.CreditCard;
import demo.customer.Customer;
import demo.customer.CustomerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static demo.creditcard.CreditCardType.VISA;

@SpringBootTest(classes = AccountApplication.class)
@ActiveProfiles(profiles = "test")
public class AccountApplicationTests {

 @Autowired
 private CustomerRepository customerRepository;

 @Test
 public void customerTest() {
  Account account = new Account("12345");
  Customer customer = new Customer("Jane", "Doe", "jane.doe@gmail.com", account);
  CreditCard creditCard = new CreditCard("1234567890", VISA);
  customer.getAccount().getCreditCards().add(creditCard);

  String street1 = "1600 Pennsylvania Ave NW";
  Address address = new Address(street1, null, "DC", "Washington",
   "United States", AddressType.SHIPPING, 20500);
  customer.getAccount().getAddresses().add(address);

  customer = customerRepository.save(customer);
  Optional<Customer> persistedResult = customerRepository.findById(customer.getId());
  Assertions.assertNotNull(persistedResult.get().getAccount()); // <1>
  Assertions.assertNotNull(persistedResult.get().getCreatedAt());
  Assertions.assertNotNull(persistedResult.get().getLastModified()); // <2>

  Assertions.assertTrue(persistedResult.get().getAccount().getAddresses().stream()
   .anyMatch(add -> add.getStreet1().equalsIgnoreCase(street1))); // <3>

  customerRepository.findByEmailContaining(customer.getEmail()) // <4>
   .orElseThrow(
    () -> new RuntimeException("there's supposed to be a matching record!"));

 }
}