package com.terapico.hacontrol.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;

import com.terapico.hacontrol.common.HAResponse;
import java.net.*;
import java.util.*;

class URLBroadCastingTask implements Runnable {

	private boolean running = true;

	private String urlTemplate;

	/**
	 * @return the urlTemplate
	 */
	public String getUrlTemplate() {
		return urlTemplate;
	}

	/**
	 * @param urlTemplate
	 *            the urlTemplate to set
	 */
	public void setUrlTemplate(String urlTemplate) {
		this.urlTemplate = urlTemplate;
	}

	public void run() {
		String broadcastContent = this.getBroadcastContent();
		if (null == broadcastContent) {
			return;
		}

		MulticastSocket multicastSocket = null;
		InetAddress group = null;
		try {
			group = InetAddress.getByName("224.0.0.7");
			multicastSocket = new MulticastSocket(6789);
			multicastSocket.joinGroup(group);

		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			System.err.println("Can not create and join the broast group due to: " + e1.getMessage());
			return;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Can not create and join the broast group due to: " + e.getMessage());
			return;
		}

		// TODO Auto-generated method stub
		while (this.isRunning()) {

			try {
				Thread.sleep(1000);
				broadcastContent = this.getBroadcastContent();
				if(broadcastContent==null){
					System.err.println("Can not get the content to broadcast!");
					continue;
				}
				DatagramPacket urlpackage = new DatagramPacket(broadcastContent.getBytes(), broadcastContent.length(), group, 6789);
				multicastSocket.send(urlpackage);
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				continue;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				continue;
			}

		}
		try {
			multicastSocket.leaveGroup(group);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/*
	 * <init-param> <param-name>urlsuffix</param-name>
	 * <param-value>http://{serverIp}:8080/hacontrol</param-value> </init-param>
	 */
	private String getBroadcastContent() {
		
		if (null == getUrlTemplate()) {
			System.err.println("Can not get urltemplate from web.xml, please config it.");
			return null;
		}
		if ("".equals(getUrlTemplate())) {
			System.err.println("Can use empty urltemplate from web.xml, please config it.");
			return null;
		}
		String hostIp = null;
		String macAddress = null;
	
		
		try {
			NetworkInterface firstExternalNetworkInterface=getNonLoopBackHostInterface();
			hostIp = getFirstExternalIPV4Address(firstExternalNetworkInterface);
			macAddress=getHostMacAddressExpr(firstExternalNetworkInterface);
		} catch (UnknownHostException e1) {
			System.err.println("Can not get local host ip address due to: " + e1.getMessage());
			return null;
		} catch (SocketException e1) {
			System.err.println("Can not get local host ip address due to: " + e1.getMessage());
			return null;
		}

		String content = this.getUrlTemplate().replaceAll("\\{serverIp\\}", hostIp);
		//content = this.getUrlTemplate().replaceAll("\\{macAddress\\}", hostIp);
		
		return HAResponse.getOKStatus().addValue("hardwareAddress", macAddress)
				.addValue("serviceURL", content)
				.addValue("messageType", "serviceURL")
				.addValue("timestamp", System.currentTimeMillis() / 1000 + "").toXML();
	}

	protected static String getHostIp() throws UnknownHostException, SocketException {

		Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
		while (networkInterfaces.hasMoreElements()) {
			NetworkInterface networkInterface = networkInterfaces.nextElement();
			//System.out.println("Interface: " + networkInterface.getDisplayName());
			if(networkInterface.isLoopback()){
				//System.out.println("found loopback interface "+networkInterface);
				continue;
			}
			Enumeration<InetAddress> addressEnum= networkInterface.getInetAddresses();
			while (addressEnum.hasMoreElements()) {
				InetAddress addr = addressEnum.nextElement();
				if(addr instanceof Inet4Address){
					
					return addr.getHostAddress();					
				}
			}
		}
		System.out.println("get default, this is not run under linux");
		InetAddress localhost = InetAddress.getLocalHost();
		return localhost.getHostAddress();

	}
	
	protected static NetworkInterface getNonLoopBackHostInterface() throws UnknownHostException, SocketException {

		Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
		while (networkInterfaces.hasMoreElements()) {
			NetworkInterface networkInterface = networkInterfaces.nextElement();
			//System.out.println("Interface: " + networkInterface.getDisplayName());
			if(networkInterface.isLoopback()){
				//System.out.println("found loopback interface "+networkInterface);
				continue;
			}
			Enumeration<InetAddress> addressEnum = networkInterface.getInetAddresses();
			while (addressEnum.hasMoreElements()) {
				InetAddress addr = addressEnum.nextElement();
				if(addr instanceof Inet4Address){
					return networkInterface;
				}
			}
						
		}
		throw new SocketException("Can not find any interface on this machine!");

	}
	
	protected static String getHostMacAddressExpr(NetworkInterface networkInterface) throws UnknownHostException, SocketException {

		byte[] hardwareAddress =	networkInterface.getHardwareAddress();
		if(hardwareAddress==null){
			return "00-00-00-00-00-00-00";
		}
		if(hardwareAddress.length==0){
			return "00-00-00-00-00-00-00";
		}
		StringBuilder sb=new StringBuilder();
		sb.append(String.format("%02X", hardwareAddress[0]));		
		for(int i=1;i<hardwareAddress.length;i++){			
			sb.append("-");
			sb.append(String.format("%02X", hardwareAddress[i]));			
		}		
		return sb.toString();
		

	}
	protected static String getFirstExternalIPV4Address(NetworkInterface networkInterface) throws UnknownHostException, SocketException {

		Enumeration<InetAddress> addressEnum= networkInterface.getInetAddresses();
		while (addressEnum.hasMoreElements()) {
			InetAddress addr = addressEnum.nextElement();
			if(addr instanceof Inet4Address){				
				return addr.getHostAddress();					
			}
		}
		
		throw new SocketException("Can not find any ipv4 address on this network interface: "+getHostMacAddressExpr(networkInterface)+"!");

	}

	/**
	 * @return the running
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * @param running
	 *            the running to set
	 */
	public void setRunning(boolean running) {
		this.running = running;
	}

	public void stop() {
		this.running = false;
	}

}

public class HAControlerURLBroadcastService extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String urlSuffix = "/hacontrol";

	/**
	 * @return the urlSuffix
	 */
	public String getUrlSuffix() {
		return urlSuffix;
	}

	/**
	 * @param urlSuffix
	 *            the urlSuffix to set
	 */
	public void setUrlSuffix(String urlSuffix) {
		this.urlSuffix = urlSuffix;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#service(javax.servlet.ServletRequest,
	 * javax.servlet.ServletResponse)
	 */
	@Override
	public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException {

	}

	private static final String URL_TEMPLATE = "urltempate";

	URLBroadCastingTask task;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
		super.init(config);
		String urlTemplate = config.getInitParameter(URL_TEMPLATE);
		if (null == urlTemplate) {
			System.err.println("Can not get urltemplate from web.xml, please config it.");
			return;
		}
		if ("".equals(urlTemplate)) {
			System.err.println("Can use empty urltemplate from web.xml, please config it.");
			return;
		}

		
		task = new URLBroadCastingTask();
		task.setUrlTemplate(urlTemplate);
		Thread thread = new Thread(task);
		thread.start();
		try {
			System.out.println("Service URL broadcasting services started with ip: "+URLBroadCastingTask.getHostIp());
		} catch (UnknownHostException exception) {
			// TODO Auto-generated catch block
			exception.printStackTrace();
		} catch (SocketException exception) {
			// TODO Auto-generated catch block
			exception.printStackTrace();
		}
	}

	public void destroy() {
		if (task == null) {
			return;
		}
		task.setRunning(false);
	}
}
