package shop.fevertime.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.fevertime.backend.domain.Challenge;
import shop.fevertime.backend.domain.ChallengeHistory;
import shop.fevertime.backend.domain.ChallengeStatus;
import shop.fevertime.backend.domain.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ChallengeHistoryRepository extends JpaRepository<ChallengeHistory, Long> {

    @Query("select ch " + "from ChallengeHistory ch " + "where ch.challengeStatus =:challengeStatus " + "and ch.user =:user " + "and ch.challenge =:challenge")
    Optional<ChallengeHistory> findChallengeHistoryByChallengeStatusEquals(@Param("challengeStatus") ChallengeStatus challengeStatus, @Param("user") User user, @Param("challenge") Challenge challenge);

    List<ChallengeHistory> findAllByChallengeAndUser(Challenge challenge, User user);

    ChallengeHistory findByChallengeAndUser(Challenge challenge, User user);

    int countChallengeHistoriesByChallengeAndUserAndChallengeStatus(Challenge challenge, User user, ChallengeStatus challengeStatus);

    long countDistinctUserByChallengeAndChallengeStatus(Challenge challenge, ChallengeStatus challengeStatus);

    List<ChallengeHistory> findAllByChallengeAndChallengeStatus(Challenge challenge, ChallengeStatus challengeStatus);

    List<ChallengeHistory> findAllByUser(User user);

    List<ChallengeHistory> findAllByChallengeAndChallengeStatusAndUserNot(Challenge challenge, ChallengeStatus challengeStatus, User user);

    void deleteAllByChallenge(Challenge challenge);

    List<ChallengeHistory> findAllByMissionDateBefore(LocalDate missionDate);

}
