package net.spofo.auth.controller;

import static org.springframework.http.ResponseEntity.ok;

import lombok.RequiredArgsConstructor;
import net.spofo.auth.dto.request.AddMemberRequest;
import net.spofo.auth.dto.response.MemberResponse;
import net.spofo.auth.exception.SocialIdNotFound;
import net.spofo.auth.service.MemberService;
import net.spofo.auth.service.PublicKeyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MemberController {

    private final MemberService memberService;
    private final PublicKeyService publicKeyService;

    @GetMapping("/auth/members/search")
    public ResponseEntity<MemberResponse> verifyValidMember(
            @RequestHeader("authorization") String token) {
        MemberResponse memberResponse = publicKeyService.verifyToken(token);
        return ResponseEntity.ok(memberResponse);
    }

    @PutMapping("/auth/members")
    public ResponseEntity<MemberResponse> insertMember(@RequestBody AddMemberRequest request) {
        try {
            memberService.findBySocialId(request.getSocialId());
        } catch (SocialIdNotFound e) {
            MemberResponse savedMember = memberService.save(request);
            return ok().body(savedMember);
        }
        return ResponseEntity.noContent().build(); // 이미 있는 회원
    }

    @GetMapping("/test")
    @ResponseStatus(HttpStatus.OK)
    String getStatus() {
        return "저는 인증서버예요.";
    }

    @GetMapping("/test/callStock")
    @ResponseStatus(HttpStatus.OK)
    String getStock() {
        return memberService.getStock();
    }

    @GetMapping("/test/callPortfolio")
    @ResponseStatus(HttpStatus.OK)
    String getPortfolio() {
        return memberService.getPortfolio();
    }
}
