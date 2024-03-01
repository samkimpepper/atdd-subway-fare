package nextstep.member.domain;

import lombok.AllArgsConstructor;
import nextstep.auth.oauth2.OAuth2User;

@AllArgsConstructor
public class CustomOAuth2User implements OAuth2User {
    private Long id;
    private String email;

    private int age;

    @Override
    public Long getId() {
        return null;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public int getAge() { return age; }
}
