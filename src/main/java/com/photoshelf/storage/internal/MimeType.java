package com.photoshelf.storage.internal;

import com.photoshelf.storage.exception.InvalidMimeTypeException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

public class MimeType {

	private String raw;

	public static MimeType guessFromStream(InputStream is) {
		try {
			String mimeType = URLConnection.guessContentTypeFromStream(is);
			if (mimeType == null) {
				throw new InvalidMimeTypeException("unknown mimetype");
			}
			return new MimeType(mimeType);
		} catch (IOException e) {
			throw new InvalidMimeTypeException("cannot read data.", e);
		}
	}

	private MimeType(String mimetype) {
		this.raw = mimetype;
	}

	private boolean isImage() {
		String primaryType = this.raw.split("/")[0];
		return primaryType.equals("image");
	}

	@Override
	public String toString() {
		return raw;
	}
}
