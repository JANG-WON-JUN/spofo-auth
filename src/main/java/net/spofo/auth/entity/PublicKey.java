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
/*
    참고
    @Table 어노테이션은 엔티티와 매핑되는 db table의 테이블명을 지정해주는데
    기본값은 엔티티의 클래스명과 동일합니다.
    따라서 이 경우에는 @Table 어노테이션을 붙여주지 않아도 됩니다.
 */
@Table(name = "publickey")
public class PublicKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 150, nullable = false)
    private String publickey;

    @Builder
    private PublicKey(String publickey) {
        this.publickey = publickey;
    }

}