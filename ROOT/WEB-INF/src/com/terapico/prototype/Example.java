package com.terapico.prototype;

import java.io.DataInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Enumeration;

import javax.comm.CommPortIdentifier;
import javax.comm.SerialPort;

import deltadore.x2d.common.EnrollmentMessage;
import deltadore.x2d.control.AcknowledgmentEvent;
import deltadore.x2d.control.AcknowledgmentListener;
import deltadore.x2d.control.Controller;
import deltadore.x2d.control.DirectController;
import deltadore.x2d.control.EndTransactionEvent;
import deltadore.x2d.control.EndTransactionListener;
import deltadore.x2d.control.Factory;
import deltadore.x2d.control.FrameIoEvent;
import deltadore.x2d.control.FrameReceivedListener;
import deltadore.x2d.control.FrameSentListener;
import deltadore.x2d.control.MeshController;
import deltadore.x2d.control.Message;
import deltadore.x2d.control.MessageReceivedEvent;
import deltadore.x2d.control.MessageReceivedListener;
import deltadore.x2d.control.Node;
import deltadore.x2d.control.NodeDiscoveredEvent;
import deltadore.x2d.control.NodeDiscoveredListener;
import deltadore.x2d.control.RadioFrequency;
import deltadore.x2d.control.Request;
import deltadore.x2d.control.Response;
import deltadore.x2d.control.ResponseStatus;
import deltadore.x2d.light.LightCommandArg;
import deltadore.x2d.light.LightCommandRequest;
import deltadore.x2d.light.LightInfoResponse;
import deltadore.x2d.light.LightSettingArg;
import deltadore.x2d.light.LightSettingRequest;
import deltadore.x2d.light.LightStatusRequest;
import deltadore.x2d.light.LightStatusResponse;
import deltadore.x2d.remote.BasicCommand;
import deltadore.x2d.remote.BasicCommandMessage;
import deltadore.x2d.rollershutter.RollerShutterCommandArg;
import deltadore.x2d.rollershutter.RollerShutterCommandRequest;
import deltadore.x2d.rollershutter.RollerShutterStatusRequest;
import deltadore.x2d.rollershutter.RollerShutterStatusResponse;
import deltadore.x2d.sensor.TemperatureMessage;
import deltadore.x2d.sensor.TemperatureType;

public class Example implements MessageReceivedListener, FrameSentListener, FrameReceivedListener, EndTransactionListener, AcknowledgmentListener, NodeDiscoveredListener {
	
	private Object messageLock;
	private Object txLock;
	protected void waitMessage() throws InterruptedException{
		if(messageLock==null)messageLock=new Object();
		synchronized(messageLock){
			messageLock.wait();
		}
		
	}
	protected void unlockMessage() throws InterruptedException{
		if(messageLock==null)messageLock=new Object();
		synchronized(messageLock){
			messageLock.notify();
		}
	}
	protected void waitTx() throws InterruptedException{
		if(txLock==null)txLock=new Object();
		synchronized(txLock){
			txLock.wait();
		}
	}
	protected void unlockTx() throws InterruptedException{
		if(txLock==null)txLock=new Object();
		synchronized(txLock){
			txLock.notify();
		}
		
	}
	
	
	
