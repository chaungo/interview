package models.exception;

public class APIException extends Exception {
    private static final long serialVersionUID = -4530140103715612384L;
    private String message;
    private APIErrorCode errorCode = APIErrorCode.GENERIC;
    public APIException() {
        super();
        
    }
    public APIException(String message) {
        super(message);
        this.message = message;
    }
    public APIException(String message, Throwable t) {
        super(message, t);
        this.message = message;
    }
    public APIException(String message, APIErrorCode errorCode) {
        super();
        this.message = message;
        this.errorCode = errorCode;
    }
    public APIException(String message, APIErrorCode errorCode, Throwable t) {
        super(t);
        this.message = message;
        this.errorCode = errorCode;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public APIErrorCode getErrorCode() {
        return errorCode;
    }
    public void setErrorCode(APIErrorCode errorCode) {
        this.errorCode = errorCode;
    }
    
}
