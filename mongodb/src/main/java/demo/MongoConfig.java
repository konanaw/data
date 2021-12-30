package demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Date;

@Configuration
public class MongoConfig {

//    Second option
//    @Bean
//    MongoCustomConversions customConversions() {
//        return MongoCustomConversions.create(
//                configurer -> configurer.useSpringDataJavaTimeCodecs()
//                        .registerConverter(DateToZonedDateTimeReadConverter.INSTANCE)
//                        .registerConverter(ZonedDateTimeToDateWriteConverter.INSTANCE));
//    }

    @Bean
    MongoCustomConversions customConversions() {
        return new MongoCustomConversions(
                Arrays.asList(
                        DateToZonedDateTimeReadConverter.INSTANCE,
                        ZonedDateTimeToDateWriteConverter.INSTANCE
                ));
    }

    @ReadingConverter
    public enum DateToZonedDateTimeReadConverter implements Converter<Date, ZonedDateTime> {

        INSTANCE;

        @Override
        public ZonedDateTime convert(Date date) {
            return date.toInstant().atZone(ZoneOffset.systemDefault());
        }
    }

    @WritingConverter
    public enum ZonedDateTimeToDateWriteConverter implements Converter<ZonedDateTime, Date> {

        INSTANCE;

        @Override
        public Date convert(ZonedDateTime zonedDateTime) {
            return Date.from(zonedDateTime.toInstant());
        }
    }
}