	public Example() throws Exception {

		// Choosing port
		DataInputStream input = new DataInputStream(System.in);
		// System.out.print("Port choice (ex: COM1, /dev/ttyUSB0) : ");
		String portName = "/dev/ttyUSB0";

		// Setting port
		CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(portName);
		SerialPort port = (SerialPort) portId.open("X2D example", 2000);
		port.setSerialPortParams(38400, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

		port.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);

		// Creating X2D controller
		Controller controller = Factory.createController();
		controller.addFrameSentListener(this);
		controller.addFrameReceivedListener(this);
		controller.addAcknowledgmentListener(this);

		// Using non-mesh protocol
		DirectController directController = (DirectController) controller;
		directController.addMessageReceivedListener(this);

		// Using mesh protocol
		MeshController meshController = (MeshController) controller;
		meshController.addEndTransactionListener(this);
		meshController.addNodeDiscoveredListener(this);
		
		// Starting X2D controller
		controller.open(port.getInputStream(), port.getOutputStream());
		
		meshController.initNetworks();
		waitMessage();
		//waitTx();
		System.out.println("network count="+meshController.getNetworkCount());

		// Displaying menu
		System.out.println();
		System.out.println("Menu");
		System.out.println("=====================================");
		System.out.println("0 => quit");
		System.out.println("1 => (direct) start enrollment mode");
		System.out.println("2 => (direct) send enrollment request");
		System.out.println("3 => (direct) switch on the light");
		System.out.println("6 => (direct) switch off the light");
		System.out.println("7 => (direct) flash the light");

		System.out.println("4 => (mesh) start node discovery");
		System.out.println("5 => (mesh) switch on the light");
		System.out.println();

		while (true) {
			String menuChoice = input.readLine();
			if (menuChoice.equals("0")) {
				break;
			} else if (menuChoice.equals("1")) {
				System.out.println();
				System.out.println("(direct) starting enrollment mode...");

				directController.setEnrollmentListeningMode(true);
			} else if (menuChoice.equals("2")) {
				System.out.println();
				System.out.println("(direct) sending enrollment request...");

				EnrollmentMessage msg = (EnrollmentMessage) directController.createMessage(EnrollmentMessage.class);
				msg.setSourcePort(0);
				directController.sendMessage(msg, RadioFrequency.AM);
			} else if (menuChoice.equals("3")) {
				System.out.println();
				System.out.println("(direct) switching on the light...");

				BasicCommandMessage msg = (BasicCommandMessage) directController.createMessage(BasicCommandMessage.class);
				msg.setSourcePort(0);
				msg.setCommand(BasicCommand.ON);
				directController.sendMessage(msg, RadioFrequency.AM);
			} else if (menuChoice.equals("6")) {
				System.out.println();
				System.out.println("(direct) switching on the light...");

				BasicCommandMessage msg = (BasicCommandMessage) directController.createMessage(BasicCommandMessage.class);
				msg.setSourcePort(0);
				msg.setCommand(BasicCommand.OFF);
				directController.sendMessage(msg, RadioFrequency.AM);
			} else if (menuChoice.equals("7")) {
				System.out.println();
				System.out.println("(direct) switching on the light...");

				BasicCommandMessage msg = (BasicCommandMessage) directController.createMessage(BasicCommandMessage.class);
				msg.setSourcePort(0);
				msg.setCommand(BasicCommand.TOGGLE);
				directController.sendMessage(msg, RadioFrequency.AM);
			} else if (menuChoice.equals("4")) {
				System.out.println();
				System.out.println("(mesh) starting node discovery...");

				meshController.getNetwork(0).startNodeDiscovery(true);
			} else if (menuChoice.equals("5")) {
				System.out.println();
				System.out.println("(mesh) switching on the light...");

				LightCommandRequest req = (LightCommandRequest) meshController.createRequest(LightCommandRequest.class);
				req.setNetwork(meshController.getNetwork(0));
				req.addNode(Node.NODE0, LightCommandArg.UP);
				req.addNode(Node.NODE1, LightCommandArg.percent(20));

				// LightCommandArg.UP_SLOW
				req.addNode(Node.NODE2, LightCommandArg.UP);
				req.addNode(Node.NODE3, LightCommandArg.UP);
				meshController.beginTransaction(req);
			} else if (menuChoice.equals("8")) {
				System.out.println();
				System.out.println("(mesh) switching on the light...");
				LightCommandRequest req = (LightCommandRequest) meshController.createRequest(LightCommandRequest.class);
				req.setNetwork(meshController.getNetwork(0));
				req.addNode(Node.NODE1, LightCommandArg.UP);
				meshController.beginTransaction(req);
			} else if (menuChoice.equals("9")) {
				System.out.println();
				System.out.println("(mesh) switching on the light...");
				LightCommandRequest req = (LightCommandRequest) meshController.createRequest(LightCommandRequest.class);
				req.setNetwork(meshController.getNetwork(0));
				req.addNode(Node.NODE1, LightCommandArg.DOWN);
				meshController.beginTransaction(req);

			} else if (menuChoice.equals("10")) {
				System.out.println();
				System.out.println("(mesh) switching on the light...");
				LightStatusRequest req = (LightStatusRequest) meshController.createRequest(LightStatusRequest.class);
				System.out.println("network count: " + meshController.getNetworkCount());
				req.setNetwork(meshController.getNetwork(0));
				req.addNode(Node.NODE1, LightCommandArg.NONE);
				meshController.beginTransaction(req);

			} else if (menuChoice.equals("12")) {
				System.out.println();
				System.out.println("(mesh) switching on the light...");
				LightSettingRequest req = (LightSettingRequest) meshController.createRequest(LightSettingRequest.class);
				req.setNetwork(meshController.getNetwork(0));
				req.addNode(Node.NODE1, LightSettingArg.SET_FAVORITE_1);
				meshController.beginTransaction(req);

			} else if (menuChoice.equals("13")) {
				System.out.println();
				meshController.getNetwork(0).deleteAllNodes();
				meshController.getNetwork(0).getTopology();

			} else if (menuChoice.equals("14")) {
				System.out.println();

				Node[] nodes = meshController.getNetwork(0).getTopology();
				for (Node node : nodes) {
					System.out.println("Node: " + node.toInt());

				}

			} else if (menuChoice.equals("15")) {

				System.out.println();
				System.out.println("(mesh) switching on the light...");
				Request req = meshController.createRequest(RollerShutterCommandRequest.class);
				// System.out.println("network count: "+meshController.getNetworkCount());
				req.setNetwork(meshController.getNetwork(0));
				req.addNode(Node.NODE2, RollerShutterCommandArg.UP);
				meshController.beginTransaction(req);

			} else if (menuChoice.equals("17")) {

				System.out.println();
				System.out.println("(mesh) switching on the light...");
				Request req = meshController.createRequest(RollerShutterCommandRequest.class);
				// System.out.println("network count: "+meshController.getNetworkCount());
				req.setNetwork(meshController.getNetwork(0));
				req.addNode(Node.NODE2, RollerShutterCommandArg.DOWN);
				meshController.beginTransaction(req);

			} else if (menuChoice.equals("18")) {

				System.out.println();
				System.out.println("(mesh) switching on the light...");
				Request req = meshController.createRequest(RollerShutterCommandRequest.class);
				// System.out.println("network count: "+meshController.getNetworkCount());
				req.setNetwork(meshController.getNetwork(0));
				req.addNode(Node.NODE2, RollerShutterCommandArg.DOWN_SLOW);
				meshController.beginTransaction(req);

			} else if (menuChoice.equals("16")) {

				System.out.println();
				System.out.println("(mesh) switching on the light...");
				Request req = meshController.createRequest(RollerShutterStatusRequest.class);
				// System.out.println("network count: "+meshController.getNetworkCount());
				req.setNetwork(meshController.getNetwork(0));
				req.addNode(Node.NODE2, RollerShutterCommandArg.NONE);
				meshController.beginTransaction(req);

			} else if (menuChoice.equals("6")) {
				System.out.println();
				System.out.println("(direct) switching OFF the light...");

				BasicCommandMessage msg = (BasicCommandMessage) directController.createMessage(BasicCommandMessage.class);
				msg.setSourcePort(0);
				msg.setCommand(BasicCommand.OFF);
				directController.sendMessage(msg, RadioFrequency.AM);
			}
		}

		controller.close();
		port.close();
		System.exit(0);
	}

