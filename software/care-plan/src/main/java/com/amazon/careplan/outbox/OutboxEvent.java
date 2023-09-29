package com.amazon.careplan.outbox;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "outbox_event", schema = "care_plan")
public class OutboxEvent {

    @Id
    private UUID id;
    private String payload;
    private String eventType;
    private String topic;
    private String publishStatus;
    private Timestamp createdAt;

    public OutboxEvent() {
    }

    public OutboxEvent( String topic, String eventType, String payload) {
        this.id = UUID.randomUUID();
        this.payload = payload;
        this.eventType = eventType;
        this.topic = topic;
        this.publishStatus = EventStatus.CREATED.name();
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEventPayload() {
        return payload;
    }

    public void setPublishStatus(String publishStatus) {
        this.publishStatus = publishStatus;
    }

    public String getTopic() {
        return topic;
    }

    public String getEventType(){
        return eventType;
    }
}
