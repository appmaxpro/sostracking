package com.tms.exceptions;

public class TmsRuntimeException extends RuntimeException{

    public TmsRuntimeException() {
    }

    public TmsRuntimeException(String message) {
        super(message);
    }

    public TmsRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }



}
