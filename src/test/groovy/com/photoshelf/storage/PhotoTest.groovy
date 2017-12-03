package com.photoshelf.storage

import spock.lang.Specification

class PhotoTest extends Specification {

	def "When #value, returns #mineType"() {
		when:
		def photo = new Photo(data)

		then:
		photo.mimeType().toString() == result

		where:
		data														|| result
		new FileInputStream("src/test/resources/lena.jpg")	|| "image/jpeg"
		new FileInputStream("src/test/resources/lena.png")	|| "image/png"
	}
}
