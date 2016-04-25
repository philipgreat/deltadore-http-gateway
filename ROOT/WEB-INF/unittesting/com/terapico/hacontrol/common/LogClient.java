package com.terapico.hacontrol.common;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class LogClient {

	/**
	 * @param args
	 * @throws UnknownHostException 
	 */
	public static void main(String[] args) throws UnknownHostException {
		// TODO Auto-generated method stub
		InetAddress addr = InetAddress.getByName("localhost");
        int port = 16800;
        send(addr,port,"cccc".getBytes(),4);
        
	}
	public static void send(InetAddress dst, int port, byte[] outbuf, int len) {
        try {
            DatagramPacket request = new DatagramPacket(outbuf, len, dst, port);
            DatagramSocket socket = new DatagramSocket();
            socket.send(request);
        } catch (SocketException e) {
        } catch (IOException e) {
        }
    }
}
