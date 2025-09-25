package com.camping.admin.context;

import com.camping.admin.domain.entity.Campsite;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class ReservationContext {
    private Long reservationId;

    void clear() {
        reservationId = null;
    }
}
