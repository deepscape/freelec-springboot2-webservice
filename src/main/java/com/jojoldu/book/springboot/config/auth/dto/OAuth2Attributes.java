package com.jojoldu.book.springboot.config.auth.dto;

import com.jojoldu.book.springboot.domain.user.Role;
import com.jojoldu.book.springboot.domain.user.User;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
public class OAuth2Attributes {
    private Map<String, Object> attributes;
    private String nameAttributesKey;
    private String name;
    private String email;
    private String picture;

    @Builder
    public OAuth2Attributes(Map<String, Object> attributes, String nameAttributesKey, String name, String email, String picture) {
        this.attributes = attributes;
        this.nameAttributesKey = nameAttributesKey;
        this.name = name;
        this.email = email;
        this.picture = picture;
    }

    // OAuth2User 에서 반환하는 사용자 정보는 Map 이기 때문에, 아래와 같이 ofGoogle 을 통해  값 하나하나를 변환해야 한다.
    public static OAuth2Attributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        if ("naver".equals(registrationId)) { return ofNaver("id", attributes); }

        return ofGoogle(userNameAttributeName, attributes);
    }

    private static OAuth2Attributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuth2Attributes.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .picture((String) attributes.get("picture"))
                .attributes(attributes)
                .nameAttributesKey(userNameAttributeName)
                .build();
    }

    private static OAuth2Attributes ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>)attributes.get("response");

        return OAuth2Attributes.builder()
                .name((String) response.get("name"))
                .email((String) response.get("email"))
                .picture((String) response.get("profileImage"))
                .attributes(response)
                .nameAttributesKey(userNameAttributeName)
                .build();
    }


    /*
        User Entity 생성
        OAuth2Attributes 에서 Entity 를 생성하는 시점은 처음 가입할 때
        가입할 때, 기본 권한은 GUEST
     */
    public User toEntity() {
        return User.builder()
                .name(name)
                .email(email)
                .picture(picture)
                .role(Role.GUEST)
                .build();
    }
}
