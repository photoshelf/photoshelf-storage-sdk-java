package com.photoshelf.strage;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClients;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class Photoshelf {

	private URL url;
	private HttpClient httpClient;

	public Photoshelf(URL url) {
		this.url = url;
		this.httpClient = HttpClients.createDefault();
	}

	public byte[] find(String id) throws IOException {
		HttpGet request = new HttpGet(this.url + "/" + id);
		HttpResponse response = this.httpClient.execute(request);
		return readAll(response.getEntity().getContent());
	}

	public String create(byte[] photo) throws IOException {
		HttpPost request = new HttpPost(this.url + "/");
		request.setEntity(MultipartEntityBuilder.create().addBinaryBody("photo", photo).build());
		HttpResponse response = this.httpClient.execute(request);
		JsonObject json = new Gson().fromJson(new InputStreamReader(response.getEntity().getContent()), JsonObject.class);
		return json.get("id").getAsString();
	}

	public void replace(String id, byte[] photo) throws IOException {
		HttpPut request = new HttpPut(this.url + "/" + id);
		request.setEntity(MultipartEntityBuilder.create().addBinaryBody("photo", photo).build());
		this.httpClient.execute(request);
	}

	public void delete(String id) throws IOException {
		HttpDelete request = new HttpDelete(this.url + "/" + id);
		this.httpClient.execute(request);
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
