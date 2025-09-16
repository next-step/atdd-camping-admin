package com.camping.admin.domain.enums;

import com.camping.admin.domain.entity.Reservation;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

public enum ReservationStatus {
    WAITING {
        @Override
        public ReservationStatus checkIn(Reservation reservation) {
            throw new IllegalStateException("대기 중인 예약은 체크인할 수 없습니다");
        }

        @Override
        public ReservationStatus checkOut(Reservation reservation) {
            throw new IllegalStateException("대기 중인 예약은 체크아웃할 수 없습니다");
        }
    },

    PENDING {
        @Override
        public ReservationStatus checkIn(Reservation reservation) {
            throw new IllegalStateException("승인 대기 중인 예약은 체크인할 수 없습니다");
        }

        @Override
        public ReservationStatus checkOut(Reservation reservation) {
            throw new IllegalStateException("승인 대기 중인 예약은 체크아웃할 수 없습니다");
        }
    },

    CONFIRMED {
        @Override
        public ReservationStatus checkIn(Reservation reservation) {
            validateCheckInDate(reservation);
            return CHECKED_IN;
        }

        @Override
        public ReservationStatus checkOut(Reservation reservation) {
            throw new IllegalStateException("체크인하지 않은 예약은 체크아웃할 수 없습니다");
        }
    },

    REJECTED {
        @Override
        public ReservationStatus checkIn(Reservation reservation) {
            throw new IllegalStateException("거절된 예약은 체크인할 수 없습니다");
        }

        @Override
        public ReservationStatus checkOut(Reservation reservation) {
            throw new IllegalStateException("거절된 예약은 체크아웃할 수 없습니다");
        }
    },

    CHECKED_IN {
        @Override
        public ReservationStatus checkIn(Reservation reservation) {
            throw new IllegalStateException("이미 체크인된 예약입니다");
        }

        @Override
        public ReservationStatus checkOut(Reservation reservation) {
            return CHECKED_OUT;
        }
    },

    CHECKED_OUT {
        @Override
        public ReservationStatus checkIn(Reservation reservation) {
            throw new IllegalStateException("이미 체크아웃된 예약은 다시 체크인할 수 없습니다");
        }

        @Override
        public ReservationStatus checkOut(Reservation reservation) {
            throw new IllegalStateException("이미 체크아웃된 예약입니다");
        }
    },

    CANCELLED {
        @Override
        public ReservationStatus checkIn(Reservation reservation) {
            throw new IllegalStateException("취소된 예약은 체크인할 수 없습니다");
        }

        @Override
        public ReservationStatus checkOut(Reservation reservation) {
            throw new IllegalStateException("취소된 예약은 체크아웃할 수 없습니다");
        }
    };

    private static final Map<ReservationStatus, Set<ReservationStatus>> ALLOWED_TRANSITIONS;

    static {
        ALLOWED_TRANSITIONS = Map.of(
            WAITING, Set.of(PENDING, CONFIRMED, REJECTED, CANCELLED),
            PENDING, Set.of(CONFIRMED, REJECTED, CANCELLED),
            CONFIRMED, Set.of(CHECKED_IN, CANCELLED),
            REJECTED, Set.of(),
            CHECKED_IN, Set.of(CHECKED_OUT),
            CHECKED_OUT, Set.of(),
            CANCELLED, Set.of()
        );
    }

    public abstract ReservationStatus checkIn(Reservation reservation);
    public abstract ReservationStatus checkOut(Reservation reservation);

    public boolean canTransitionTo(ReservationStatus targetStatus) {
        return ALLOWED_TRANSITIONS.getOrDefault(this, Set.of()).contains(targetStatus);
    }

    public void validateTransition(ReservationStatus targetStatus) {
        if (!canTransitionTo(targetStatus)) {
            throw new IllegalStateException(
                String.format("%s 상태에서 %s 상태로 변경할 수 없습니다", this, targetStatus)
            );
        }
    }

    private static void validateCheckInDate(Reservation reservation) {
        LocalDate today = LocalDate.now();
        LocalDate startDate = reservation.getStartDate();

        if (today.isBefore(startDate)) {
            throw new IllegalArgumentException("체크인 날짜가 아직 되지 않았습니다");
        }

        if (today.isAfter(startDate.plusDays(2))) {
            throw new IllegalArgumentException("체크인 기간이 지났습니다");
        }
    }

    public static ReservationStatus from(String reservationStatus) {
        for (ReservationStatus status : ReservationStatus.values()) {
            if (status.name().equalsIgnoreCase(reservationStatus)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid reservation status: " + reservationStatus);
    }
}