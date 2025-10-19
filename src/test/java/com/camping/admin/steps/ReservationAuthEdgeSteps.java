package com.camping.admin.steps;

import com.camping.admin.support.AuthHelper;
import com.camping.admin.support.HttpSupport;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;

import static com.camping.admin.steps.ReservationListSteps.lastResponse;

public class ReservationAuthEdgeSteps {
    private boolean asBrowser = false;

    @Given("관리자가 로그인하지 않았다.")
    public void 관리자가로그인하지않았다() {
        AuthHelper.clearLogin();
    }

    @Given("로그인 유효기간이 지난 상태다.")
    public void 로그인유효기간이지난상태다() {
        AuthHelper.setExpired();
    }

    @And("브라우저로 본다.")
    public void 브라우저로본다() {
        // When에서 이미 브라우저 모드로 호출을 했음.
        // 해당 메서드는 시나리오에서 제거하는게 나은 방법인지 잘 모르겠습니다.
    }

    @When("예약 목록을 조회했다.")
    public void 예약목록을조회했다() {
        // 시나리오가 앞에서 상태를 정하기 때문에, 토큰이 있을 수도 있고 없을 수도 있음
        lastResponse = HttpSupport.getWithAuth("/admin/reservations");
    }

    @When("예약 목록 화면을 열었다.")
    public void 예약목록화면을열었다() {
        lastResponse = HttpSupport.getAsBrowserNoAuth("/admin/reservations");
    }
}