	public static void main(String[] args) throws Exception {
		new Example();
	}

	public void messageReceived(MessageReceivedEvent evt) {
		Message msg = evt.getMessage();
		try {
			this.unlockMessage();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (msg instanceof TemperatureMessage) {
			TemperatureMessage tempMsg = (TemperatureMessage) msg;
			double tempValue = tempMsg.getTemperature() / 10.0;

			if (tempMsg.getType() == TemperatureType.INTERNAL) {
				System.out.println("Room temperature = " + tempValue + "�C");
			} else if (tempMsg.getType() == TemperatureType.EXTERNAL) {
				System.out.println("Outdoor temperature " + tempValue + "�C");
			}
		}
	}
	Integer index=1;

	public void endTransaction(EndTransactionEvent evt) {
		try {
			this.unlockTx();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Request request = evt.getRequest();

		for (Enumeration e = request.nodes(); e.hasMoreElements();) {
			Node node = (Node) e.nextElement();
			Response resp = evt.getResponse(node);
			try {
				peekObject(resp);
			} catch (IllegalArgumentException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InvocationTargetException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			printIneterfaces(resp);

			if (resp instanceof LightStatusResponse) {
				LightStatusResponse lightStatusResp = (LightStatusResponse) resp;
				System.out.println(lightStatusResp.getLevel());
			}
			if (resp instanceof LightInfoResponse) {
				LightInfoResponse lightInfoResp = (LightInfoResponse) resp;
				System.out.println(lightInfoResp.getChannelCount());
			}
			if (resp instanceof RollerShutterStatusResponse) {
				RollerShutterStatusResponse lightInfoResp = (RollerShutterStatusResponse) resp;
				System.out.println("RollShuttel Position: "+lightInfoResp.getPosition());
			}
			
			
			ResponseStatus status = resp.getStatus();
			System.out.println(node.toString() + " > " + status.toString());
		}
	}
	public static void invokeAndPrint(Object object,Method method) throws IllegalArgumentException, InvocationTargetException{
		
		Object ret;
		try {
			ret = method.invoke(object, new Class[]{});
			System.out.println(method.getName()+"="+ret);
		} catch (IllegalAccessException e) {
			System.out.println(method.getName()+"=@error@");
		}
		
		
	}
	public static void peekObject(Object object) throws IllegalArgumentException,  InvocationTargetException {
		Class clazz = object.getClass();
		Method[] methods = clazz.getMethods();
		for (Method method : methods) {
			
			if( method.getReturnType().equals(Void.TYPE)){
			    return;
			 }
			if (method.getGenericParameterTypes().length != 0) {
				continue;
			}
			if (method.getName().startsWith("get")) {
				invokeAndPrint(object,method);
			}
			if (method.getName().startsWith("is")) {
				invokeAndPrint(object,method);
			}
			if (method.getName().startsWith("has")) {
				invokeAndPrint(object,method);
			}

		}

	}
	

	public void nodeDiscovered(NodeDiscoveredEvent evt) {
		System.out.println("New node > Net" + evt.getNetwork().getIdentifier() + " " + evt.getNode().toString());
	}

	public void acknowledgment(AcknowledgmentEvent evt) {
		System.out.println("----------------------ACKNOWLEDGMENT-------------------");
		try {
			this.unlockMessage();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(evt.getAcknowledgment().toString());
		
		printIneterfaces(evt.getAcknowledgment());
		printIneterfaces(evt.getSource());

		System.out.println();
	}

	public void printIneterfaces(Object object) {
		System.out.println(object.toString());
		Type[] types = object.getClass().getGenericInterfaces();
		for (Type t : types) {
			System.out.println(t.toString());

		}
	}

	public void frameSent(FrameIoEvent arg0) {
		System.out.println("[tx] > " + arg0.getHexString());
	}

	public void frameReceived(FrameIoEvent arg0) {
		System.out.println("[rx] > " + arg0.getHexString());
	}

}
