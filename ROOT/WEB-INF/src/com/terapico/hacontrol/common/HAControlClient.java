package com.terapico.hacontrol.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.MulticastSocket;
import java.net.URL;
import java.text.MessageFormat;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class HAControlClient {

	private String serviceBaseURLPrefix;

	public void init() throws IOException, SAXException, ParserConfigurationException {

		String xml = getAddressResponseXML();
		String serviceURLExpr = HAResponse.fromXMLText(xml).getValue("serviceURL").toString();
		serviceBaseURLPrefix = serviceURLExpr;

	}

	public String getControlPanelURL() throws MalformedURLException{
		URL baseURL=new URL(serviceBaseURLPrefix);
		URL url=new URL(baseURL,"/controlpanel.html");
		return url.toString();		
		
	}
	
	private static final String QUERY_TOPOLOGY = "/queryTopology/";
	public HAResponse queryTopology() throws IOException, SAXException, ParserConfigurationException {
		Object[] values = new Object[] {  };
		return executeCommand(QUERY_TOPOLOGY, values);
	}
	private static final String QUERY_CACHED_TOPOLOGY = "/queryCachedTopology/";
	public HAResponse queryCachedTopology() throws IOException, SAXException, ParserConfigurationException {
		Object[] values = new Object[] {  };
		return executeCommand(QUERY_CACHED_TOPOLOGY, values);
	}
		
	
	private static final String START_NODE_DISCOVERY = "/startNodeDiscovery/{0}/";
	public HAResponse queryCachedTopology(int network) throws IOException, SAXException, ParserConfigurationException {
		Object[] values = new Object[] { network };
		return executeCommand(START_NODE_DISCOVERY, values);
	}
		
	private static final String DELETE_ALL_NODES = "/deleteAllNodes/{0}/";
	public HAResponse deleteAllNodes(int network) throws IOException, SAXException, ParserConfigurationException {
		Object[] values = new Object[] { network };
		return executeCommand(DELETE_ALL_NODES, values);
	}
	
	private static final String DELETE_NODE = "/deleteNode/{0}/{1}/";
	public HAResponse deleteNode(int network,int node) throws IOException, SAXException, ParserConfigurationException {
		Object[] values = new Object[] { network ,node};
		return executeCommand(DELETE_NODE, values);
	}
	
	
	private static final String SWITCH_ON_LIGHT = "/switchOnLight/{0}/{1}/";
	public HAResponse switchOnLight(int network, int node) throws IOException, SAXException, ParserConfigurationException {
		Object[] values = new Object[] { network, node };
		return executeCommand(SWITCH_ON_LIGHT, values);
	}
	
	
	private static final String SWITCH_OFF_LIGHT = "/switchOffLight/{0}/{1}/";
	public HAResponse switchOffLight(int network, int node) throws IOException, SAXException, ParserConfigurationException {
		Object[] values = new Object[] { network, node };
		return executeCommand(SWITCH_OFF_LIGHT, values);
	}

	private static final String ADJUST_LIGHT = "/adjustLight/{0}/{1}/{2}/";
	public HAResponse adjustLight(int network, int node,int percent) throws IOException, SAXException, ParserConfigurationException {
		Object[] values = new Object[] { network, node,percent };
		return executeCommand(ADJUST_LIGHT, values);
	}

	private static final String SET_LIGHT_COLOR = "/setLightColor/{0}/{1}/{2}/{3}/{4}/";
	public HAResponse setLightColor(int network, int node,int red, int blue,int green) throws IOException, SAXException, ParserConfigurationException {
		Object[] values = new Object[] { network, node,red,blue,green };
		return executeCommand(SET_LIGHT_COLOR, values);
	}

	private static final String GET_LIGHT_COLOR = "/getLightColor/{0}/{1}/";
	public HAResponse getLightColor(int network, int node) throws IOException, SAXException, ParserConfigurationException {
		Object[] values = new Object[] { network, node };
		return executeCommand(GET_LIGHT_COLOR, values);
	}

	private static final String QUERY_LIGHT_STATUS = "/queryLightStatus/{0}/{1}/";
	public HAResponse queryLightStatus(int network, int node) throws IOException, SAXException, ParserConfigurationException {
		Object[] values = new Object[] { network, node };
		return executeCommand(QUERY_LIGHT_STATUS, values);
	}

	///queryLightInfo/{networkId}/{nodeId}/ 
	
	private static final String QUERY_LIGHT_INFO = "/queryLightInfo/{networkId}/{nodeId}/";
	public HAResponse queryLightInfo(int network, int node) throws IOException, SAXException, ParserConfigurationException {
		Object[] values = new Object[] { network, node };
		return executeCommand(QUERY_LIGHT_INFO, values);
	}
	
	private static final String OPEN_ROLLERSHUTTER = "/openRollerShutter/{0}/{1}/";
	public HAResponse openRollerShutter(int network, int node) throws IOException, SAXException, ParserConfigurationException {
		Object[] values = new Object[] { network, node };
		return executeCommand(OPEN_ROLLERSHUTTER, values);
	}
	
	private static final String CLOSE_ROLLERSHUTTER = "/closeRollerShutter/{0}/{1}/";
	public HAResponse closeRollerShutter(int network, int node) throws IOException, SAXException, ParserConfigurationException {
		Object[] values = new Object[] { network, node };
		return executeCommand(CLOSE_ROLLERSHUTTER, values);
	}
	
	private static final String OPEN_ROLLERSHUTTER_SLOWLY = "/openRollerShutterSlowly/{0}/{1}/";
	public HAResponse openRollerShutterSlowly(int network, int node) throws IOException, SAXException, ParserConfigurationException {
		Object[] values = new Object[] { network, node };
		return executeCommand(OPEN_ROLLERSHUTTER_SLOWLY, values);
	}
	
	private static final String CLOSE_ROLLERSHUTTER_SLOWLY = "/closeRollerShutterSlowly/{0}/{1}/";
	public HAResponse closeRollerShutterSlowly(int network, int node) throws IOException, SAXException, ParserConfigurationException {
		Object[] values = new Object[] { network, node };
		return executeCommand(CLOSE_ROLLERSHUTTER_SLOWLY, values);
	}
	
	private static final String QUERY_ROLLERSHUTTER_INFO = "/queryRollerShutterInfo/{0}/{1}/  ";
	public HAResponse queryRollerShutterInfo(int network, int node) throws IOException, SAXException, ParserConfigurationException {
		Object[] values = new Object[] { network, node };
		return executeCommand(QUERY_ROLLERSHUTTER_INFO, values);
	}
	
	private static final String QUERY_ROLLERSHUTTER_STATUS = "/queryRollerShutterStatus/{0}/{1}/  ";
	public HAResponse queryRollerShutterStatus(int network, int node) throws IOException, SAXException, ParserConfigurationException {
		Object[] values = new Object[] { network, node };
		return executeCommand(QUERY_ROLLERSHUTTER_STATUS, values);
	}
	
	
	
	public HAResponse executeCommand(String commandTemplate, Object[] values) throws IOException, SAXException, ParserConfigurationException {
		String command = MessageFormat.format(commandTemplate, values);
		String xml = getTextFromCommand(command);
		return HAResponse.fromXMLText(xml);

	}

	protected String getTextFromCommand(String command) throws IOException {

		URL commandURL = new URL(serviceBaseURLPrefix+command);
		// Read all the text returned by the server
		BufferedReader in = new BufferedReader(new InputStreamReader(commandURL.openStream()));
		String str;
		StringBuffer stringBuffer = new StringBuffer();
		while ((str = in.readLine()) != null) {
			// str is one line of text; readLine() strips the newline
			// character(s)
			stringBuffer.append(str);
		}
		in.close();
		return stringBuffer.toString();

	}

	protected String getAddressResponseXML() throws IOException {
		InetAddress group = InetAddress.getByName("224.0.0.7");
		// InetSocketAddress groupInetSocketAddress = new
		// InetSocketAddress(InetAddress.getByName("228.5.6.7"), 6789);
		MulticastSocket socket = new MulticastSocket(6789);
		socket.setSoTimeout(5000);
		socket.joinGroup(group);
		// socket.joinGroup(groupInetSocketAddress,
		// NetworkInterface.getByInetAddress(getWifiInetAddress(wifi)));

		byte[] buf = new byte[1080];
		DatagramPacket recv = new DatagramPacket(buf, buf.length);
		socket.receive(recv);
		socket.leaveGroup(group);
		byte recvedBytes[] = new byte[recv.getLength()];
		System.arraycopy(buf, 0, recvedBytes, 0, recv.getLength());
		String address = new String(recvedBytes);
		// socket.leaveGroup(group);
		socket.close();

		return address;
	}

	
}
