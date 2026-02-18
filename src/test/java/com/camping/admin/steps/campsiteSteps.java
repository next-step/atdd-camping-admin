package com.camping.admin.steps;

import io.cucumber.java.PendingException;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;

public class    campsiteSteps {
    @Then("응답 상태 코드는 {int}이다")
    public void 응답상태코드는이다(int arg0) {
        System.out.println("응답 상태 코드는 " + arg0 + "입니다.");
    }

    @And("캠프사이트 목록이 반환된다")
    public void 캠프사이트목록이반환된다() {
        System.out.println("캠프사이트 목록이 반환되었습니다.");
    }

    @And("등록된 캠프사이트의 사이트 번호는 {string}이다")
    public void 등록된캠프사이트의사이트번호는이다(String arg0) {
        System.out.println("등록된 캠프사이트의 사이트 번호는 " + arg0 + "입니다.");
    }

    @And("등록된 캠프사이트의 최대 인원은 {int}이다")
    public void 등록된캠프사이트의최대인원은이다(int arg0) {
        System.out.println("등록된 캠프사이트의 최대 인원은 " + arg0 + "입니다.");
    }

    @And("캠프사이트의 설명은 {string}이다")
    public void 캠프사이트의설명은이다(String arg0) {
        System.out.println("캠프사이트의 설명은 " + arg0 + "입니다.");
    }
}
