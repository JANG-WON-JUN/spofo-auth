package net.spofo.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "Member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false)
    private String platform;

    @Column(length = 500, nullable = false)
    private String socialId;

    /*
        참고.
        @Builder를 생성할 때 생성자 위에 붙여주는 경우,
        생성자의 접근제한자를 private로 선언하여
        @Builder & 생성자로 만들 수 있었던 Member 클래스를
        @Builder만 사용하도록 제한할 수 있습니다.
        (@Builder를 사용하는 이유 생각해보기!)

        또 생성자를 굳이 선언하여 @Builder를 붙여준 이유도 생각해보면 좋을 것 같아요.
     */
    @Builder
    private Member(String platform, String socialId) {
        this.platform = platform;
        this.socialId = socialId;
    }
}
