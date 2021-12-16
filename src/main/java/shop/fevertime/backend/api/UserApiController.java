package shop.fevertime.backend.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import shop.fevertime.backend.dto.request.UserRequestDto;
import shop.fevertime.backend.dto.response.*;
import shop.fevertime.backend.dto.request.SocialLoginRequestDto;
import shop.fevertime.backend.security.UserDetailsImpl;
import shop.fevertime.backend.service.ChallengeHistoryService;
import shop.fevertime.backend.service.UserService;
import shop.fevertime.backend.util.JwtTokenUtil;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RestController
@Slf4j
public class UserApiController {

    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsService userDetailsService;
    private final UserService userService;
    private final ChallengeHistoryService challengeHistoryService;

    /**
     * 유저 카카오 로그인 API
     */
    @PostMapping(value = "/login/kakao")
    public ResponseEntity<?> createAuthenticationTokenByKakao(@RequestBody SocialLoginRequestDto socialLoginDto) throws Exception {
        String kakaoId = userService.kakaoLogin(socialLoginDto.getToken());
        final UserDetails userDetails = userDetailsService.loadUserByUsername(kakaoId);
        final String token = jwtTokenUtil.generateToken(userDetails);
        return ResponseEntity.ok(new JwtResponse(token, userDetails.getUsername()));
    }

    /**
     * 유저 정보 조회 API
     */
    @GetMapping("/user")
    public UserResponseDto getUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return new UserResponseDto(userDetails.getUser());
    }

    /**
     * 유저가 생성한 챌린지 리스트 API
     */
    @GetMapping("/user/challenges/create")
    public List<UserChallengeResponseDto> getUserChallenges(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.getChallenges(userDetails.getUser());
    }

    /**
     * 유저 정보 변경 API
     */
    @PutMapping("/user")
    public ResultResponseDto updateUser(@ModelAttribute UserRequestDto requestDto,
                                        @RequestParam(required = false) MultipartFile image,
                                        @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        userService.updateUser(userDetails.getUser(), requestDto, image);
        return new ResultResponseDto("success", "유저 정보가 수정되었습니다.");
    }

    /**
     * 유저가 참여중인 챌린지 리스트 API
     */
    @GetMapping("/user/challenges")
    public List<UserChallengeInfoDto> getChallengesByUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return challengeHistoryService.getChallengesByUser(userDetails.getUser());
    }
}
