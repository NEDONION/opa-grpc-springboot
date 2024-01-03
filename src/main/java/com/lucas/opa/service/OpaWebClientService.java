package com.lucas.opa.service;

import com.lucas.opa.model.OpaRequest;
import com.lucas.opa.model.OpaRequestBodyModel;
import com.lucas.opa.model.OpaResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class OpaWebClientService {

	private final WebClient webClient;

	public OpaWebClientService(WebClient.Builder webClientBuilder,
			@Value("${opa.url}") String opaUrl) {
		this.webClient = webClientBuilder.baseUrl(opaUrl).build();
	}

	public Mono<OpaResponse> postForOpaResponse(OpaRequest requestBody) {
		return webClient.post()
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(requestBody)
				.retrieve()
				.bodyToMono(OpaResponse.class);
	}
}
