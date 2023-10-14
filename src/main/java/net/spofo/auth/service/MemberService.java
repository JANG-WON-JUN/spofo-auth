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
                .orElseThrow(() -> new SocialIdNotFound("id를 찾을 수 없습니다."));

        return MemberResponse.from(member);
    }

    public MemberResponse save(AddMemberRequest request) {
        Member member = memberRepository.save(request.toEntity());
        return MemberResponse.from(member);
    }
}