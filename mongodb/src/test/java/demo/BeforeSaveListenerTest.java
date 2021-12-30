package demo;

import demo.data.BaseEntity;
import demo.data.BeforeSaveListener;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.mapping.event.BeforeSaveEvent;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.TimeZone;

/**
 * Converting from Joda-Time to java.time
 * https://blog.joda.org/2014/11/converting-from-joda-time-to-javatime.html
 */
@SpringBootTest
public class BeforeSaveListenerTest {

    @Autowired
    private BeforeSaveListener beforeSaveListener;

    @Test
    public void test() {

        long timeMillis = System.currentTimeMillis();
        long nanoTime = System.nanoTime();

        LocalDateTime ldt = LocalDateTime.now(); //JSR-310
        ZonedDateTime zdt = ZonedDateTime.now(); //JSR-310
        ZonedDateTime zdtFromInstant= Instant.now().atZone(ZoneId.of("Europe/Minsk"));
        Instant instantFromClock= Instant.now(Clock.systemDefaultZone());
        DateTime timestamp = new DateTime(); //joda

        System.out.println("LocalDateTime: " + ldt.toString());
        System.out.println("ZonedDateTime: " + zdt.toString());
        System.out.println("joda DateTime: " + timestamp.toString());

        BeforeSaveEvent<BaseEntity> event = new BeforeSaveEvent<>(new BaseEntity(), null, null);

        beforeSaveListener.onBeforeSave(event);

        System.out.println(Arrays.toString(TimeZone.getAvailableIDs()));

    }
}
