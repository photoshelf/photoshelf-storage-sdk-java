package com.photoshelf.storage;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import java.io.*;
import java.net.URLConnection;

public class Photo {

	private Identifier id;
	private byte[] image;

	public Photo(InputStream image) throws IOException {
		this.image = readAll(image);
	}

	static Photo of(Identifier id, InputStream image) throws IOException {
		Photo instance = new Photo(image);
		instance.id = id;
		return instance;
	}

	public byte[] getImage() {
		return image;
	}

	public MimeType mimeType() {
		InputStream is = new BufferedInputStream(new ByteArrayInputStream(this.image));
		try {
			String mimeType = URLConnection.guessContentTypeFromStream(is);
			if (mimeType == null) {
				throw new IllegalStateException("cannot guess content type.");
			}
			return new MimeType(mimeType);
		} catch (MimeTypeParseException | IOException e) {
			throw new IllegalStateException("unknown data type", e);
		}
	}

	public boolean isNew() {
		return this.id == null;
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
