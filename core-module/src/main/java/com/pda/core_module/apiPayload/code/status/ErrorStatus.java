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
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "AUTH4001", "아이디 또는 비밀번호가 올바르지 않습니다"),
    DUPLICATE_NICKNAME(HttpStatus.UNAUTHORIZED, "USER4009", "이미 사용중인 닉네임입니다."),
    NO_FOLLOW_INFO(HttpStatus.NOT_FOUND, "USER4008", "팔로우 정보가 존재하지 않습니다"),
    ALREADY_FOLLOWING(HttpStatus.BAD_REQUEST, "USER4010", "이미 팔로우한 사용자입니다."), // 새로운 에러 추가,
    STOCK_ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "USER4011", "유저의 계좌가 존재하지않습니다."),
    NO_CHANGE_USER_STATUS(HttpStatus.UNAUTHORIZED, "USER4012", "인플루언서는 계좌를 공개해야 합니다."),
    NOT_GET_ACCOUNT(HttpStatus.UNAUTHORIZED, "USER4013", "계좌를 공개한 사람만 다른 사람의 계좌를 볼 수 있습니다."),
    DO_NOT_WANT_ACCOUNT(HttpStatus.NOT_FOUND, "USER4014", "계좌가 비공개인 유저입니다."),


    // 주식 관련 응답
    WATCHLIST_NOT_FOUND(HttpStatus.BAD_REQUEST, "STOCK4001", "watch list에 해당 주식이 없습니다."),
    STOCK_NOT_FOUND(HttpStatus.NOT_FOUND, "STOCK4002", "주식을 찾을수 없습니다."),
    OWN_STOCK_NOT_FOUND(HttpStatus.BAD_REQUEST, "STOCK4003", "보유주식 list에 해당 주식이 없습니다."),
    OWN_ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "STOCK4004", "계좌 계설을 먼저 하세요."),


    // 게시글 관련 응답
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "POST4002", "게시글을 찾을수 없습니다."),


    // 마일리지 관련 응답
    ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "MILEAGE4001", "계좌 계설을 먼저 하세요."),
    MILEAGE_NOT_ENOUGH(HttpStatus.UNAUTHORIZED, "MILEAGE4002", "마일리지가 부족합니다.")
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

