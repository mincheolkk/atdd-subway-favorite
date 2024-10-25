package nextstep.member.application;

import static nextstep.member.domain.GithubResponse.사용자1;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class GithubClientTest {

    @Autowired
    GithubClient githubClient;

    @Test
    void 토큰_요청을_보낸다() {
        // given
        String 사용자1_code = 사용자1.getCode();
        String 사용자1_access_token = 사용자1.getAccessToken();

        // when
        String actual = githubClient.requestGithubToken(사용자1_code);

        // then
        assertThat(actual).isNotBlank();
        assertThat(actual).isEqualTo(사용자1_access_token);
    }
}
