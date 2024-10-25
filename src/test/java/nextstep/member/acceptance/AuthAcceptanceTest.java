package nextstep.member.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.member.application.dto.GithubTokenRequest;
import nextstep.member.application.dto.MemberResponse;
import nextstep.member.domain.Member;
import nextstep.member.domain.MemberRepository;
import nextstep.utils.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import static nextstep.member.acceptance.AuthSteps.깃헙_로그인_요청;
import static nextstep.member.acceptance.AuthSteps.로그인_토큰발급_요청;
import static nextstep.member.acceptance.MemberSteps.유저조회_요청;
import static nextstep.member.acceptance.MemberSteps.회원_생성_요청;
import static nextstep.member.domain.GithubResponse.*;
import static org.assertj.core.api.Assertions.assertThat;

class AuthAcceptanceTest extends AcceptanceTest {
    public static final String EMAIL = "admin@email.com";
    public static final String PASSWORD = "password";
    public static final Integer AGE = 20;

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("Bearer Auth")
    @Test
    void bearerAuth() {
        memberRepository.save(new Member(EMAIL, PASSWORD, AGE));

        ExtractableResponse<Response> tokenResponse = 로그인_토큰발급_요청(EMAIL, PASSWORD);
        String accessToken = tokenResponse.jsonPath().getString("accessToken");
        assertThat(tokenResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(accessToken).isNotBlank();

        ExtractableResponse<Response> memberInfoResponse = 유저조회_요청(accessToken);
        assertThat(memberInfoResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(memberInfoResponse.jsonPath().getString("email")).isEqualTo(EMAIL);
    }

    /**
     *  Given : 가입한 회원이 존재하고
     *  When : 해당 회원이 깃헙 로그인을 요청하면
     *  Then : 토큰을 발급한다.
     */
    @Test
    void 회원이_깃헙로그인을_한다() {
        // given
        회원_생성_요청(사용자1.getEmail(), "password", 20);
        var request = new GithubTokenRequest(사용자1.getCode());

        // when
        ExtractableResponse<Response> response = 깃헙_로그인_요청(request);

        // then
        assertThat(response.jsonPath().getString("accessToken")).isNotBlank();
    }

    /**
     *  Given : 비회원이
     *  When : 깃헙 로그인을 요청하면
     *  Then : 회원가입을 시키고 토큰을 발급한다.
     */
    @Test
    void 비회원이_깃헙_로그인을_한다() {
        // given
        GithubTokenRequest request = new GithubTokenRequest(사용자2.getCode());

        // when
        ExtractableResponse<Response> response = 깃헙_로그인_요청(request);

        // then
        assertThat(response.jsonPath().getString("accessToken")).isNotBlank();

        MemberResponse memberResponse = 유저조회_요청(response.jsonPath().getString("accessToken"))
                .as(MemberResponse.class);
        assertThat(memberResponse.getEmail()).isEqualTo(사용자2.getEmail());
    }
}