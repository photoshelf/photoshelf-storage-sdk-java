package com.photoshelf.strage;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.net.URL;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class PhotoshelfTest {

	private static final String BINDING_ADDRESS = "localhost";

	private Photoshelf client;

	@Rule
	public WireMockRule wireMockRule = new WireMockRule(options().dynamicPort().bindAddress(BINDING_ADDRESS));

	@Before
	public void setUp() throws Exception {
		URL base = new URL("http://" + BINDING_ADDRESS + ":" + wireMockRule.port() + "/");
		this.client = new Photoshelf(base);
	}

	@Test
	public void find() throws Exception {
		stubFor(get("/id")
				.willReturn(aResponse()
						.withStatus(200)
						.withBody("test".getBytes())));

		byte[] bytea = client.find("id");

		verify(1, getRequestedFor(urlEqualTo("/id")));
		assertThat(bytea, is("test".getBytes()));
	}

	@Test
	public void create() throws Exception {
		stubFor(post("/")
				.willReturn(aResponse()
						.withStatus(201)
						.withBody("{\"id\":\"foo\"}")));

		String id = client.create("test".getBytes());

		verify(1, postRequestedFor(urlEqualTo("/")));
		assertThat(id, is("foo"));
	}

	@Test
	public void replace() throws Exception {
		stubFor(put("/id")
				.willReturn(aResponse()
						.withStatus(200)));

		client.replace("id", new byte[]{});

		verify(1, putRequestedFor(urlEqualTo("/id")));
	}

	@Test
	public void deletePhoto() throws Exception {
		stubFor(delete("/id")
				.willReturn(aResponse()
						.withStatus(200)));

		client.delete("id");

		verify(1, deleteRequestedFor(urlEqualTo("/id")));
	}

}