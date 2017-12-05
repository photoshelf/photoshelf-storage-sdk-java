package com.photoshelf.storage

import spock.lang.Specification
import spock.lang.Unroll

class PhotoTest extends Specification {

	@Unroll
	def "When file type is #type, returns #result"() {
		when:
		def photo = new Photo(data)

		then:
		photo.mimeType().toString() == result

		where:
		data														| type		|| result
		new FileInputStream("src/test/resources/lena.jpg")	| "jpeg"	|| "image/jpeg"
		new FileInputStream("src/test/resources/lena.png")	| "PNG"		|| "image/png"
	}

	def "When unknown file type, throws IllegalStateException"() {
		when:
		def photo = new Photo(data)

		then:
		try {
			photo.mimeType()
		} catch(Exception e) {
			assert e.class == exception
		}

		where:
		data														|| exception
		new ByteArrayInputStream("hoge".getBytes())					|| IllegalStateException
		new FileInputStream("src/test/resources/lena.pdf")	|| IllegalStateException
	}
}
