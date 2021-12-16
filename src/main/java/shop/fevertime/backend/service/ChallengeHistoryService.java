package shop.fevertime.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.fevertime.backend.domain.*;
import shop.fevertime.backend.dto.response.*;
import shop.fevertime.backend.exception.ApiRequestException;
import shop.fevertime.backend.repository.CertificationRepository;
import shop.fevertime.backend.repository.ChallengeHistoryRepository;
import shop.fevertime.backend.repository.ChallengeRepository;
import shop.fevertime.backend.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChallengeHistoryService {

    private final ChallengeHistoryRepository challengeHistoryRepository;
    private final ChallengeRepository challengeRepository;
    private final CertificationRepository certificationRepository;
    private final UserRepository userRepository;
    private final CertificationService certificationService;

    @Transactional
    public ChallengeUserResponseDto getChallengeHistoryUser(Long challengeId, User user) {
        // 챌린지 찾기
        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(
                () -> new ApiRequestException("해당 챌린지를 찾을 수 없습니다.")
        );
        // 유저가 챌린지 인증한 리스트 찾기
        List<CertificationResponseDto> certifies = certificationRepository.findAllByChallengeAndUser(challenge, user).stream()
                .map(CertificationResponseDto::new)
                .collect(Collectors.toList());
        // 챌린지 참여 내역
        List<ChallengeHistoryResponseDto> userHistories = challengeHistoryRepository.findAllByChallengeAndUser(challenge, user).stream()
                .map(ChallengeHistoryResponseDto::new)
                .collect(Collectors.toList());

        return new ChallengeUserResponseDto(user, certifies, userHistories);
    }

    @Transactional
    public List<UserCertifiesResponseDto> getChallengeHistoryUsers(Long challengeId) {
        // 챌린지 찾기
        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(
                () -> new ApiRequestException("해당 챌린지를 찾을 수 없습니다.")
        );
        //히스토리에서 해당 챌린지에 조인한 데이터 가져옴 -> 해당 유저 리스트 가져옴
        List<ChallengeHistory> status = challengeHistoryRepository.findAllByChallengeAndChallengeStatus(challenge, ChallengeStatus.JOIN);

        List<UserChallengeStatusResponseDto> userList = new ArrayList<>();

        for (ChallengeHistory history : status) {
            userList.add(new UserChallengeStatusResponseDto(history.getUser(), history.getFirstWeekMission()));
        }
        //찾아온 히스토리 데이터를 유저 리스트에 유저아이디로 조인해서 1주차 미션 여부 가져와야할듯?!
        return userList.stream().
                map(UserChallengeStatusDto -> new UserCertifiesResponseDto(UserChallengeStatusDto.getUser(),
                        UserChallengeStatusDto.getUser().getCertificationList(),
                        UserChallengeStatusDto.getFirstWeekMission()))
                .collect(Collectors.toList());

//        return userRepository.findAllCertifiesByChallenge(challenge).stream()
//                .map(user -> new UserCertifiesResponseDto(user, user.getCertificationList()))
//                .collect(Collectors.toList());
    }

    @Transactional
    public void joinChallenge(Long challengeId, User user) throws ApiRequestException {
        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(
                () -> new ApiRequestException("해당 챌린지를 찾을 수 없습니다.")
        );

        if (challenge.getChallengeProgress() == ChallengeProgress.STOP) {
            throw new ApiRequestException("종료된 챌린지에 참여할 수 없습니다.");
        }

        //해당 챌린지와 유저로 히스토리 찾아와서 fail 갯수 가져오기
        List<ChallengeHistory> user1 = challengeHistoryRepository.findAllByChallengeAndUserAndChallengeStatus(challenge, user, ChallengeStatus.FAIL);
        if (user1.size() >= 3) throw new ApiRequestException("챌린지에 참여할 수 없습니다.");

        LocalDateTime now = LocalDateTime.now();
        ChallengeHistory challengeHistory = new ChallengeHistory(
                user,
                challenge,
                now,
                now.plusDays(7),
                ChallengeStatus.JOIN,
                FirstWeekMission.NO);

        challengeHistoryRepository.save(challengeHistory);
    }

    @Transactional
    public void cancelChallenge(Long challengeId, User user) {
        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(
                () -> new ApiRequestException("해당 챌린지를 찾을 수 없습니다.")
        );

        if (challenge.getChallengeProgress() == ChallengeProgress.STOP) {
            throw new ApiRequestException("종료된 챌린지에 참여 취소할 수 없습니다.");
        }

        ChallengeHistory challengeHistory = challengeHistoryRepository.findChallengeHistoryByChallengeStatusEquals(
                ChallengeStatus.JOIN,
                user,
                challenge).orElseThrow(
                () -> new ApiRequestException("해당 챌린지를 참여중인 기록이 없습니다.")
        );

        certificationRepository.findAllByChallengeAndUser(challenge, user)
                .forEach(certi -> certificationService.deleteCertification(certi.getId(), user));

        challengeHistory.cancel();
    }

    // 작업중
    public List<UserChallengeInfoDto> getChallengesByUser(User user) {

        return challengeHistoryRepository.findAllByUser(user).stream()
                .map(challengeHistory -> new UserChallengeInfoDto(
                        challengeHistory.getChallenge(), challengeHistory,
                        certificationRepository.findAllByChallengeAndUser(challengeHistory.getChallenge(), user),
                        challengeHistoryRepository.countChallengeHistoriesByChallengeAndUserAndChallengeStatus(challengeHistory.getChallenge(), user, ChallengeStatus.FAIL)

                )).collect(Collectors.toList());
    }
}
