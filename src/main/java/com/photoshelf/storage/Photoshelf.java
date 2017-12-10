package com.photoshelf.storage;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.photoshelf.storage.exception.InvalidImageException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClients;

import java.io.*;
import java.net.URL;
import java.net.UnknownHostException;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_OK;

public class Photoshelf {

	private URL url;
	private HttpClient httpClient;

	public Photoshelf(URL url) {
		this.url = url;
		this.httpClient = HttpClients.createDefault();
	}

	public Photo find(Identifier id) throws IOException, InvalidImageException {
		HttpGet request = new HttpGet(this.url + "/photos/" + id);
		HttpResponse response = this.httpClient.execute(request);
		if (response.getStatusLine().getStatusCode() == SC_OK) {
			return Photo.of(id, response.getEntity().getContent());
		} else {
			throw new IllegalStateException(response.toString());
		}
	}

	public Identifier create(Photo photo) throws IOException {
		HttpPost request = new HttpPost(this.url + "/photos");
		request.setEntity(MultipartEntityBuilder.create()
				.addBinaryBody("photo", photo.getImage(), ContentType.create("application/octet-stream"), "image")
				.build());
		HttpResponse response = this.httpClient.execute(request);
		if (response.getStatusLine().getStatusCode() == SC_CREATED) {
			JsonObject json = new Gson().fromJson(new InputStreamReader(response.getEntity().getContent()), JsonObject.class);
			return Identifier.of(json.get("id").getAsString());
		} else {
			throw new IllegalStateException(response.toString());
		}
	}

	public void replace(Photo photo) throws IOException {
		if (photo.identifier().isPresent()) {
			HttpPut request = new HttpPut(this.url + "/photos/" + photo.identifier().get());
			request.setEntity(MultipartEntityBuilder.create()
					.addBinaryBody("photo", photo.getImage(), ContentType.create("application/octet-stream"), "image")
					.build());
			HttpResponse response = this.httpClient.execute(request);
			if (response.getStatusLine().getStatusCode() != SC_OK) {
				throw new IllegalStateException(response.toString());
			}
		} else {
			throw new IllegalArgumentException("id must not be null");
		}
	}

	public void delete(Identifier id) throws IOException {
		HttpDelete request = new HttpDelete(this.url + "/photos/" + id);
		HttpResponse response = this.httpClient.execute(request);
		if (response.getStatusLine().getStatusCode() != SC_OK) {
			throw new IllegalStateException(response.toString());
		}
	}

	public boolean healthy() throws IOException {
		HttpGet request = new HttpGet(this.url.toString());
		try {
			this.httpClient.execute(request);
			return true;
		} catch (HttpHostConnectException | UnknownHostException ignore) {
			return false;
		}
	}

}
