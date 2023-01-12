package com.open.capacity.rule.exception;

 
public class RuleExecutionException extends RuntimeException {

    private static final long serialVersionUID = -4972406752422435809L;
 
    public RuleExecutionException(String message) {
        super(message);
    }

    public RuleExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
