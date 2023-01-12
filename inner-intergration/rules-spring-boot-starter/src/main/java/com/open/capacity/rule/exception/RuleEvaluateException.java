package com.open.capacity.rule.exception;

public class RuleEvaluateException extends RuntimeException {

	private static final long serialVersionUID = 4047862011948527278L;

	public RuleEvaluateException(String message) {
		super(message);
	}

	public RuleEvaluateException(String message, Throwable cause) {
		super(message, cause);
	}
}
