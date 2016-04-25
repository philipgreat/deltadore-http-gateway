package hw2000tool;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public class HWPanelSimulator {

	public static void main(String[] args) throws Exception {
		
		//test.doInit();
		
		CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier("/dev/ttyUSB0");
		SerialPort communicatorPort;
		//controller.doInit("/dev/ttyUSB0");
		communicatorPort = (SerialPort) portId.open("HA-CONTROLLER", 2000);
		communicatorPort.enableReceiveTimeout(2000);
		//communicatorPort.enableReceiveFraming(1); not supported by the current driver.
		communicatorPort.enableReceiveThreshold(1000);
		communicatorPort.setSerialPortParams(38400, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
		communicatorPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
		//communicatorPort.setOutputBufferSize(100);
		//communicatorPort.setInputBufferSize(100);
		
		OutputStream os=communicatorPort.getOutputStream();
		//0A 00 80 0A 00 00 00 00 FF 6C
		InputStream is=communicatorPort.getInputStream();

		
		//InputStream is=communicatorPort.getInputStream();
		//0A 00 80 0A 00 00 00 00 FF 6C
		
		while(true){
			//Thread.sleep(10);
			
			byte s[]=hexStringToByteArray("0a00800a00000000ff6c");
			os.write(s);

			
			byte buffer[]=new byte[1024];
			int len=is.read(buffer);
			if(len>0){

				System.out.println(toHexString(Arrays.copyOf(buffer, len)));
			}
			
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
		return sb.toString();
	}
	
	
	public static byte[] hexStringToByteArray(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}
}
