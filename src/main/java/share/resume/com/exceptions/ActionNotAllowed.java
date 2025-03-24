package share.resume.com.exceptions;

public class ActionNotAllowed extends RuntimeException {
    public ActionNotAllowed(String message) {
        super(message);
    }
}
