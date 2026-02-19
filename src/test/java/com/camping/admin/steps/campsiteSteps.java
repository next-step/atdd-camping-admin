package com.camping.admin.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class campsiteSteps {

    @Given("캠프사이트 {int}개가 등록되어 있다")
    public void 캠프사이트N개가등록되어있다(int count) {
        System.out.println("캠프사이트 " + count + "개 등록");
    }

    @Given("캠프사이트가 등록되어 있다")
    public void 캠프사이트가등록되어있다() {
        System.out.println("캠프사이트 등록");
    }

    @When("^캠프사이트 목록을 조회한다 \\(GET \"/admin/campsites\"\\)$")
    public void 캠프사이트목록을조회한다() {
        System.out.println("GET /admin/campsites 요청");
    }

    @When("^새로운 캠프사이트를 등록한다 \\(POST \"/admin/campsites\"\\)$")
    public void 새로운캠프사이트를등록한다() {
        System.out.println("POST /admin/campsites 요청");
    }

    @When("^캠프사이트 정보를 수정한다 \\(PUT \"/admin/campsites/\\{id\\}\"\\)$")
    public void 캠프사이트정보를수정한다() {
        System.out.println("PUT /admin/campsites/{id} 요청");
    }

    @Then("^조회에 성공한다 \\((\\d+)\\)$")
    public void 조회에성공한다(int statusCode) {
        System.out.println("응답 상태 코드: " + statusCode);
    }

    @Then("^캠프사이트가 생성된다 \\((\\d+)\\)$")
    public void 캠프사이트가생성된다(int statusCode) {
        System.out.println("응답 상태 코드: " + statusCode);
    }

    @Then("^수정에 성공한다 \\((\\d+)\\)$")
    public void 수정에성공한다(int statusCode) {
        System.out.println("응답 상태 코드: " + statusCode);
    }

    @And("캠프사이트 {int}개가 반환된다")
    public void 캠프사이트N개가반환된다(int count) {
        System.out.println("반환된 캠프사이트 수: " + count);
    }

    @And("생성된 캠프사이트 정보가 반환된다")
    public void 생성된캠프사이트정보가반환된다() {
        System.out.println("생성된 캠프사이트 정보 반환 확인");
    }

    @And("수정된 캠프사이트 정보가 반환된다")
    public void 수정된캠프사이트정보가반환된다() {
        System.out.println("수정된 캠프사이트 정보 반환 확인");
    }
}
