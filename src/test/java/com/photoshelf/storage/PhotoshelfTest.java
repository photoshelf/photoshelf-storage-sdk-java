package com.photoshelf.storage;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.net.URL;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class PhotoshelfTest {

	private static final String BINDING_ADDRESS = "localhost";

	private Photoshelf client;

	@Rule
	public WireMockRule wireMockRule = new WireMockRule(options().dynamicPort().bindAddress(BINDING_ADDRESS));

	@Before
	public void setUp() throws Exception {
		URL base = new URL("http://" + BINDING_ADDRESS + ":" + wireMockRule.port());
		this.client = new Photoshelf(base);
	}

	@Test
	public void find() throws Exception {
		stubFor(get("/photos/id")
				.willReturn(aResponse()
						.withStatus(200)
						.withBody("test".getBytes())));

		Photo photo = client.find(Identifier.of("id"));

		verify(1, getRequestedFor(urlEqualTo("/photos/id")));
		assertThat(photo.getImage(), is("test".getBytes()));
	}

	@Test
	public void failToFind() throws Exception {
		stubFor(get("/photos/id").willReturn(notFound()));

		try {
			client.find(Identifier.of("id"));
			fail();
		} catch (IllegalStateException ignore) {
		}
		verify(1, getRequestedFor(urlEqualTo("/photos/id")));
	}

	@Test
	public void create() throws Exception {
		stubFor(post("/photos")
				.willReturn(aResponse()
						.withStatus(201)
						.withBody("{\"id\":\"foo\"}")));

		Identifier id = client.create("test".getBytes());

		verify(1, postRequestedFor(urlEqualTo("/photos")));
		assertThat(id.toString(), is("foo"));
	}

	@Test
	public void failToCreate() throws Exception {
		stubFor(post("/photos").willReturn(serverError()));

		try {
			client.create("test".getBytes());
			fail();
		} catch (IllegalStateException ignore) {
		}
		verify(1, postRequestedFor(urlEqualTo("/photos")));
	}

	@Test
	public void replace() throws Exception {
		stubFor(put("/photos/id")
				.willReturn(aResponse()
						.withStatus(200)));

		client.replace("id", new byte[]{});

		verify(1, putRequestedFor(urlEqualTo("/photos/id")));
	}

	@Test
	public void failToReplace() throws Exception {
		stubFor(put("/photos/id").willReturn(notFound()));

		try {
			client.replace("id", new byte[]{});
			fail();
		} catch (IllegalStateException ignore) {
		}
		verify(1, putRequestedFor(urlEqualTo("/photos/id")));
	}

	@Test
	public void deletePhoto() throws Exception {
		stubFor(delete("/photos/id")
				.willReturn(aResponse()
						.withStatus(200)));

		client.delete("id");

		verify(1, deleteRequestedFor(urlEqualTo("/photos/id")));
	}

	@Test
	public void failToDelete() throws Exception {
		stubFor(delete("/photos/id").willReturn(notFound()));

		try {
			client.delete("id");
			fail();
		} catch (IllegalStateException ignore) {
		}
		verify(1, deleteRequestedFor(urlEqualTo("/photos/id")));
	}

	@Test
	public void healthyWhenServerRunning() throws Exception {
		stubFor(get("/").willReturn(ok()));

		assertTrue(client.healthy());
	}

	@Test
	public void notHealthyWhenServerDie() throws Exception {
		wireMockRule.stop();

		assertFalse(client.healthy());
	}
}
