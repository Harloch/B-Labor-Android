package com.barelabor.barelabor.data;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class IOTools {

	public static byte[] readAll(InputStream input) throws IOException {
		ByteArrayOutputStream output;
		byte[] buffer = new byte[1024];
		int count;

		input = new BufferedInputStream(input);
		output = new ByteArrayOutputStream();

		do {
			count = input.read(buffer);
			if (count > 0) {
				output.write(buffer, 0, count);
			}
		} while (count > 0);

		return output.toByteArray();
	}
}
