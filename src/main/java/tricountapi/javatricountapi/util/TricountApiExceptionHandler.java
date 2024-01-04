package tricountapi.javatricountapi.util;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tricountapi.javatricountapi.enums.TricountApiErrorCode;
import tricountapi.javatricountapi.model.ApiResponse;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class TricountApiExceptionHandler {
    @ExceptionHandler(TricountApiException.class)
    public ApiResponse<Object> sendtimeExceptionHandler(TricountApiException e,
            HttpServletResponse response) {
        TricountApiErrorCode errorCode = e.getErrorCode();
        response.setStatus(errorCode.getStatus());
        log.warn("sendtimeExceptionHandler", e);

        return new ApiResponse<>().fail(errorCode.getCode(), e.getMessage());
    }
}
