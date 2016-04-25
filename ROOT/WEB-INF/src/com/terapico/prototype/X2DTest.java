package com.terapico.prototype;

import java.io.DataInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Enumeration;

import javax.comm.CommPortIdentifier;
import javax.comm.SerialPort;

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
import deltadore.x2d.control.MessageReceivedEvent;
import deltadore.x2d.control.MessageReceivedListener;
import deltadore.x2d.control.Network;
import deltadore.x2d.control.Node;
import deltadore.x2d.control.NodeDiscoveredEvent;
import deltadore.x2d.control.NodeDiscoveredListener;
import deltadore.x2d.control.RadioFrequency;
import deltadore.x2d.control.Request;
import deltadore.x2d.control.Response;
import deltadore.x2d.light.LightCommandArg;
import deltadore.x2d.light.LightCommandRequest;
import deltadore.x2d.light.LightStatusRequest;
import deltadore.x2d.remote.BasicCommand;
import deltadore.x2d.remote.BasicCommandMessage;
import deltadore.x2d.rollershutter.RollerShutterCommandArg;
import deltadore.x2d.rollershutter.RollerShutterCommandRequest;
import deltadore.x2d.rollershutter.RollerShutterStatusRequest;

public class X2DTest  implements MessageReceivedListener, FrameSentListener, FrameReceivedListener, EndTransactionListener, AcknowledgmentListener, NodeDiscoveredListener{
	
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
	
	
	
	public void doInit()throws Exception{
		
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
		this.waitMessage();
		
		for(int i=0;i<meshController.getNetworkCount();i++){
			Network network=meshController.getNetwork(i);
			Node[] nodes=network.getTopology();
			for(Node node:nodes){
				System.out.println("Find Node here: " + node+"@network"+i);
				network.scanNode(node);
				
			}
		}
		Request req;
		for(int i=0;i<100;i++){
			
			req =  meshController.createRequest(LightStatusRequest.class);
			System.out.println("network count: " + meshController.getNetworkCount());
			req.setNetwork(meshController.getNetwork(0));
			req.addNode(Node.NODE1, LightCommandArg.NONE);
			meshController.beginTransaction(req);
			this.waitTx();
			
			req = meshController.createRequest(LightCommandRequest.class);
			req.setNetwork(meshController.getNetwork(0));
			req.addNode(Node.NODE1, LightCommandArg.percent(20));
			meshController.beginTransaction(req);			
			this.waitTx();
			
			req =  meshController.createRequest(LightStatusRequest.class);
			System.out.println("network count: " + meshController.getNetworkCount());
			req.setNetwork(meshController.getNetwork(0));
			req.addNode(Node.NODE1, LightCommandArg.NONE);
			meshController.beginTransaction(req);
			this.waitTx();
			
			
			req =  meshController.createRequest(LightCommandRequest.class);
			req.setNetwork(meshController.getNetwork(0));
			req.addNode(Node.NODE1, LightCommandArg.DOWN);
			meshController.beginTransaction(req);			
			this.waitTx();
			
			
			req =  meshController.createRequest(LightStatusRequest.class);
			System.out.println("network count: " + meshController.getNetworkCount());
			req.setNetwork(meshController.getNetwork(0));
			req.addNode(Node.NODE1, LightCommandArg.NONE);
			meshController.beginTransaction(req);
			this.waitTx();
			
			
			
			req = meshController.createRequest(RollerShutterCommandRequest.class);
			// System.out.println("network count: "+meshController.getNetworkCount());
			req.setNetwork(meshController.getNetwork(0));
			req.addNode(Node.NODE2, RollerShutterCommandArg.DOWN_SLOW);
			meshController.beginTransaction(req);
			this.waitTx();
			
			
			req = meshController.createRequest(RollerShutterStatusRequest.class);
			// System.out.println("network count: "+meshController.getNetworkCount());
			req.setNetwork(meshController.getNetwork(0));
			req.addNode(Node.NODE2, RollerShutterCommandArg.NONE);
			meshController.beginTransaction(req);
			this.waitTx();
			
			
			
			
			req = meshController.createRequest(RollerShutterCommandRequest.class);
			// System.out.println("network count: "+meshController.getNetworkCount());
			req.setNetwork(meshController.getNetwork(0));
			req.addNode(Node.NODE2, RollerShutterCommandArg.UP_SLOW);
			meshController.beginTransaction(req);
			this.waitTx();
			
			
			
			BasicCommandMessage msg = (BasicCommandMessage) directController.createMessage(BasicCommandMessage.class);
			msg.setSourcePort(0);
			msg.setCommand(BasicCommand.ON);
			directController.sendMessage(msg, RadioFrequency.AM);			
			this.waitMessage();
			
			msg = (BasicCommandMessage) directController.createMessage(BasicCommandMessage.class);
			msg.setSourcePort(0);			
			msg.setCommand(BasicCommand.OFF);
			directController.sendMessage(msg, RadioFrequency.AM);
			this.waitMessage();
			
			
			
		}
		
		
		
		System.out.println("system goes here!");
		
		System.exit(0);

		controller.close();
		port.close();
		System.exit(0);
	}
	public static void main(String[] args) throws Exception {
		X2DTest test = new X2DTest();
		test.doInit();
	}

	public void messageReceived(MessageReceivedEvent evt) {
		
		try {
			this.unlockMessage();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	Integer index=1;
	private EndTransactionEvent event;

	public void endTransaction(EndTransactionEvent evt) {
		this.event=evt;
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
		}
		
		
		try {
			this.unlockTx();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
	

	public void nodeDiscovered(NodeDiscoveredEvent evt) {
		System.out.println("New node > Net" + evt.getNetwork().getIdentifier() + " " + evt.getNode().toString());
		
	}

	public void acknowledgment(AcknowledgmentEvent evt) {
		System.out.println(evt.getAcknowledgment().toString());
		try {
			this.unlockMessage();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//printIneterfaces(evt.getAcknowledgment());
		//printIneterfaces(evt.getSource());

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
		if(object==null){
			return;
		}
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

}


