package org.frank.flinksql.service.util;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.util.Map;

public class Utils {

	public static <T> ResponseEntity<T> request(TestRestTemplate template, String url, HttpMethod method, Object body, Class<T> clazz) {
		HttpHeaders headers = devHeaders();

		HttpEntity entity = new HttpEntity(body, headers);
		return template.exchange(url, method, entity, clazz);
	}

	public static <T> ResponseEntity<T> requestByGet(TestRestTemplate template, String url, Map<String,Object> args, Class<T> clazz) {
		return template.getForEntity(url, clazz ,args);
	}

	public static <T> ResponseEntity<T> request(TestRestTemplate template, String url, String fileKey, String file, Class<T> clazz, Object... urlVariables) throws IOException {
		HttpHeaders headers = devHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		MultiValueMap<String, Object> multipartRequest = new LinkedMultiValueMap<>();
		multipartRequest.add(fileKey, new ClassPathResource(file));
		HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(multipartRequest, headers);

		return template.exchange(url, HttpMethod.POST, entity, clazz, urlVariables);
	}

	public static <T> ResponseEntity<T> requestPost(TestRestTemplate template, String url, Class<T> clazz) throws IOException {
		HttpHeaders headers = devHeaders();
		HttpEntity entity = new HttpEntity<>(headers);
		return template.postForEntity(url, entity, clazz);
	}

	public static <T> ResponseEntity<T> requestPost(TestRestTemplate template, String url, Object body, Class<T> clazz) {
		HttpHeaders headers = devHeaders();
		HttpEntity entity = new HttpEntity(body, headers);
		return template.postForEntity(url, entity, clazz);
	}

	private static HttpHeaders devHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		return headers;
	}

}
