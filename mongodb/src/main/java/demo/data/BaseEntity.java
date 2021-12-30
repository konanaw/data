package demo.data;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class BaseEntity {

    private ZonedDateTime lastModified, createdAt;
}
