package tricountapi.javatricountapi.enums;

import jakarta.servlet.http.HttpServletResponse;

public enum TricountApiErrorCode {

    INVALID_INPUT_VALUE(HttpServletResponse.SC_BAD_REQUEST, 4001, "invalid input value"),
    ENTITY_NOT_FOUND(HttpServletResponse.SC_BAD_REQUEST, 4002, "entity not found"),
    INVALID_TYPE_VALUE(HttpServletResponse.SC_BAD_REQUEST, 4003, "invalid type value"),
    ACCESS_DENIED(HttpServletResponse.SC_BAD_REQUEST, 4004, "access is denied"),
    NOT_FOUND(HttpServletResponse.SC_NOT_FOUND, 4005, "not found"),
    METHOD_NOT_ALLOWED(HttpServletResponse.SC_METHOD_NOT_ALLOWED, 4006, "method not allowed"),
    NOT_ACCEOPTABLE(HttpServletResponse.SC_NOT_ACCEPTABLE, 4007, "not acceptable"),
    UNSUPPORTED_MEDIA_TYPE(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, 4008, "unsupported media type"),
    LOGIN_NEEDED(HttpServletResponse.SC_FORBIDDEN, 4009, "login required"),
    INTERNAL_SERVER_ERROR(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 5000, "internal server error"),
    UNCATEGORIZED(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 5001, "uncategorized"),
    SERVICE_UNAVAILABLE(HttpServletResponse.SC_SERVICE_UNAVAILABLE, 5002, "service unavailable");

    private final int code;
    private final String message;
    private int status;

    TricountApiErrorCode(final int status, final int code, final String message) {
        this.status = status;
        this.message = message;
        this.code = code;
    }

    public String getMessage() {
        return this.message;
    }

    public int getCode() {
        return code;
    }

    public int getStatus() {
        return status;
    }
}
