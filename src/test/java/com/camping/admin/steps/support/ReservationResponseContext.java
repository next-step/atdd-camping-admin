package com.camping.admin.steps.support;

import com.camping.admin.utils.ResponseAcceptanceFixture;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;

public class ReservationResponseContext {
    private ExtractableResponse<Response> reserved;
    private ExtractableResponse<Response> latest;

    // 최초 예약 상태를 스냅샷으로 저장해 이후 검증에서 참조할 수 있도록 유지
    public void captureReserved(Response rawResponse, HttpStatus expectedStatus) {
        this.reserved = ResponseAcceptanceFixture.extract(rawResponse, expectedStatus);
        this.latest = this.reserved;
    }

    // 상태 변경 결과만 갱신해 최신 응답 검증에 집중
    public void captureLatest(Response rawResponse, HttpStatus expectedStatus) {
        this.latest = ResponseAcceptanceFixture.extract(rawResponse, expectedStatus);
    }

    public void reset() {
        this.reserved = null;
        this.latest = null;
    }

    public ExtractableResponse<Response> reserved() {
        return reserved;
    }

    public ExtractableResponse<Response> latest() {
        return latest;
    }
}
