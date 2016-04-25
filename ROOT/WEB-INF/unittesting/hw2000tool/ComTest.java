package hw2000tool;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Enumeration;

import javax.comm.CommPortIdentifier;
import javax.comm.NoSuchPortException;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;
import javax.comm.UnsupportedCommOperationException;

public class ComTest {

	/**
	 * @param args
	 * @throws NoSuchPortException
	 * @throws PortInUseException
	 * @throws UnsupportedCommOperationException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws NoSuchPortException,
			PortInUseException, UnsupportedCommOperationException, IOException,
			InterruptedException {
		// TODO Auto-generated method stub
		// System.out.println(System.getProperty("java.library.path"));
		System.getProperties().list(System.out);
		try {
			System.loadLibrary("win32com");
		} catch (SecurityException localSecurityException) {
			System.err.println("Security Exception win32com: "
					+ localSecurityException);
			return;
		} catch (UnsatisfiedLinkError localUnsatisfiedLinkError) {
			System.err.println("Error loading win32com: "
					+ localUnsatisfiedLinkError);
			return;
		}

		Enumeration<CommPortIdentifier> portList = CommPortIdentifier
				.getPortIdentifiers();
		while (portList.hasMoreElements()) {
			System.out.println("------");
			CommPortIdentifier portId = (CommPortIdentifier) portList
					.nextElement();
			if (CommPortIdentifier.PORT_SERIAL != portId.getPortType()) {
				continue;
			}

			System.out.println("Find Serial Port: " + portId.getName());

		}

		CommPortIdentifier portId = CommPortIdentifier
				.getPortIdentifier("COM4");
		SerialPort port = (SerialPort) portId.open("X2D example", 2000);
		port.setSerialPortParams(9600, SerialPort.DATABITS_8,
				SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

		port.setInputBufferSize(100);
		//port.set
		port.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
		

		OutputStream os = port.getOutputStream();
		// 0A 00 80 0A 00 00 00 00 FF 6C
		InputStream is = port.getInputStream();

		while (true) {

			//byte s[] = hexStringToByteArray("BB55FF0003120A000D0A");
			byte s[] = hexStringToByteArray("AA55FF20031201010D0A");
			
			// byte s[] = hexStringToByteArray("BB55FF0003120A000D0A");

			// byte s[] =
			// hexStringToByteArray("AA5509000B50640009000100000000000D0A");

			os.write(s);

			// InputStream is=communicatorPort.getInputStream();
			// 0A 00 80 0A 00 00 00 00 FF 6C

			Thread.sleep(1000);
			byte buffer[] = new byte[1024];
			int total=0;
			
			while(true){
				int len = is.read(buffer);
				total+=len;
				
				
				
				byte result[]=Arrays.copyOf(buffer, len);
				
				System.out.print(toHexString(result));
				
				
				if(total>=10){
					System.out.println();
					break;
				}
				
				
			}
			
			//System.out.println(System.currentTimeMillis()+":"+toHexString(Arrays.copyOf(buffer, len)));
			
			
			Thread.sleep(3000);

		}

	}

	private static String toHexString(byte[] fieldData) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < fieldData.length; i++) {
			int v = (fieldData[i] & 0xFF);
			if (v <= 0xF) {
				sb.append("0");
			}
			sb.append(Integer.toHexString(v));
		}
		return sb.toString().toUpperCase();
	}

	public static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
					.digit(s.charAt(i + 1), 16));
		}
		return data;
	}

}
