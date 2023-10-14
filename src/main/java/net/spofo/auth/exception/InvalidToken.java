package net.spofo.auth.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import org.springframework.http.HttpStatus;

// 참고. 보다 간결하면서도 예외를 나타낼 수 있는 이름으로 변경했습니다.
public class InvalidToken extends AuthServerException {

    public InvalidToken(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatusCode() {
        return BAD_REQUEST;
    }
}
