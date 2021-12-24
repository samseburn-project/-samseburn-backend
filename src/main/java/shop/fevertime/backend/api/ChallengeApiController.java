package shop.fevertime.backend.api;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import shop.fevertime.backend.dto.request.ChallengeRequestDto;
import shop.fevertime.backend.dto.request.ChallengeUpdateRequestDto;
import shop.fevertime.backend.dto.response.ChallengeResponseDto;
import shop.fevertime.backend.dto.response.ChallengeResponseWithTotalCountDto;
import shop.fevertime.backend.dto.response.ResultResponseDto;
import shop.fevertime.backend.security.UserDetailsImpl;
import shop.fevertime.backend.service.ChallengeService;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChallengeApiController {

    private final ChallengeService challengeService;

    /**
     * 카테고리별 챌린지 조회 API
     */
    @GetMapping("/challenges")
    public ChallengeResponseWithTotalCountDto getChallenges(
            @RequestParam("kind") String category,
            @RequestParam("page") int page,
            @RequestParam("sortBy") String sortBy) {
        return challengeService.getChallenges(category, page, sortBy);
    }

    /**
     * 챌린지 검색 API
     */
    @GetMapping("/challenges/search")
    public ChallengeResponseWithTotalCountDto searchChallenges(
            @RequestParam("search") String search,
            @RequestParam("page") int page) {
        return challengeService.searchChallenges(search, page);
    }

    /**
     * 챌린지 상세 조회 API
     */
    @GetMapping("/challenges/{challengeId}")
    public ChallengeResponseDto getChallenge(@PathVariable Long challengeId) {
        return challengeService.getChallenge(challengeId);
    }

    /**
     * 챌린지 생성 API
     */
    @PostMapping("/challenge")
    public ResultResponseDto createChallenge(
            @ModelAttribute ChallengeRequestDto requestDto,
            @RequestParam(required = false) MultipartFile image,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) throws IOException {
        challengeService.createChallenge(requestDto, userDetails.getUser(), image);
        return new ResultResponseDto("success", "챌린지 생성되었습니다.");
    }

    /**
     * 챌린지 수정 API
     */
    @PutMapping("/challenges/{challengeId}")
    public ResultResponseDto updateChallenge(
            @PathVariable Long challengeId,
            @ModelAttribute ChallengeUpdateRequestDto requestDto,
            @RequestParam(required = false) MultipartFile image,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) throws IOException {
        challengeService.updateChallenge(challengeId, requestDto, userDetails.getUser(), image);
        return new ResultResponseDto("success", "챌린지 수정되었습니다.");
    }

    /**
     * 챌린지 삭제 API
     */
    @DeleteMapping("/challenges/{challengeId}")
    public ResultResponseDto deleteChallenge(@PathVariable Long challengeId,
                                             @AuthenticationPrincipal UserDetailsImpl userDetails) {
        challengeService.deleteChallenge(challengeId, userDetails.getUser());
        return new ResultResponseDto("success", "챌린지 삭제되었습니다.");
    }

    /**
     * 챌린지 생성자 확인 API
     */
    @GetMapping("/user/challenge/{challengeId}")
    public ResultResponseDto checkChallengeCreator(
            @PathVariable Long challengeId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return challengeService.checkChallengeCreator(challengeId, userDetails.getUser());
    }
}
