package net.spofo.auth.exception;

import net.spofo.auth.exception.controller.ExceptionController;
import org.springframework.http.HttpStatus;

/**
 * 참고. RuntimeException을 상속받은 프로젝트의 공통 Exception을 만듭니다.
 * 그리고 모든 Exception은 프로젝트의 공통 Exception인 AuthServerException를 상속 받습니다.
 * 그러면 ExceptionHandler에서 AuthServerException으로 공통에러 처리를 할 수 있으며
 * 추가적으로 상세한 예외 처리를 하고 싶다면 Exception Advice에 추가하면 됩니다.
 *
 * @see ExceptionController
 */
public abstract class AuthServerException extends RuntimeException {

    // 참고. 각 예외에 status 코드를 지정할 수 있도록 추상 메서드를 만듭니다.
    public abstract HttpStatus getStatusCode();

    public AuthServerException(String message) {
        super(message);
    }
}
