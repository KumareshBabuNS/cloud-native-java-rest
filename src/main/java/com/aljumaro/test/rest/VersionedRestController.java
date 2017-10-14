package com.aljumaro.test.rest;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@RestController
@RequestMapping("/api")
public class VersionedRestController {

	public static final String V1_MEDIA_TYPE_VALUE = "application/vnd.bootiful.demo-v1+json";
	public static final String V2_MEDIA_TYPE_VALUE = "application/vnd.bootiful.demo-v2+json";

	private enum ApiVersion {
		v1, v2
	}

	@Builder
	private static class Greeting {

		// @formatter:off
		@Getter @Setter String how;
		@Getter @Setter String version;
		// @formatter:on

	}

	@GetMapping("/{version}/hi")
	Greeting greetWithPathVariable(@PathVariable ApiVersion version) {
		return greet("Path variable", version);
	}

	@GetMapping(value = "/hi", produces = MediaType.APPLICATION_JSON_VALUE)
	Greeting greetWithHeader(@RequestHeader("X-API-Version") ApiVersion version) {
		return this.greet("Header", version);
	}

	@GetMapping(value = "/hi", produces = V1_MEDIA_TYPE_VALUE)
	Greeting greetWithContentNegotiationV1() {
		return this.greet("Content-Negotiation", ApiVersion.v1);
	}

	@GetMapping(value = "/hi", produces = V2_MEDIA_TYPE_VALUE)
	Greeting greetWithContentNegotiationV2() {
		return this.greet("Content-Negotiation", ApiVersion.v2);
	}

	Greeting greet(String how, ApiVersion version) {
		return Greeting.builder().how(how).version(version.toString()).build();
	}
}
