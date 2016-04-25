package com.terapico.hacontrol.common;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class HAResponse implements java.io.Serializable {
	/**
     * 
     */
	private static final long serialVersionUID = 1L;

	private Map<String, Object> _properties;

	private static final String OK = "OK";

	private static final String ERROR = "ERROR";

	public HAResponse() {

		_properties = new HashMap();
	}

	public HAResponse addValue(String name, String value) {
		_properties.put(name, value);
		return this;
	}

	public HAResponse addNodeInfo(HANode nodeInfo) {
		// _properties.put(name, value);

		Map topology = (Map) this._properties.get("topology");
		if (topology == null) {
			topology = new HashMap();
			this._properties.put("topology", topology);
		}
		topology.put(nodeInfo.getKey(), nodeInfo.toMap());
		return this;
	}

	protected boolean isKeyForStatus(String key) {
		if ("network".equals(key)) {
			return false;
		}
		if ("node".equals(key)) {
			return false;
		}
		if ("luminance".equals(key)) {
			return false;
		}
		if ("nodeType".equals(key)) {
			return false;
		}

		return true;

	}

	public HAResponse putNodeToStatusReponse(HAResponse nodeTopology, int network, int node) {
		// _properties.put(name, value);

		Map<String,Object> topology = (Map<String,Object>) nodeTopology.getValue("topology");
		if (topology == null) {
			topology = new HashMap<String,Object>();
			this._properties.put("topology", topology);
		}
		
		Map<String, String> map = (Map<String, String>) topology.get(HANode.getKey(network, node));
		if (map == null) {

			return this;
		}
		Iterator<String> keyIter = map.keySet().iterator();
		while (keyIter.hasNext()) {
			String key = (String) keyIter.next();

			String value = map.get(key);
			this.addValue(key, value);

		}

		return this;
	}

	public void resetNodes() {
		// _properties.put(name, value);
		this._properties.remove("topology");
	}

	public HAResponse updateNodeInfo(HANode nodeInfo) {
		// _properties.put(name, value);

		Map topology = (Map) this._properties.get("topology");
		if (topology == null) {
			topology = new HashMap();
			this._properties.put("topology", topology);
		}
		topology.put("node" + ":" + nodeInfo.getKey(), nodeInfo.toMap());
		return this;
	}

	public String getStringValue(String key) {
		return (String) _properties.get(key);
	}

	public Object getValue(String key) {
		return _properties.get(key);
	}

	public HAResponse addBoolValue(String name, boolean value) {
		if (!value) {
			return this;
		}
		_properties.put(name, value + "");
		return this;
	}

	public void removeValue(String name) {
		_properties.remove(name);
	}

	public boolean isOK() {
		return OK.equals(_properties.get("status"));
	}

	public boolean isError() {
		return ERROR.equals(_properties.get("status"));
	}

	public boolean testBoolean(String property) {
		return "true".equals(_properties.get(property));
	}

	public boolean testEqual(String property, String value) {
		if (value == null) {
			return false;
		}
		return value.equals(_properties.get(property));
	}

	public static HAResponse getOKStatus() {
		HAResponse haResponse = new HAResponse();
		haResponse.addValue("status", "OK");
		return haResponse;
	}

	public static HAResponse getErrorStatus(String message) {
		HAResponse haResponse = new HAResponse();
		haResponse.addValue("status", "ERROR");
		haResponse.addValue("message", message);
		return haResponse;
	}

	public String toExpression() {

		Iterator<String> keyIter = _properties.keySet().iterator();
		StringBuffer stringBuffer = new StringBuffer(100);
		while (keyIter.hasNext()) {
			String name = (String) keyIter.next();
			Object value = this.getValue(name);
			if (!(value instanceof String)) {
				continue;
			}
			stringBuffer.append(name);
			stringBuffer.append("=");
			stringBuffer.append(value);
			stringBuffer.append(";");
		}
		return stringBuffer.toString();
	}

	public String toXML() {
		return convertMap2XML("response", _properties);
	}

	protected static boolean isGoodName(String name) {
		if (name == null) {
			return false;
		}
		if ("".equals(name)) {
			return false;
		}
		return true;
	}

	protected static String getPrefixOnly(String oldValue) {
		int end = oldValue.indexOf(':');
		if (end < 1) {
			return oldValue;
		}
		return oldValue.substring(0, end);
	}

	static class ResponseParserHandler extends DefaultHandler {

		HAResponse response;

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.xml.sax.helpers.DefaultHandler#startDocument()
		 */
		@Override
		public void startDocument() throws SAXException {
			// TODO Auto-generated method stub
			super.startDocument();

			response = new HAResponse();

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
		 * java.lang.String, java.lang.String, org.xml.sax.Attributes)
		 */
		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			// TODO Auto-generated method stub
			super.startElement(uri, localName, qName, attributes);
			if ("response".equals(qName)) {
				int length = attributes.getLength();
				for (int i = 0; i < length; i++) {
					response.addValue(attributes.getQName(i), attributes.getValue(i));
				}
				return;
			}
			if ("node".equals(qName)) {				
				
				//response.addNodeInfo(null)
				response.addNodeInfo(createNodeFromAttributes(attributes));
				return;
			}
			

		}
		protected static HANode createNodeFromAttributes(Attributes attributes)
		{
			int networkNumber=Integer.parseInt(attributes.getValue("network"));
			
			int nodeNumber=Integer.parseInt(attributes.getValue("node"));
			
			HANode node=HANode.createUnkownNode(networkNumber, nodeNumber);
			//<node node="7" luminarce="true" level="0" nodeType="light" network="11"/>
			node.setType(attributes.getValue("nodeType"));
			
			int length = attributes.getLength();
			for (int i = 0; i < length; i++) {
				String name=attributes.getQName(i);
				if("nodeType|node|network".indexOf(name)>=0){
					continue;
				}
				node.addProperty(attributes.getQName(i), attributes.getValue(i));				
				
			}
			
			return node;
			
			
			
		}
		public HAResponse getResponse() {
			// TODO Auto-generated method stub
			return response;
		}

	}

	protected static HAResponse fromXMLText(String xmlExpr) throws SAXException, IOException, ParserConfigurationException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		StringReader reader = new StringReader(xmlExpr);
		InputSource inputSource = new InputSource(reader);
		ResponseParserHandler handler = new ResponseParserHandler();
		// Create the builder and parse the file
		factory.newSAXParser().parse(inputSource, handler);

		return handler.getResponse();
	}

	public static String convertMap2XML(String name, Map<String, Object> valueMap) {

		Iterator<String> keyIter = valueMap.keySet().iterator();
		StringBuffer stringBuffer = new StringBuffer(100);
		String tagName = getPrefixOnly(name);

		if (isGoodName(tagName)) {
			stringBuffer.append("<" + tagName);
		}
		boolean hasMap = false;
		// stringBuffer.append("<values");
		while (keyIter.hasNext()) {
			String key = (String) keyIter.next();
			Object value = valueMap.get(key);
			if (!(value instanceof String)) {
				hasMap = true;
				continue;
			}
			stringBuffer.append(" ");
			stringBuffer.append(key);
			stringBuffer.append("=\"");
			stringBuffer.append(value);
			stringBuffer.append("\"");
		}
		if (!hasMap) {
			stringBuffer.append("/>");
			return stringBuffer.toString();
		}
		stringBuffer.append(">");
		keyIter = valueMap.keySet().iterator();
		while (keyIter.hasNext()) {
			String key = (String) keyIter.next();
			Object value = valueMap.get(key);
			if (!(value instanceof Map)) {
				continue;
			}

			@SuppressWarnings("unchecked")
			Map<String, Object> subValueMap = (Map<String, Object>) value;
			stringBuffer.append(convertMap2XML(key, subValueMap));
		}

		if (isGoodName(name)) {
			stringBuffer.append("</" + tagName + ">");
		}
		return stringBuffer.toString();

	}

	public static void main(String args[]) throws SAXException, IOException, ParserConfigurationException {

		HAResponse resp = HAResponse.getOKStatus();
		System.out.println(resp.toXML());

		Map<String, String> nodeInfo = new HashMap<String, String>();

		nodeInfo.put("network", "10");
		nodeInfo.put("node", "10");

		// resp.addNodeInfo(nodeInfo);
		System.out.println(resp.toXML());

		String xml = "<response executionTime=\"83ms\" status=\"OK\"/>";
		System.out.println(HAResponse.fromXMLText(xml).toXML());

		xml = "<response status=\"OK\" executionTime=\"68ms\"><topology><node position=\"0\" node=\"9\" nodeType=\"rollershutter\" network=\"11\"/><node position=\"0\" node=\"8\" nodeType=\"rollershutter\" network=\"11\"/><node node=\"5\" luminarce=\"true\" level=\"0\" nodeType=\"light\" network=\"11\"/><node node=\"4\" luminarce=\"true\" level=\"0\" nodeType=\"light\" network=\"11\"/><node node=\"7\" luminarce=\"true\" level=\"0\" nodeType=\"light\" network=\"11\"/><node node=\"6\" luminarce=\"true\" level=\"0\" nodeType=\"light\" network=\"11\"/><node node=\"1\" level=\"0\" nodeType=\"light\" network=\"11\"/><node node=\"0\" level=\"0\" nodeType=\"light\" network=\"11\"/><node node=\"3\" level=\"0\" nodeType=\"light\" network=\"11\"/><node node=\"2\" level=\"0\" nodeType=\"light\" network=\"11\"/><node position=\"0\" node=\"11\" nodeType=\"rollershutter\" network=\"11\"/><node position=\"0\" node=\"10\" nodeType=\"rollershutter\" network=\"11\"/></topology></response>";
		System.out.println(HAResponse.fromXMLText(xml).toXML());
	}

	public HAResponse updateNodeStatus(int network, int node, String property, String value) {
		// TODO Auto-generated method stub

		Map topology = (Map) this._properties.get("topology");
		if (topology == null) {
			return this;
		}

		Map<String, String> values = (Map<String, String>) topology.get(HANode.getKey(network, node));
		if (values == null) {
			return this;
		}

		values.put(property, value);
		return this;

	}
	
	
	public String getNodeStatus(int network, int node, String property) {
		// TODO Auto-generated method stub

		Map topology = (Map) this._properties.get("topology");
		if (topology == null) {
			return null;
		}

		Map<String, String> values = (Map<String, String>) topology.get(HANode.getKey(network, node));
		if (values == null) {
			return null;
		}
		return values.get(property);
	}
	public Integer getNodeStatusInt(int network, int node, String property) {
		// TODO Auto-generated method stub

		Map topology = (Map) this._properties.get("topology");
		if (topology == null) {
			return null;
		}

		Map<String, String> values = (Map<String, String>) topology.get(HANode.getKey(network, node));
		if (values == null) {
			return null;
		}
		String value=values.get(property);
		if(value==null)return null;
		return Integer.valueOf(values.get(property));

	}
	

	public void removeNode(int network, int node) {
		// TODO Auto-generated method stub
		Map topology = (Map) this._properties.get("topology");
		if (topology == null) {
			return;
		}
		topology.remove(HANode.getKey(network, node));
	}

	public void deleteNodesFromNetwork(int network) {
		// TODO Auto-generated method stub
		Map topology = (Map) this._properties.get("topology");
		if (topology == null) {
			return;
		}
		Iterator<String> keyIter = topology.keySet().iterator();
		while (keyIter.hasNext()) {
			String key = (String) keyIter.next();
			Map<String,String> nodeInfo=(Map<String,String> )topology.get(key);
			if(nodeInfo.get("network").equals(network+"")){
				topology.remove(key);
			}
		}
	}

}
