package net.spofo.auth.exception.advice;

import static org.springframework.http.ResponseEntity.status;

import net.spofo.auth.exception.AuthServerException;
import net.spofo.auth.exception.dto.ErrorResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/*
    참고.
    RestControllerAdvice를 사용하여 @ResponseBody를 사용하지 않아도
    응답을 JSON 형태로 변경하여 반환할 수 있습니다.

    @ControllerAdvice와 @RestControllerAdvice는
    @Controller와 @RestController과 같은 구조입니다.
 */
@RestControllerAdvice
public class ExControllerAdvice {

    /*
        참고.
        아래 주석처리 된 2개의 메서드는 각 예외마다 처리할 수 있는 메서드입니다.
        그러나 SocialIdNotFound, InvalidToken 클래스가 AuthServerException를 상속받음으로써
        아래의 commonExHandler 1개의 메서드로 공통된 예외 처리를 할 수 있습니다.
        만약 SocialIdNotFound를 위한 독자적인 예외처리 로직이 필요하다면
        socialIdExHandler 메서드를 만들어 처리하기만 하면 됩니다.
     */
    @ExceptionHandler
    public ResponseEntity<ErrorResult> commonExHandler(AuthServerException e) {
        ErrorResult errorResult = ErrorResult.builder()
                .errorCode(e.getStatusCode())
                .errorMessage(e.getMessage())
                .build();

        return status(e.getStatusCode()).body(errorResult);
    }

    /*
    @ExceptionHandler
    public ResponseEntity<ErrorResult> invalidTokenExHandler(InvalidToken e) {
        String errorMessage = e.getMessage();
        ErrorResult errorResult = ErrorResult.builder()
                .errorCode(BAD_REQUEST)
                .errorMessage(errorMessage)
                .build();
        return new ResponseEntity<>(errorResult, BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResult> socialIdExHandler(SocialIdNotFound e) {
        String errorMessage = e.getMessage();
        ErrorResult errorResult = ErrorResult.builder()
                .errorCode(BAD_REQUEST)
                .errorMessage(errorMessage)
                .build();
        return new ResponseEntity<>(errorResult, BAD_REQUEST);
    }
     */
}
