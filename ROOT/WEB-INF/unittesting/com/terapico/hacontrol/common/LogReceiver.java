package com.terapico.hacontrol.common;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;

public class LogReceiver {
	public static void main(String args[]) {

		try {
			byte[] inbuf = new byte[1024]; // default size
			DatagramSocket socket = new DatagramSocket(16800);

			while (true) {
				DatagramPacket packet = new DatagramPacket(inbuf, inbuf.length);

				socket.receive(packet);

				// Data is now in inbuf
				int numBytesReceived = packet.getLength();
				
				byte x[]=Arrays.copyOf(packet.getData(), numBytesReceived);
	
				
				System.out.print(new String(x,"UTF-8"));

			}
			// Wait for packet

		} catch (SocketException e) {
		} catch (IOException e) {
		}

	}
}
