package org.photoshelf.storage;

public class Identifier {

	private String value;

	private Identifier(String value) {
		this.value = value;
	}

	public static Identifier of(String value) {
		return new Identifier(value);
	}

	@Override
	public String toString() {
		return value;
	}
}
