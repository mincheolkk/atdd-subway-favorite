package nextstep.member.application;

import nextstep.global.exception.AuthenticationException;
import nextstep.global.exception.GithubConnectionFailException;
import nextstep.member.application.dto.GithubAccessTokenRequest;
import nextstep.member.application.dto.GithubAccessTokenResponse;
import nextstep.member.config.GithubProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class GithubClient {

    private static final String ACCESS_TOKEN_URL = "/github/login/oauth/access_token";

    private final GithubProperties githubProperties;
    private final RestTemplate restTemplate;

    public GithubClient(GithubProperties githubProperties) {
        this.githubProperties = githubProperties;
        this.restTemplate = new RestTemplateBuilder()
                .rootUri(githubProperties.getUrl())
                .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
                .messageConverters(new MappingJackson2HttpMessageConverter())
                .build();
    }

    public String requestGithubToken(String code) {
        var body = GithubAccessTokenRequest.of(
                code,
                githubProperties.getClientId(),
                githubProperties.getClientSecret()
        );
        var httpEntity = new HttpEntity<>(body);
        var response = restTemplate.exchange(
                        ACCESS_TOKEN_URL,
                        HttpMethod.POST,
                        httpEntity,
                        GithubAccessTokenResponse.class
                ).getBody();

        validateGithubResponse(response);
        validateToken(response.getAccessToken());
        return response.getAccessToken();
    }

    private void validateGithubResponse(GithubAccessTokenResponse response) {
        if (response == null) {
            throw new GithubConnectionFailException();
        }
    }

    private void validateToken(String token) {
        if (token == null || token.isBlank()) {
            throw new AuthenticationException();
        }
    }
}
