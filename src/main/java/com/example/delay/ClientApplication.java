package com.example.delay;

import reactor.core.publisher.Flux;
import reactor.netty.http.client.HttpClient;

public class ClientApplication {
	public static void main(String[] args) {
		HttpClient client = HttpClient.create().port(8080);

		System.out.println(Flux.range(0, 20)
				.flatMap(i -> client.get()
						.uri("/test")
						.responseContent()
						.aggregate()
						.asString())
				.collectList()
				.block());
	}
}
