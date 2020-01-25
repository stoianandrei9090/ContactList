package ro.jademy.contactlist;

import java.util.List;

public class InputNotValidException extends Exception {

    //private String fieldName;
    private List<String> messageList;

    public List<String> getMessageList() {
        return messageList;
    }

    public void printMessageList(){
        for(String message : messageList) System.out.println(message);
    }

    public InputNotValidException(List<String> messageList) {
        this.messageList = messageList;
    }

    public void setMessageList(List<String> messageList) {
        this.messageList = messageList;
    }

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
