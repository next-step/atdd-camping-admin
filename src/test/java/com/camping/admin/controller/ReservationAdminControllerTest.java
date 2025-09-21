package com.camping.admin.controller;

import com.camping.admin.repository.ReservationRepository;
import com.camping.admin.service.ReservationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReservationAdminController.class)
@DisplayName("예약 관리 컨트롤러 테스트")
class ReservationAdminControllerTest {

    private static final String RESERVATIONS_PATH = "/admin/reservations";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservationService reservationService;

    @MockBean
    private ReservationRepository reservationRepository;

    private static Stream<Arguments> provideInvalidStatusTestCases() {
        return Stream.of(
                Arguments.of(
                        "존재하지 않는 상태값으로 업데이트 시도",
                        """
                                {
                                    "status": "INVALID_STATUS"
                                }
                                """,
                        HttpStatus.BAD_REQUEST
                ),

                Arguments.of(
                        "빈 문자열 상태값으로 업데이트 시도",
                        """
                {
                    "status": ""
                }
                                """,
                        HttpStatus.BAD_REQUEST
                ),

                Arguments.of(
                        "null 상태값으로 업데이트 시도",
                        """
                {
                    "status": null
                }
                                """,
                        HttpStatus.BAD_REQUEST
                ),

                Arguments.of(
                        "빈 요청 본문으로 업데이트 시도",
                        "{}",
                        HttpStatus.BAD_REQUEST
                )
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideInvalidStatusTestCases")
    @DisplayName("잘못된 상태값으로 예약 상태 업데이트 시도 시 400 에러를 반환한다")
    void updateReservationStatus_WithInvalidStatus_ShouldReturn400(
            String testDescription,
            String requestBody,
            HttpStatus expectedStatus) throws Exception {

        // given
        var reservationId = 1L;

        // when & then
        mockMvc.perform(patch(RESERVATIONS_PATH + "/" + reservationId + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().is(expectedStatus.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.status").value(expectedStatus.value()));
    }
}