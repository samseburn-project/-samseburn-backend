package shop.fevertime.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import shop.fevertime.backend.domain.User;
import shop.fevertime.backend.domain.UserRole;
import shop.fevertime.backend.dto.request.UserRequestDto;

import shop.fevertime.backend.dto.response.UserChallengeResponseDto;
import shop.fevertime.backend.exception.ApiRequestException;
import shop.fevertime.backend.repository.ChallengeRepository;
import shop.fevertime.backend.repository.UserRepository;
import shop.fevertime.backend.security.kakao.KakaoOAuth2;
import shop.fevertime.backend.security.kakao.KakaoUserInfo;
import shop.fevertime.backend.util.S3Uploader;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final KakaoOAuth2 kakaoOAuth2;
    private final S3Uploader s3Uploader;
    private final ChallengeRepository challengeRepository;

    public String kakaoLogin(String token) {
        // 카카오 OAuth2 를 통해 카카오 사용자 정보 조회
        KakaoUserInfo userInfo = kakaoOAuth2.getUserInfo(token);
        String kakaoId = userInfo.getId();
        String nickname = userInfo.getNickname();
        String email = userInfo.getEmail();
        String defaultimgUrl = "https://fever-prac.s3.ap-northeast-2.amazonaws.com/user/SpartaIconScale7.png";

        // DB 에 중복된 Kakao Id 가 있는지 확인
        User kakaoUser = userRepository.findByKakaoId(kakaoId)
                .orElse(null);

        // 카카오 정보로 회원가입
        if (kakaoUser == null) {
            kakaoUser = new User(nickname, email, UserRole.USER, kakaoId, defaultimgUrl);
            userRepository.save(kakaoUser);
        }

        return kakaoId;
    }

    @Transactional
    public List<UserChallengeResponseDto> getChallenges(User user) {
        return challengeRepository.findAllByUser(user).stream()
                .map(UserChallengeResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateUser(User user, UserRequestDto requestDto, MultipartFile image) throws IOException {
        User findUser = userRepository.findById(user.getId()).orElseThrow(
                () -> new ApiRequestException("해당 아이디가 존재하지 않습니다.")
        );
        //이미지가 null일때 유저이름만 업데이트
        if (image == null) {
            findUser.updateUsername(requestDto.getUsername());
            return;
        }
        // 기존 이미지 S3에서 삭제 (기본 이미지 아닐 경우만 )
        if (!Objects.equals(findUser.getImgUrl(), "https://fever-prac.s3.ap-northeast-2.amazonaws.com/user/SpartaIconScale7.png")) {
            String[] ar = findUser.getImgUrl().split("/");
            s3Uploader.delete(ar[ar.length - 1], "user");
        }

        // 이미지 AWS S3 업로드
        String uploadImageUrl = s3Uploader.upload(image, "user");

        findUser.updateUser(uploadImageUrl, requestDto.getUsername());

    }
}
