package demo.data;

import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeSaveEvent;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

@Component
public class BeforeSaveListener extends AbstractMongoEventListener<BaseEntity> {

    @Override
    public void onBeforeSave(BeforeSaveEvent<BaseEntity> event) {

        ZonedDateTime timestamp = ZonedDateTime.now();

        if (event.getSource().getCreatedAt() == null) {
            event.getSource().setCreatedAt(timestamp);
        }

        event.getSource().setLastModified(timestamp);

        super.onBeforeSave(event);
    }
}
