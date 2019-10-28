/*******************************************************************************
 * Copyright (c) 2017 Intuit
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.intuit.ipp.compression;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPOutputStream;

import mockit.Expectations;
import mockit.Mocked;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.intuit.ipp.exception.CompressionException;
import com.intuit.ipp.util.Logger;

public class GZIPCompressorTest {

	private static final org.slf4j.Logger LOG = Logger.getLogger();

	@Test
	public void testCompressAndDecompress() {
		String data = "Hello World!";
		try {
			GZIPCompressor compressor = new GZIPCompressor();
			byte[] compressed = compressor.compress(data, null);
			LOG.debug("GZIP compression : " + compressed);

			String decompressed = new String(((ByteArrayOutputStream)compressor.decompress(new ByteArrayInputStream(compressed))).toByteArray());
			LOG.debug("GZIP decompression : " + decompressed);

			Assert.assertEquals(data, decompressed, "GZIPCompressor : Both the original data and compressed then decompressed data are not same.");
		} catch (CompressionException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testCompress() {
		String data = "Hello World!";
		try {
			GZIPCompressor compressor = new GZIPCompressor();
			byte[] compressed = compressor.compress(data, null);
			Assert.assertNotEquals(data, compressed, "GZIPCompressor : given data did not compress.");
		} catch (CompressionException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testCompress_null() {
		String data = null;
		try {
			GZIPCompressor compressor = new GZIPCompressor();
			byte[] compressed = compressor.compress(data, null);
			Assert.assertEquals(compressed, null);
		} catch (CompressionException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testCompress_uploadFile() throws CompressionException {
		String data = "Hello World!";
		byte[] uploadFile = data.getBytes();
		GZIPCompressor compressor = new GZIPCompressor();
		byte[] compressed = compressor.compress(data, uploadFile);
		Assert.assertNotEquals(uploadFile, compressed);
	}

	@Test(expectedExceptions = CompressionException.class)
	public void testCompress_CompressionException() throws CompressionException, IOException {
		final String data = "Hello World!";
		final DeflaterOutputStream mockedStream = new GZIPOutputStream(new ByteArrayOutputStream());
		new Expectations(DeflaterOutputStream.class) {{
			mockedStream.write(data.getBytes());
			result = new IOException("IOException thrown");
		}};

		GZIPCompressor compressor = new GZIPCompressor();
		compressor.compress(data, null);
	}

	@Test
	public void testDecompress() {
		String data = "Hello World!";
		try {
			GZIPCompressor compressor = new GZIPCompressor();
			byte[] compressed = compressor.compress(data, null);
			LOG.debug(compressed.toString());
			String decompressed = new String(((ByteArrayOutputStream)compressor.decompress(new ByteArrayInputStream(compressed))).toByteArray());
			Assert.assertEquals(data, decompressed, "GZIPCompressor : given data did not decompress.");
		} catch (CompressionException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testDecompress_invalid() {
		boolean isException = false;
		String data = "data";
		try {
			GZIPCompressor compressor = new GZIPCompressor();
			compressor.decompress(new ByteArrayInputStream(data.getBytes()));
		} catch (CompressionException e) {
			isException = true;
		}
		Assert.assertTrue(isException);
	}
}
