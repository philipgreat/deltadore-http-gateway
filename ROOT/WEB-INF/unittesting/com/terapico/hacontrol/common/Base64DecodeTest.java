package com.terapico.hacontrol.common;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class Base64DecodeTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws IOException {
		String encodedData=getTextFromFile("tmp/lua-entryped.txt");
		//System.out.println(encodedData);
		byte[] buf = new sun.misc.BASE64Decoder().decodeBuffer(encodedData);
		System.out.println(new String(buf));
		
	}
	
	public static String getTextFromFile(String filePath) {
		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader in = new BufferedReader(new FileReader(filePath));
			String str;
			while ((str = in.readLine()) != null) {
				sb.append(str);
				sb.append("\r\n");
			}
			in.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return null;
		}
		return sb.toString();
	}

}
