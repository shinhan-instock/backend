package com.pda.core_module.apiPayload.code.status;



import com.pda.core_module.apiPayload.code.BaseErrorCode;
import com.pda.core_module.apiPayload.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum
ErrorStatus implements BaseErrorCode {


    // 가장 일반적인 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON5000", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON4000", "잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON4001", "인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON4003", "금지된 요청입니다."),

    // 사용자 관련 응답
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER4001", "사용자를 찾을수 없습니다."),

    // 주식 관련 응답
    WATCHLIST_NOT_FOUND(HttpStatus.BAD_REQUEST, "STOCK4001", "watch list에 해당 주식이 없습니다."),
    STOCK_NOT_FOUND(HttpStatus.NOT_FOUND, "STOCK4002", "주식을 찾을수 없습니다."),

    // 게시글 관련 응답
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "POST4002", "게시글을 찾을수 없습니다."),
    ;




    private final HttpStatus httpStatus;
    private final String code;
    private final String message;


    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build()
                ;


    }
}

