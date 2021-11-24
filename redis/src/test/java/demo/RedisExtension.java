package demo;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

public class RedisExtension implements BeforeAllCallback, AfterAllCallback {

    public static GenericContainer redis;

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        redis = new GenericContainer(DockerImageName.parse("redis:latest")).withExposedPorts(6379);

        redis.start();

        System.setProperty("spring.redis.host", redis.getHost());
        System.setProperty("spring.redis.port", String.valueOf(redis.getFirstMappedPort()));
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        redis.stop();
    }

}
