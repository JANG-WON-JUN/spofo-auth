package net.spofo.auth.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import net.spofo.auth.dto.response.MemberResponse;
import net.spofo.auth.entity.PublicKey;
import net.spofo.auth.exception.InvalidToken;
import net.spofo.auth.repository.PublicKeyRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@RequiredArgsConstructor
@Service
public class PublicKeyService {

    private final MemberService memberService;
    private final PublicKeyRepository publicKeyRepository;
    private final RestClient restClient;

    /*
        참고.
        지금은 카카오 1개 뿐이지만, 만약 네이버와 구글 로그인이 추가된다면?
        따라서 카카오의 URL, 네이버의 URL, 구글의 URL을 따로 관리해야 할 수 있습니다.
        그러한 경우 enum으로 관리할 수 있으므로 생각해보시면 좋을 것 같아요
     */
    private final String issuer = "https://kauth.kakao.com";
    private final String KAKAO_PUBLIC_KEY_URL = "https://kauth.kakao.com/.well-known/jwks.json";


    @Value("${auth.kakao.clientid}")
    private String appKey;

    public MemberResponse verifyToken(String token) { // 토큰 검증
        DecodedJWT jwtOrigin = verifyValidation(token);

        if (verifySignature(jwtOrigin) == false) { // 토큰의 공개키가 유효하지 않다면 DB를 업데이트하거나 실패라고 알려주거나
            getKakaoPublicKeys(token); // 예외 발생 없이 잘 돌아오면 정상적인 토큰. (db가 업데이트 된 상태이므로 한 번 더 서명 검증 필요)
            if (verifySignature(jwtOrigin) == false) {
                throw new InvalidToken("유효하지 않은 토큰(공개키 불일치)");
            }
        }
        // 토큰 검증 완료!
        String socialId = jwtOrigin.getSubject();
        MemberResponse memberResponse = memberService.findBySocialId(socialId);
        return memberResponse;
    }

    private boolean verifySignature(DecodedJWT jwtOrigin) { // 토큰의 공개키와 비교하여 서명 검증
        List<PublicKey> storedPublicKey = loadPublicKey();

        for (int i = 0; i < storedPublicKey.size(); i++) {
            if (jwtOrigin.getKeyId().equals(storedPublicKey.get(i).getPublickey())) {
                return true; // 토큰의 공개키가 유효함.
            }
        }
        return false;
    }

    /*
        참고
        token 매개변수가 사용되지 않는데 참고 부탁드려요.
     */
    private void getKakaoPublicKeys(String token) {
        // 카카오 공개키 목록 가져오기
        ResponseEntity response = restClient.get()
                .uri(KAKAO_PUBLIC_KEY_URL)
                .retrieve()
                /*
                    참고
                    RestClient의 결과값을 받을 때 정형화된 값이라면
                    String으로 받고 JSONObject를 사용해 직접 파싱하는 것이 아닌
                    바로 자바 클래스에 매핑 해줄 수 있습니다.

                    ex) toEntity(MyClass.class);
                 */
                .toEntity(String.class);

        String kidJson = response.getBody().toString();
        List<String> publicKeyList = new ArrayList<>();
        List<PublicKey> storedPublicKeyList = loadPublicKey();

        try {
            // 1. 데이터 파싱
            JSONObject jsonObject = new JSONObject(kidJson);
            JSONArray keysArray = jsonObject.getJSONArray("keys");

            // 2. 파싱한 데이터로 리스트 만들기
            for (int i = 0; i < keysArray.length(); i++) {
                JSONObject keyObject = keysArray.getJSONObject(i);
                String kid = keyObject.getString("kid");
                publicKeyList.add(kid);
            }
        } catch (Exception e) { //JSONExecption
            throw new InvalidToken("잘못된 JSON 입니다.");
        }

        // 만약 불러온 pk와 저장된 pk가 다르다면 공개키가 업데이트 된 것이므로 DB 업데이트
        if (!matchPublicKey(publicKeyList, storedPublicKeyList)) {
            saveNewPublicKey(publicKeyList);
        }
    }

    private boolean matchPublicKey(List<String> publicKeyList,
            List<PublicKey> storedPublicKeyList) {
        for (int i = 0; i < publicKeyList.size(); i++) {
            for (int j = 0; j < storedPublicKeyList.size(); j++) {
                if (publicKeyList.get(i).equals(storedPublicKeyList.get(j).getPublickey())) {
                    throw new InvalidToken("유효하지 않은 토큰입니다.");
                }
            }
        }
        return false;
    }

    private void saveNewPublicKey(List<String> publicKeyList) {
        deleteAllPublicKey();
        publicKeyList.stream()
                // 참고
                // 정적 팩터리 메서드를 사용하도록 하면 좋습니다! (아래 링크 참고)
                // https://github.com/TaemHam/effective-java-study/tree/main/2%EC%9E%A5/%EC%95%84%EC%9D%B4%ED%85%9C01
                .map(PublicKey::from) // 각 요소를 PublicKey 객체로 변환
                .forEach(this::savePublicKey); // 각 PublicKey를 저장
    }

    private DecodedJWT verifyValidation(String token) {
        DecodedJWT jwtOrigin = JWT.decode(token);

        Optional<DecodedJWT> validationResult = Optional.of(jwtOrigin)
                .filter(jwt -> jwt.getIssuer().equals(issuer))
                .filter(jwt -> jwt.getAudience().get(0).equals(appKey))
                .filter(jwt -> !jwt.getExpiresAt().before(new Date()));

        return validationResult.orElseThrow(() -> new InvalidToken("토큰이 유효하지 않습니다."));
    }

    private PublicKey savePublicKey(PublicKey publicKey) {
        return publicKeyRepository.save(publicKey);
    }

    // 참고
    // deleteAllPublicKey, loadPublicKey는 왜 별도의 메서드로 작성하셨나요?
    // 1줄의 코드를 별도의 메서드로 작성하는 경우의 이점을 생각해보시고
    // 코드를 인라인 해도 무방하면 아래 2개의 메서드는 빼도 좋을 것 같아요
    private void deleteAllPublicKey() {
        publicKeyRepository.deleteAllInBatch();
    }

    private List<PublicKey> loadPublicKey() {
        return publicKeyRepository.findAll();
    }
}