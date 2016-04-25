package com.terapico.hacontrol.common;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class IPTest {

	/**
	 * @param args
	 * @throws UnknownHostException 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		System.out.println(getHostIp());
		System.out.println(getNonLoopBackHostInterface().getDisplayName());
		
		//main2();
	}
	protected static String getHostIp() throws UnknownHostException, SocketException {

		Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
		while (networkInterfaces.hasMoreElements()) {
			NetworkInterface e = networkInterfaces.nextElement();
			//System.out.println("Interface: " + e.getName());
			if(e.isLoopback()){
				continue;
			}
			Enumeration<InetAddress> a = e.getInetAddresses();
			while (a.hasMoreElements()) {
				InetAddress addr = a.nextElement();
				if(addr instanceof Inet4Address){
					return addr.getHostAddress();
				}
			}
		}
		
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
	


}
