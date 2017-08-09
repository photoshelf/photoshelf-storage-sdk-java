package com.photoshelf.storage;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

	public byte[] find(String id) throws IOException {
		HttpGet request = new HttpGet(this.url + "/photos/" + id);
		HttpResponse response = this.httpClient.execute(request);
		if (response.getStatusLine().getStatusCode() == SC_OK) {
			return readAll(response.getEntity().getContent());
		} else {
			throw new IllegalStateException(response.toString());
		}
	}

	public String create(byte[] photo) throws IOException {
		HttpPost request = new HttpPost(this.url + "/photos");
		request.setEntity(MultipartEntityBuilder.create()
				.addBinaryBody("photo", photo, ContentType.create("application/octet-stream"), "image")
				.build());
		HttpResponse response = this.httpClient.execute(request);
		if (response.getStatusLine().getStatusCode() == SC_CREATED) {
			JsonObject json = new Gson().fromJson(new InputStreamReader(response.getEntity().getContent()), JsonObject.class);
			return json.get("Id").getAsString();
		} else {
			throw new IllegalStateException(response.toString());
		}
	}

	public void replace(String id, byte[] photo) throws IOException {
		HttpPut request = new HttpPut(this.url + "/photos/" + id);
		request.setEntity(MultipartEntityBuilder.create()
				.addBinaryBody("photo", photo, ContentType.create("application/octet-stream"), "image")
				.build());
		HttpResponse response = this.httpClient.execute(request);
		if (response.getStatusLine().getStatusCode() != SC_OK) {
			throw new IllegalStateException(response.toString());
		}
	}

	public void delete(String id) throws IOException {
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

	private byte[] readAll(InputStream inputStream) throws IOException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		byte [] buffer = new byte[1024];
		while(true) {
			int len = inputStream.read(buffer);
			if(len < 0) {
				break;
			}
			bout.write(buffer, 0, len);
		}
		return bout.toByteArray();
	}
}
