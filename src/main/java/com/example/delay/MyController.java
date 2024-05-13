package com.example.delay;

import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

@RestController
public class MyController {

	final WebClient webClient;

	MyController(WebClient.Builder webClientBuilder) {
		this.webClient = webClientBuilder.build();
	}

	Mono<?> call() {
		return webClient.method(HttpMethod.GET)
				.uri("http://localhost:8080/delay")
				.headers(httpHeaders -> httpHeaders.set("Content-Type", "application/json"))
				.retrieve()
				.toEntity(String.class)
				.flatMap(Mono::just)
				.timeout(Duration.ofSeconds(20))
				.onErrorResume(TimeoutException.class, ex -> {
					String message = "Webclient timeout";
					return Mono.error(new TimeoutException(message).initCause(ex));
				});
	}

	@GetMapping("/test")
	public Mono<?> test() {
		return call();
	}

	@GetMapping("/delay")
	public Mono<String> delayResponse() {
		return Flux.interval(Duration.ofSeconds(15))
				.next()
				.map(any -> "success");
	}
}
