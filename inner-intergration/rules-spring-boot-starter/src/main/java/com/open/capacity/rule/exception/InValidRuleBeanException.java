package com.open.capacity.rule.exception;

 
public class InValidRuleBeanException extends RuntimeException {


	/**
	 *
	 */
	private static final long serialVersionUID = -5867334410327567810L;


	public InValidRuleBeanException() {
    }

     
    public InValidRuleBeanException(String message) {
        super(message);
    }

     
    public InValidRuleBeanException(String message, Throwable cause) {
        super(message, cause);
    }
}
