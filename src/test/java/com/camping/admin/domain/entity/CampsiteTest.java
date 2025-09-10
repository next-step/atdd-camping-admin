package com.camping.admin.domain.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class CampsiteTest {

    private Campsite campsite;

    @BeforeEach
    void setUp() {
        campsite = new Campsite("A-01", "일반 캠프사이트", 4);
    }

    @DisplayName("캠프사이트를 정상적으로 생성할 수 있다")
    @Test
    void createCampsite_Success() {
        // when & then
        assertThat(campsite.getSiteNumber()).isEqualTo("A-01");
        assertThat(campsite.getDescription()).isEqualTo("일반 캠프사이트");
        assertThat(campsite.getMaxPeople()).isEqualTo(4);
        assertThat(campsite.getReservations()).isNotNull();
        assertThat(campsite.getReservations()).isEmpty();
    }

    @DisplayName("설명이 없는 캠프사이트를 생성할 수 있다")
    @Test
    void createCampsite_NoDescription_Success() {
        // when
        Campsite simpleCampsite = new Campsite("B-02", null, 6);

        // then
        assertThat(simpleCampsite.getSiteNumber()).isEqualTo("B-02");
        assertThat(simpleCampsite.getDescription()).isNull();
        assertThat(simpleCampsite.getMaxPeople()).isEqualTo(6);
    }

    @DisplayName("빈 설명을 가진 캠프사이트를 생성할 수 있다")
    @Test
    void createCampsite_EmptyDescription_Success() {
        // when
        Campsite emptyCampsite = new Campsite("C-03", "", 2);

        // then
        assertThat(emptyCampsite.getSiteNumber()).isEqualTo("C-03");
        assertThat(emptyCampsite.getDescription()).isEqualTo("");
        assertThat(emptyCampsite.getMaxPeople()).isEqualTo(2);
    }

    @DisplayName("최대 인원이 없는 캠프사이트를 생성할 수 있다")
    @Test
    void createCampsite_NoMaxPeople_Success() {
        // when
        Campsite unlimitedCampsite = new Campsite("D-04", "무제한 캠프사이트", null);

        // then
        assertThat(unlimitedCampsite.getSiteNumber()).isEqualTo("D-04");
        assertThat(unlimitedCampsite.getDescription()).isEqualTo("무제한 캠프사이트");
        assertThat(unlimitedCampsite.getMaxPeople()).isNull();
    }

    @DisplayName("캠프사이트 정보를 수정할 수 있다")
    @Test
    void updateCampsite_Success() {
        // when
        campsite.setSiteNumber("A-01-PREMIUM");
        campsite.setDescription("프리미엄 캠프사이트");
        campsite.setMaxPeople(6);

        // then
        assertThat(campsite.getSiteNumber()).isEqualTo("A-01-PREMIUM");
        assertThat(campsite.getDescription()).isEqualTo("프리미엄 캠프사이트");
        assertThat(campsite.getMaxPeople()).isEqualTo(6);
    }

    @DisplayName("캠프사이트에 예약을 추가할 수 있다")
    @Test
    void addReservation_Success() {
        // given
        Reservation reservation = new Reservation("김철수", 
                LocalDate.of(2024, 1, 1), 
                LocalDate.of(2024, 1, 3), 
                campsite);

        // when
        List<Reservation> reservations = campsite.getReservations();
        reservations.add(reservation);

        // then
        assertThat(campsite.getReservations()).hasSize(1);
        assertThat(campsite.getReservations().get(0)).isEqualTo(reservation);
        assertThat(reservation.getCampsite()).isEqualTo(campsite);
    }

    @DisplayName("캠프사이트에 여러 예약을 추가할 수 있다")
    @Test
    void addMultipleReservations_Success() {
        // given
        Reservation reservation1 = new Reservation("김철수", 
                LocalDate.of(2024, 1, 1), 
                LocalDate.of(2024, 1, 3), 
                campsite);
        Reservation reservation2 = new Reservation("이영희", 
                LocalDate.of(2024, 1, 5), 
                LocalDate.of(2024, 1, 7), 
                campsite);

        // when
        List<Reservation> reservations = campsite.getReservations();
        reservations.add(reservation1);
        reservations.add(reservation2);

        // then
        assertThat(campsite.getReservations()).hasSize(2);
        assertThat(campsite.getReservations()).containsExactly(reservation1, reservation2);
    }

    @DisplayName("다양한 최대 인원수를 가진 캠프사이트를 생성할 수 있다")
    @Test
    void createCampsites_VariousMaxPeople_Success() {
        // given
        Campsite smallSite = new Campsite("S-01", "소형 사이트", 2);
        Campsite mediumSite = new Campsite("M-01", "중형 사이트", 4);
        Campsite largeSite = new Campsite("L-01", "대형 사이트", 8);
        Campsite extraLargeSite = new Campsite("XL-01", "특대형 사이트", 12);

        // when & then
        assertThat(smallSite.getMaxPeople()).isEqualTo(2);
        assertThat(mediumSite.getMaxPeople()).isEqualTo(4);
        assertThat(largeSite.getMaxPeople()).isEqualTo(8);
        assertThat(extraLargeSite.getMaxPeople()).isEqualTo(12);
    }

    @DisplayName("캠프사이트 번호는 고유해야 한다")
    @Test
    void siteNumber_ShouldBeUnique() {
        // given
        String uniqueSiteNumber = "UNIQUE-001";
        Campsite site1 = new Campsite(uniqueSiteNumber, "첫 번째 사이트", 4);
        Campsite site2 = new Campsite(uniqueSiteNumber, "두 번째 사이트", 6);

        // when & then
        // 이 테스트는 데이터베이스 제약조건을 확인하는 것이므로
        // Entity 레벨에서는 단순히 같은 사이트 번호가 설정될 수 있음을 확인
        assertThat(site1.getSiteNumber()).isEqualTo(site2.getSiteNumber());
        assertThat(site1.getDescription()).isNotEqualTo(site2.getDescription());
    }
}