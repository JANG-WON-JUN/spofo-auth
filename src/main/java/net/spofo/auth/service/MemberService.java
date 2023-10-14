package net.spofo.auth.service;

import lombok.RequiredArgsConstructor;
import net.spofo.auth.dto.request.AddMemberRequest;
import net.spofo.auth.dto.response.MemberResponse;
import net.spofo.auth.entity.Member;
import net.spofo.auth.exception.SocialIdNotFound;
import net.spofo.auth.repository.MemberRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberResponse findBySocialId(String socialId) {
        Member member = memberRepository.findBySocialId(socialId)
                // 참고
                // 만약 다른 곳에서도 SocialIdNotFound를 사용한다면 같은 에러 메세지를 2번 작성해야 합니다.
                // SocialIdNotFound의 에러 메세지를 공통으로 처리할 수 있는 방법을 생각해보면 좋을 것 같아요~
                .orElseThrow(() -> new SocialIdNotFound("id를 찾을 수 없습니다."));

        return MemberResponse.from(member);
    }

    public MemberResponse save(AddMemberRequest request) {
        Member member = memberRepository.save(request.toEntity());
        return MemberResponse.from(member);
    }
}