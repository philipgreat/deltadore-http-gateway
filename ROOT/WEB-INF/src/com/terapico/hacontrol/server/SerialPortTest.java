package com.terapico.hacontrol.server;

import java.util.Enumeration;

import javax.comm.CommPortIdentifier;
import javax.comm.NoSuchPortException;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;
import javax.comm.UnsupportedCommOperationException;

public class SerialPortTest {

	/**
	 * @param args
	 * @throws NoSuchPortException 
	 * @throws PortInUseException 
	 * @throws UnsupportedCommOperationException 
	 */
	public static void main(String[] args) throws NoSuchPortException, PortInUseException, UnsupportedCommOperationException {
		// TODO Auto-generated method stub
		//System.out.println(System.getProperty("java.library.path"));
		System.getProperties().list(System.out);
		try
	    {
	      System.loadLibrary("win32com");
	    } catch (SecurityException localSecurityException) {
	      System.err.println("Security Exception win32com: " + localSecurityException);
	      return;
	    } catch (UnsatisfiedLinkError localUnsatisfiedLinkError) {
	      System.err.println("Error loading win32com: " + localUnsatisfiedLinkError);
	      return;
	    }
		
		Enumeration<CommPortIdentifier> portList = CommPortIdentifier.getPortIdentifiers();
		while (portList.hasMoreElements()) {
			System.out.println("------");
			CommPortIdentifier portId = (CommPortIdentifier) portList.nextElement();
			if (CommPortIdentifier.PORT_SERIAL != portId.getPortType()) {
				continue;
			}

			System.out.println("Find Serial Port: " + portId.getName());

		}
		
		
		CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier("COM3");
		SerialPort port = (SerialPort) portId.open("X2D example", 2000);
		port.setSerialPortParams(38400, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

		port.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
		port.close();
	}

}
