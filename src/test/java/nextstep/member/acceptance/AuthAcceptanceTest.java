package nextstep.member.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.member.application.dto.GithubTokenRequest;
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
import static org.assertj.core.api.Assertions.assertThat;

class AuthAcceptanceTest extends AcceptanceTest {
    public static final String EMAIL = "admin@email.com";
    public static final String PASSWORD = "password";
    public static final Integer AGE = 20;
    public static final String 사용자1_CODE = "aofijeowifjaoief";

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

    @DisplayName("Github Auth")
    @Test
    void 깃헙_로그인을_구현한다() {
        // given
        GithubTokenRequest request = new GithubTokenRequest(사용자1_CODE);

        // when
        ExtractableResponse<Response> response = 깃헙_로그인_요청(request);

        // then
        assertThat(response.jsonPath().getString("accessToken")).isNotBlank();
    }
}