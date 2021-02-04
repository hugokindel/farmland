package com.ustudents.farmland.json.exception;

/** Exception for parsing related issues. */
public class JSonCannotParseException extends Exception {
    /**
     * Class constructor.
     *
     * @param message The message to pass.
     */
    public JSonCannotParseException(String message){
        super(message);
    }
}
