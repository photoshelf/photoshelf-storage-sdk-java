package com.photoshelf.storage.internal;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

public class MimeType {

	private String raw;

	public static MimeType guessFromStream(InputStream is) throws InvalidMimeTypeException, IOException {
		String mimeType = URLConnection.guessContentTypeFromStream(new BufferedInputStream(is));
		if (mimeType == null) {
			throw new InvalidMimeTypeException("unknown mimetype");
		}
		return new MimeType(mimeType);
	}

	private MimeType(String mimetype) {
		this.raw = mimetype;
	}

	public boolean isImage() {
		String primaryType = this.raw.split("/")[0];
		return primaryType.equals("image");
	}

	@Override
	public String toString() {
		return raw;
	}
}
