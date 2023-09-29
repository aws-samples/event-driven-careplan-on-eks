package com.amazon.careplan.outbox;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OutboxEntryRepository extends CrudRepository<OutboxEvent, UUID>{

    String UPGRADE_SKIPLOCKED = "-2";

    @Lock(value = LockModeType.PESSIMISTIC_WRITE) // adds 'FOR UPDATE' statement
    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = UPGRADE_SKIPLOCKED)})
    List<OutboxEvent> findTop10ByPublishStatus(String publishStatus);

}
