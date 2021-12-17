package shop.fevertime.backend.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import shop.fevertime.backend.exception.ApiRequestException;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Nested
    @DisplayName("유저 객체 생성")
    class CreateUser {

        private String username;
        private String email;
        private UserRole role;
        private String kakaoId;
        private String imgUrl;

        @BeforeEach
        void setup() {
            username = "test";
            email = "tes@email.com";
            role = UserRole.USER;
            kakaoId = "12456";
            imgUrl = "http://img.com/img";
        }

        @Test
        @DisplayName("정상 케이스")
        void create_Normal() {
            // given

            // when
            User user = new User(username, email, role, kakaoId, imgUrl);
            // then
            assertThat(user.getId()).isNull();
            assertThat(user.getUsername()).isEqualTo(username);
            assertThat(user.getEmail()).isEqualTo(email);
            assertThat(user.getRole()).isEqualTo(role);
            assertThat(user.getKakaoId()).isEqualTo(kakaoId);
            assertThat(user.getImgUrl()).isEqualTo(imgUrl);
        }

        @Nested
        @DisplayName("실패 케이스")
        class FailCases {

            @Nested
            @DisplayName("유저 이름")
            class Username {

                @Test
                @DisplayName("null")
                void fail_null() {
                    // given
                    username = null;
                    // when
                    Exception exception = assertThrows(ApiRequestException.class,
                            () -> new User(username, email, role, kakaoId, imgUrl));
                    // then
                    assertThat(exception.getMessage()).isEqualTo("입력된 유저 이름이 없습니다.");
                }

                @Test
                @DisplayName("공백")
                void fail_empty() {
                    // given
                    username = "";
                    // when
                    Exception exception = assertThrows(ApiRequestException.class,
                            () -> new User(username, email, role, kakaoId, imgUrl));
                    // then
                    assertThat(exception.getMessage()).isEqualTo("입력된 유저 이름이 없습니다.");
                }

                @Test
                @DisplayName("이름 아홉글자")
                void fail_length_nine() {
                    // given
                    username = "일이삼사오육칠팔구";
                    // when
                    Exception exception = assertThrows(ApiRequestException.class,
                            () -> new User(username, email, role, kakaoId, imgUrl));
                    // then
                    assertThat(exception.getMessage()).isEqualTo("유저 이름은 1~8자 사이로 입력하세요.");
                }
            }

            @Nested
            @DisplayName("유저 이메일")
            class Email {

                @Test
                @DisplayName("null")
                void fail_null() {
                    // given
                    email = null;
                    // when
                    Exception exception = assertThrows(ApiRequestException.class,
                            () -> new User(username, email, role, kakaoId, imgUrl));
                    // then
                    assertThat(exception.getMessage()).isEqualTo("이메일이 없습니다.");
                }
            }

            @Nested
            @DisplayName("유저 권한")
            class Role {

                @Test
                @DisplayName("null")
                void fail_null() {
                    // given
                    role = null;
                    // when
                    Exception exception = assertThrows(ApiRequestException.class,
                            () -> new User(username, email, role, kakaoId, imgUrl));
                    // then
                    assertThat(exception.getMessage()).isEqualTo("유저 권한이 없습니다.");
                }
            }

            @Nested
            @DisplayName("유저 카카오아이디")
            class KakaoId {

                @Test
                @DisplayName("null")
                void fail_null() {
                    // given
                    kakaoId = null;
                    // when
                    Exception exception = assertThrows(ApiRequestException.class,
                            () -> new User(username, email, role, kakaoId, imgUrl));
                    // then
                    assertThat(exception.getMessage()).isEqualTo("카카오 아이디 값이 없습니다.");
                }

                @Test
                @DisplayName("공백")
                void fail_empty() {
                    // given
                    kakaoId = "";
                    // when
                    Exception exception = assertThrows(ApiRequestException.class,
                            () -> new User(username, email, role, kakaoId, imgUrl));
                    // then
                    assertThat(exception.getMessage()).isEqualTo("카카오 아이디 값이 없습니다.");
                }
            }

            @Nested
            @DisplayName("유저 이미지 링크")
            class imgUrl {

                @Test
                @DisplayName("null")
                void fail_null() {
                    // given
                    imgUrl = null;
                    // when
                    Exception exception = assertThrows(ApiRequestException.class,
                            () -> new User(username, email, role, kakaoId, imgUrl));
                    // then
                    assertThat(exception.getMessage()).isEqualTo("이미지 링크를 확인해주세요.");
                }

                @Test
                @DisplayName("공백")
                void fail_empty() {
                    // given
                    imgUrl = "";
                    // when
                    Exception exception = assertThrows(ApiRequestException.class,
                            () -> new User(username, email, role, kakaoId, imgUrl));
                    // then
                    assertThat(exception.getMessage()).isEqualTo("이미지 링크를 확인해주세요.");
                }

                @Test
                @DisplayName("URL 형식")
                void fail_url_form() {
                    // given
                    imgUrl = "test_email";
                    // when
                    Exception exception = assertThrows(ApiRequestException.class,
                            () -> new User(username, email, role, kakaoId, imgUrl));
                    // then
                    assertThat(exception.getMessage()).isEqualTo("이미지 링크를 확인해주세요.");
                }
            }
        }
    }

    @Nested
    @DisplayName("유저 객체 수정")
    class Update {

        private String username;
        private String email;
        private UserRole role;
        private String kakaoId;
        private String imgUrl;

        private String updateUsername;
        private String updateimgUrl;

        @BeforeEach
        void setup() {
            username = "test";
            email = "tes@email.com";
            role = UserRole.USER;
            kakaoId = "12456";
            imgUrl = "http://img.com/img";

            updateUsername = "update";
            updateimgUrl = "https://img.com/update";
        }

        @Test
        @DisplayName("정상 케이스")
        void update_Normal() {
            // given
            User user = new User(username, email, role, kakaoId, imgUrl);
            // when
            user.updateUsername(updateUsername);
            // then
            assertThat(user.getId()).isNull();
            assertThat(user.getUsername()).isEqualTo(updateUsername);
            assertThat(user.getEmail()).isEqualTo(email);
            assertThat(user.getRole()).isEqualTo(role);
            assertThat(user.getKakaoId()).isEqualTo(kakaoId);
        }

        @Nested
        @DisplayName("실패 케이스")
        class FailCases {

            @Nested
            @DisplayName("유저 이름")
            class Username {

                @Test
                @DisplayName("null")
                void fail_null() {
                    // given
                    updateUsername = null;
                    User user = new User(username, email, role, kakaoId, imgUrl);
                    // when
                    Exception exception = assertThrows(ApiRequestException.class,
                            () -> user.updateUsername(updateUsername));
                    // then
                    assertThat(exception.getMessage()).isEqualTo("입력된 유저 이름이 없습니다.");
                }

                @Test
                @DisplayName("공백")
                void fail_empty() {
                    // given
                    updateUsername = "";
                    User user = new User(username, email, role, kakaoId, imgUrl);
                    // when
                    Exception exception = assertThrows(ApiRequestException.class,
                            () -> user.updateUsername(updateUsername));
                    // then
                    assertThat(exception.getMessage()).isEqualTo("입력된 유저 이름이 없습니다.");
                }

                @Test
                @DisplayName("유저 이름 아홉글자")
                void fail_length_nine() {
                    // given
                    updateUsername = "일이삼사오육칠팔구";
                    User user = new User(username, email, role, kakaoId, imgUrl);
                    // when
                    Exception exception = assertThrows(ApiRequestException.class,
                            () -> user.updateUsername(updateUsername));
                    // then
                    assertThat(exception.getMessage()).isEqualTo("유저 이름은 1~8자 사이로 입력하세요.");
                }
            }

            @Nested
            @DisplayName("유저 이미지 링크")
            class imgUrl {

                @Test
                @DisplayName("null")
                void fail_null() {
                    // given
                    updateimgUrl = null;
                    User user = new User(username, email, role, kakaoId, imgUrl);
                    // when
                    Exception exception = assertThrows(ApiRequestException.class,
                            () -> user.updateUser(updateimgUrl, user.getUsername()));
                    // then
                    assertThat(exception.getMessage()).isEqualTo("이미지 링크를 확인해주세요.");
                }


                @Test
                @DisplayName("URL 형식")
                void fail_url_form() {
                    // given
                    updateimgUrl = "test_email";
                    User user = new User(username, email, role, kakaoId, imgUrl);
                    // when
                    Exception exception = assertThrows(ApiRequestException.class,
                            () -> user.updateUser(updateimgUrl, user.getUsername()));
                    // then
                    assertThat(exception.getMessage()).isEqualTo("이미지 링크를 확인해주세요.");
                }
            }
        }
    }
}