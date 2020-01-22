package ro.jademy.contactlist;

public class InputNotValidException extends Exception {

    //private String fieldName;



    public InputNotValidException() {
    }

    public InputNotValidException(String message) {
        super(message);
    }

    public InputNotValidException(String message, Throwable cause) {
        super(message, cause);
    }

    public InputNotValidException(Throwable cause) {
        super(cause);
    }

    public InputNotValidException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
