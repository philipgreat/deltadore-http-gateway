package com.terapico.hacontrol.common;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

public class HANode {
	private static final String LIGHT="light";
	private static final String ROLLERSHUTTER="rollershutter";
	private static final String UNKNOWN="unkown";
	
	private String _type="unkown";
	private int networkId;
	private int nodeId;
	private Properties nodeProperties;
	public String getKey()
	{
		return getKey(this.getNetworkId(),this.getNodeId());		
	}
	public static String getKey(int networkId,int nodeId)
	{
		return "node:net"+networkId+"_node"+nodeId;
		
	}
	/**
	 * @return the networkId
	 */
	public int getNetworkId() {
		return networkId;
	}
	/**
	 * @param networkId the networkId to set
	 */
	public void setNetworkId(int networkId) {
		this.networkId = networkId;
	}
	/**
	 * @return the nodeId
	 */
	public int getNodeId() {
		return nodeId;
	}
	/**
	 * @param nodeId the nodeId to set
	 */
	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}
	private HANode()
	{
		nodeProperties=new Properties();		
	}
	public static HANode createLightNode()
	{
		HANode haNode=new HANode();
		haNode.setType(LIGHT);
		return haNode;
	}
	public static HANode createLightNode(int network,int node)
	{
		HANode haNode=new HANode();
		haNode.setType(LIGHT);
		haNode.setNetworkId(network);
		haNode.setNodeId(node);
		haNode.addProperty("level", "0");
		return haNode;
	}
	public static HANode createRollerShutterNode(int network,int node)
	{
		HANode haNode=new HANode();
		haNode.setType(ROLLERSHUTTER);
		haNode.setNetworkId(network);
		haNode.setNodeId(node);
		haNode.addProperty("position", "0");
		return haNode;
	}
	public static HANode createLightNodeWithLuminarcControl(int network,int node)
	{
		HANode haNode=new HANode();
		haNode.setType(LIGHT);
		haNode.setNetworkId(network);
		haNode.setNodeId(node);
		haNode.addProperty("level", "0");
		haNode.addProperty("luminarce", "true");
		return haNode;
	}
	public static HANode createRollerShutterNode()
	{
		HANode haNode=new HANode();
		haNode.setType(ROLLERSHUTTER);
		return haNode;
	}
	public static HANode createUnkownNode(int network,int node)
	{
		HANode haNode=new HANode();
		haNode.setNetworkId(network);
		haNode.setNodeId(node);
		haNode.setType(UNKNOWN);
		return haNode;
	}
	public void setType(String type) {
		// TODO Auto-generated method stub
		_type=type;
	}
	public String getType(String type) {
		// TODO Auto-generated method stub
		return _type;
	}
	public void addProperty(String name,String value) {
		// TODO Auto-generated method stub
		this.nodeProperties.setProperty(name, value);
	}
	public String toExpression() {
		// TODO Auto-generated method stub
		StringBuffer stringBuffer=new StringBuffer();
		stringBuffer.append(this.getKey()+"={");
		stringBuffer.append("nodeType="+_type+";");		
		Iterator it=nodeProperties.entrySet().iterator();
		while(it.hasNext()){
			Entry entry=(Entry)it.next();			
			stringBuffer.append(entry.getKey()+"="+entry.getValue()+";");
			
		}	
		
		stringBuffer.append("};");
		return stringBuffer.toString();		
	}
	public Map<String,String> toMap() {
		
		Map<String,String>returnMap=new HashMap<String,String>();		
		StringBuffer stringBuffer=new StringBuffer();		
		returnMap.put("network", this.getNetworkId()+"");
		returnMap.put("node", this.getNodeId()+"");
		returnMap.put("nodeType", _type);
		
		
		Iterator it=nodeProperties.entrySet().iterator();
		while(it.hasNext()){
			Entry entry=(Entry)it.next();			
			stringBuffer.append(entry.getKey()+"="+entry.getValue()+";");
			
			returnMap.put((String)entry.getKey(),(String) entry.getValue());
			
		}	
		
		
		return returnMap;		
	}

}
