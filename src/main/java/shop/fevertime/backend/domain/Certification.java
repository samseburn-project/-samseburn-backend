package shop.fevertime.backend.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.fevertime.backend.util.CertificationValidator;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Certification extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "certification_id")
    private Long id;

    @Column(nullable = false)
    private String imgUrl;

    @Column(nullable = false)
    private String contents;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    public Certification(String imgUrl, String contents, User user, Challenge challenge) {
        CertificationValidator.validateCreate(user, imgUrl, contents, challenge);
        this.imgUrl = imgUrl;
        this.contents = contents;
        this.user = user;
        this.challenge = challenge;
    }

    public void update(String contents) {
        CertificationValidator.validateUpdatContents(contents);
        this.contents = contents;
    }
}
