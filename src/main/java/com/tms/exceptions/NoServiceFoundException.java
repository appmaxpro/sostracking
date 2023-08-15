package com.tms.exceptions;

public class NoServiceFoundException extends java.lang.UnsupportedOperationException{

    public NoServiceFoundException() {
        this("NO_SERVICE_FOUND_ERROR");
    }

    public NoServiceFoundException(String message) {
        super(message);
    }

    public NoServiceFoundException(Throwable cause) {
        super(cause);
    }
}
