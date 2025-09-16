package com.camping.admin.domain.event;

import java.time.LocalDateTime;

public interface DomainEvent {
    LocalDateTime getOccurredOn();
}