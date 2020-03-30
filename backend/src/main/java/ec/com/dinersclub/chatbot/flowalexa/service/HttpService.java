package ec.com.dinersclub.chatbot.flowalexa.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;

public class HttpService {

	private Object body;

	protected HttpEntity<Object> getHttpEntity() {
		return new HttpEntity<Object>(this.body, this.getHeaders());
	}

	/**
	 * @param body the body to set
	 */
	protected void setBody(Object body) {
		this.body = body;
	}

	private HttpHeaders getHeaders() {
		HttpHeaders httpHeaders = new HttpHeaders();
		if (body instanceof LinkedMultiValueMap) {
			httpHeaders.add("Content-Type", "application/x-www-form-urlencoded");
		} else {
			httpHeaders.add("Content-Type", "application/json");
		}
		return httpHeaders;
	}

}
