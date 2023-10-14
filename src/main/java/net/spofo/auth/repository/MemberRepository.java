package net.spofo.auth.repository;

import java.util.Optional;
import net.spofo.auth.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    /*
        참고
        이 메서드가 왜 동작하는지 공부하면 좋을 것 같아요!
        (키워드 : Spring Data JPA 쿼리 메소드)
     */
    Optional<Member> findBySocialId(String socialId);
}