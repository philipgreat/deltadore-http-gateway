package com.terapico.hacontrol.common;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

public class HAControlClientTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	
	@Test
	public void testInit() throws IOException, SAXException, ParserConfigurationException {
		
		HAControlClient client=new HAControlClient();
		client.init();
		for(int i=0;i<10;i++){
			System.out.println(client.switchOffLight(0, 0).toXML());
			System.out.println(client.switchOnLight(0, 0).toXML());
		}
	
		
		
	}
	@Test
	public void testInit2() throws IOException, SAXException, ParserConfigurationException {
		
		HAControlClient client=new HAControlClient();
		client.init();
		System.out.println(client.getControlPanelURL());		
		
		
	}
	
	

}
