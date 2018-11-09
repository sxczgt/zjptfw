package com.unipay.builder;

public abstract class RequestBuilder {
	public RequestBuilder() {
	}

	public abstract boolean validate();

	public abstract Object getBizContent();
}
