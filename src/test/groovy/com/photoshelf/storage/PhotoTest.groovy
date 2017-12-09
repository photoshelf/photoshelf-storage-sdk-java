package com.photoshelf.storage

import com.photoshelf.storage.exception.InvalidImageException
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
		data												| type		|| result
		new FileInputStream("src/test/resources/lena.jpg")	| "jpeg"	|| "image/jpeg"
		new FileInputStream("src/test/resources/lena.png")	| "PNG"		|| "image/png"
	}

	def "When unknown file type, throws IllegalStateException"() {
		when:
		new Photo(data)

		then:
		def e = thrown(exception)
		assert e.class == exception

		where:
		data												|| exception
		new ByteArrayInputStream("hoge".getBytes())			|| InvalidImageException
		new FileInputStream("src/test/resources/lena.pdf")	|| InvalidImageException
	}
}
