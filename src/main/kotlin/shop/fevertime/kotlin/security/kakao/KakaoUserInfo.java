package shop.fevertime.kotlin.security.kakao;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class KakaoUserInfo {
    String id;
    String email;
    String nickname;
}
