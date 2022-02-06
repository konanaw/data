package demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers(disabledWithoutDocker = true)
@AutoConfigureMockMvc
@SpringBootTest(classes = UserServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles(profiles = "test")
//@ContextConfiguration(initializers = UserServiceApplicationTests.Initializer.class)
public class UserServiceApplicationTests {

    @Container
    private static GenericContainer redis = new GenericContainer(DockerImageName.parse("redis:latest"))
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.redis.host", redis::getHost);
        registry.add("spring.redis.port", redis::getFirstMappedPort);
    }

//    Second option: 
//    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
//        @Override
//        public void initialize(ConfigurableApplicationContext ctx) {
//            TestPropertyValues.of(
//                    "spring.redis.host:" + redis.getHost(),
//                    "spring.redis.port:" + redis.getFirstMappedPort())
//                    .applyTo(ctx);
//        }
//    }

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private MockMvc mvc;

    @BeforeEach
    public void setUp() {
        printDetailsRedis();
        flush();
    }

    @AfterAll
    public static void stopServer() {
        redis.stop();
    }

    private void printDetailsRedis() {
        String host = redis.getHost();
        Integer port = redis.getFirstMappedPort();
        System.out.println(String.format("============ redis host:port ====== %s:%s ", host, port));
    }

    private void flush() {
        redisTemplate.execute((RedisConnection connection) -> {
            connection.flushDb();
            return "OK";
        });
        userRepository.deleteAll();
    }

    @Test
    public void createUser() throws Exception {
        // Setup test data
        User expectedUser = new User("Jane", "Doe");

        // Test create user success
        User actualUser = objectMapper.readValue(
                this.mvc.perform(
                        post("/users").content(objectMapper.writeValueAsString(expectedUser))
                                .contentType(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(status().isCreated()).andReturn().getResponse()
                        .getContentAsString(), User.class);

        // Test create user conflict
        this.mvc.perform(post("/users")
                .content(objectMapper.writeValueAsString(expectedUser))
                .contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(
                status().isConflict());

        // Clean up
        userService.deleteUser(actualUser.getId());
    }

    @Test
    public void getUser() throws Exception {
        // Setup test data
        User expectedUser = new User("John", "Doe");

        expectedUser = userService.createUser(expectedUser);

        // Test get user success
        this.mvc.perform(get("/users/{id}", expectedUser.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().string(objectMapper.writeValueAsString(expectedUser)));

        // Delete user
        userService.deleteUser(expectedUser.getId());

        // Test get user not found
        this.mvc.perform(get("/users/{id}", expectedUser.getId())).andExpect(
                status().isNotFound());

        // Clean up
        userService.deleteUser(expectedUser.getId());
    }

    @Test
    public void updateUser() throws Exception {
        // Setup test data
        User expectedUser = new User("Johnny", "Appleseed");

        // Test get user not found
        this.mvc.perform(get("/users/{id}", expectedUser.getId())).andExpect(
                status().isNotFound());

        // Test re-create user for cache
        // invalidation
        expectedUser = userService.createUser(expectedUser);

        this.mvc.perform(get("/users/{id}", expectedUser.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().string(objectMapper.writeValueAsString(expectedUser)));

        // Change first name
        expectedUser.setFirstName("John");

        // Test update user for cache
        // invalidation
        this.mvc.perform(
                put("/users/{id}", expectedUser.getId()).content(
                        objectMapper.writeValueAsString(expectedUser)).contentType(
                        MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isNoContent());

        // Test that user was updated
        this.mvc.perform(get("/users/{id}", expectedUser.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().string(objectMapper.writeValueAsString(expectedUser)));

        // Clean up
        userService.deleteUser(expectedUser.getId());
    }

    @Test
    public void deleteUser() throws Exception {
        // Setup test data
        User expectedUser = new User("Sally", "Ride");
        expectedUser = userService.createUser(expectedUser);

        // Test getting the user to put into
        // cache
        this.mvc.perform(get("/users/{id}", expectedUser.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().string(objectMapper.writeValueAsString(expectedUser)));

        // Delete the user and remove from
        // cache
        this.mvc.perform(delete("/users/{id}", expectedUser.getId())).andExpect(
                status().isNoContent());

        // Test get user not found
        this.mvc.perform(get("/users/{id}", expectedUser.getId())).andExpect(
                status().isNotFound());
    }
}
