package share.resume.com.exceptions;

public class JwtException extends RuntimeException {
    public JwtException(String message) {
        super(message);
    }
}
