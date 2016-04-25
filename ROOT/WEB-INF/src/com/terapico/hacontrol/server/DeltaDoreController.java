package com.terapico.hacontrol.server;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.comm.CommPortIdentifier;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;

import com.terapico.hacontrol.common.HANode;
import com.terapico.hacontrol.common.HAResponse;
import com.terapico.prototype.X2DTest;

import deltadore.x2d.common.EnrollmentMessage;
import deltadore.x2d.control.Acknowledgment;
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
import deltadore.x2d.control.NodeArg;
import deltadore.x2d.control.NodeDiscoveredEvent;
import deltadore.x2d.control.NodeDiscoveredListener;
import deltadore.x2d.control.RadioFrequency;
import deltadore.x2d.control.Request;
import deltadore.x2d.control.Response;
import deltadore.x2d.light.LightColorArg;
import deltadore.x2d.light.LightColorResponse;
import deltadore.x2d.light.LightCommandArg;
import deltadore.x2d.light.LightCommandRequest;
import deltadore.x2d.light.LightGetColorRequest;
import deltadore.x2d.light.LightInfoRequest;
import deltadore.x2d.light.LightInfoResponse;
import deltadore.x2d.light.LightSetColorRequest;
import deltadore.x2d.light.LightStatusRequest;
import deltadore.x2d.light.LightStatusResponse;
import deltadore.x2d.remote.BasicCommand;
import deltadore.x2d.remote.BasicCommandMessage;
import deltadore.x2d.rollershutter.RollerShutterCommandArg;
import deltadore.x2d.rollershutter.RollerShutterCommandRequest;
import deltadore.x2d.rollershutter.RollerShutterInfoRequest;
import deltadore.x2d.rollershutter.RollerShutterInfoResponse;
import deltadore.x2d.rollershutter.RollerShutterStatusRequest;
import deltadore.x2d.rollershutter.RollerShutterStatusResponse;

public class DeltaDoreController extends SoftHAController implements MessageReceivedListener, FrameSentListener, FrameReceivedListener, EndTransactionListener, AcknowledgmentListener,
		NodeDiscoveredListener,  java.io.Serializable {

	/**
     * 
     */
    private static final long serialVersionUID = 1L;

	private static DeltaDoreController _instance;

	private synchronized HAResponse create(Response response) {
		HAResponse haResponse = new HAResponse();
		haResponse.addValue("status", response.getStatus().toString());
		if (response instanceof LightColorResponse) {
			LightColorResponse lcResp = (LightColorResponse) response;
			haResponse.addValue("blue", lcResp.getBlueValue() + "");
			haResponse.addValue("red", lcResp.getRedValue() + "");
			haResponse.addValue("green", lcResp.getGreenValue() + "");
			return haResponse;
		}
		if (response instanceof LightInfoResponse) {
			LightInfoResponse liResp = (LightInfoResponse) response;
			haResponse.addValue("actuatorType", liResp.getActuatorType().toString() + "");
			haResponse.addValue("channelCount", liResp.getChannelCount() + "");
			haResponse.addBoolValue("multicolor", liResp.isMulticolor());
			return haResponse;
		}
		if (response instanceof LightStatusResponse) {
			LightStatusResponse lsResp = (LightStatusResponse) response;
			haResponse.addValue("level", lsResp.getLevel() + "");
			haResponse.addBoolValue("commandFaulty", lsResp.isCommandFaulty());
			haResponse.addBoolValue("favoritePosition", lsResp.isFavoritePosition());
			haResponse.addBoolValue("overheatingFaulty", lsResp.isOverheatingFaulty());
			haResponse.addBoolValue("overloadFaulty", lsResp.isOverloadFaulty());
			haResponse.addBoolValue("presenceDetected", lsResp.isPresenceDetected());
			haResponse.addBoolValue("twilight", lsResp.isTwilight());

			return haResponse;
		}

		if (response instanceof RollerShutterInfoResponse) {
			RollerShutterInfoResponse rsResp = (RollerShutterInfoResponse) response;
			haResponse.addValue("actuatorType", rsResp.getActuatorType().toString() + "");
			haResponse.addValue("channelCount", rsResp.getChannelCount() + "");
			return haResponse;
		}
		/*
		 * isFavoritePosition=@error@ isIntrusionDetected=@error@
		 * isLoweringFaulty=@error@ isObstacleFaulty=@error@
		 * isOverheatingFaulty=@error@ isRaisingFaulty=@error@
		 */
		if (response instanceof RollerShutterStatusResponse) {
			RollerShutterStatusResponse rssResp = (RollerShutterStatusResponse) response;
			haResponse.addValue("position", rssResp.getPosition() + "");
			haResponse.addBoolValue("favoritePosition", rssResp.isFavoritePosition());
			haResponse.addBoolValue("intrusionDetected", rssResp.isIntrusionDetected());
			haResponse.addBoolValue("loweringFaulty", rssResp.isLoweringFaulty());
			haResponse.addBoolValue("obstacleFaulty", rssResp.isObstacleFaulty());
			haResponse.addBoolValue("overheatingFaulty", rssResp.isOverheatingFaulty());
			haResponse.addBoolValue("raisingFaulty", rssResp.isRaisingFaulty());
			return haResponse;
		}
		// RollerShutterInfoResponse, RollerShutterStatusResponse

		return haResponse;

	}

	private HANode getNodeTypeAndFeature(int network, int node) throws Exception {

		// System.out.println("try do.........................");
		HAResponse response = null;

		response = this.queryLightInfo(network, node);
		System.out.println("response("+network+","+node+"): "+response.toExpression());
		if (response.isOK()) {
			HANode haNode = HANode.createLightNode();
			haNode.setNetworkId(network);
			haNode.setNodeId(node);
			if (response.testBoolean("multicolor")) {
				// haNode.addFeature("color");
				haNode.addProperty("multicolor", "true");
				HAResponse colorReponse = this.getLightColor(network, node);
				haNode.addProperty("red", colorReponse.getStringValue("red"));
				haNode.addProperty("green", colorReponse.getStringValue("green"));
				haNode.addProperty("blue", colorReponse.getStringValue("blue"));

			}
			if (response.testEqual("actuatorType", "STATEFULL")) {
				haNode.addProperty("luminance", "true");
			}
			response = this.queryLightStatus(network, node);
			haNode.addProperty("level", response.getStringValue("level"));
			return haNode;
		}
		response = this.queryRollerShutterInfo(network, node);
		if (response.isOK()) {
			HANode haNode = HANode.createRollerShutterNode();
			haNode.setNetworkId(network);
			haNode.setNodeId(node);
			HAResponse positionReponse = this.queryRollerShutterStatus(network, node);
			if (positionReponse.isOK()) {
				haNode.addProperty("position", positionReponse.getStringValue("position"));
			}
			return haNode;
		}
		//System.out.println("not expected goes here!");
		return HANode.createUnkownNode(network, node);

	}

	private DeltaDoreController() {

	}

	public static synchronized DeltaDoreController instance() {
		if (_instance == null) {
			_instance = new DeltaDoreController();
		}
		return _instance;

	}

	private int networkCount = 0;

	public int getNetworkCount() {
		return networkCount;
	}

	public void setNetworkCount(int count) {
		this.networkCount = count;
	}

	static Map<String, Object> commandMap = null;

	static {
		System.setProperty("java.class.path", System.getProperty("java.class.path").replace('/', File.separatorChar));

	}

	protected Object findParameter(String parameter) {
		return commandMap.get(parameter);
	}

	public void doInit() throws Exception {

		CommPortIdentifier selectPortId = null;
		int counter = 0;
		Enumeration<CommPortIdentifier> portList = CommPortIdentifier.getPortIdentifiers();
		while (portList.hasMoreElements()) {

			CommPortIdentifier portId = (CommPortIdentifier) portList.nextElement();
			if (CommPortIdentifier.PORT_SERIAL != portId.getPortType()) {
				continue;
			}

			counter++;
			selectPortId = portId;
			System.out.println("Found Serial Port: " + portId.getName());

		}

		if (selectPortId == null) {

			String message = "System has tried to find all the serial ports, but nothing found. The reason may be:\r\n"
					+ "1. You are using a modern PC/Laptop without a serial port, you can install driver, please go to http://www.ftdichip.com/Drivers/VCP.htm to install one;\r\n"
					+ "2. You are using a 64-bit operating system and 64-bit JRE/JDK, you can use 32-bit JDK/JRE on 64-bit system\r\n"
					+ "3. You did nothing above, but you forget plugging the USB communicator/emitter in.\r\n";

			throw new IllegalStateException(message);

		}
		if (counter > 1) {
			String message = "System found " + counter + " serial ports, in this case, you have to set the port name, please select one from system log";
			throw new IllegalStateException(message);
		}

		try {
			genericInit(selectPortId);
		} catch (PortInUseException e) {
			String message = "System found the only serial port: " + selectPortId.getName() + " is in use, current owner is " + selectPortId.getCurrentOwner();
			throw new IllegalStateException(message);
		}

	}

	public void doInit(String selectedPort) throws Exception {
		// TODO Auto-generated method stub
		String portName = selectedPort;

		// Setting port
		CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(portName);
		// TODO: add more check.
		genericInit(portId);

	}

	private Controller controller;

	private SerialPort communicatorPort;

	protected void genericInit(CommPortIdentifier portId) throws Exception {

		communicatorPort = (SerialPort) portId.open("HA-CONTROLLER", 2000);
		communicatorPort.setSerialPortParams(38400, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
		communicatorPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
		communicatorPort.setOutputBufferSize(100);

		controller = Factory.createController();
		controller.open(communicatorPort.getInputStream(), communicatorPort.getOutputStream());

		controller.addFrameSentListener(this);
		controller.addFrameReceivedListener(this);
		controller.addAcknowledgmentListener(this);

		DirectController directController = (DirectController) controller;
		directController.addMessageReceivedListener(this);

		MeshController meshController = (MeshController) controller;
		meshController.addEndTransactionListener(this);
		meshController.addNodeDiscoveredListener(this);

		meshController.initNetworks();
		this.waitAcknowledgement();

		setNetworkCount(meshController.getNetworkCount());
		updateTopology();
		
		
		System.out.println("The controller is started with '"+this.getNetworkCount()+"' networks.");

	}



	protected void updateTopology() {
		this.setNodeTopology(HAResponse.getOKStatus());
		MeshController meshController = (MeshController) controller;
		
		for (int i = 0; i < this.getNetworkCount(); i++) {
			Node nodes[] = meshController.getNetwork(i).getTopology();
			for (int j = 0; j < nodes.length; j++) {
				HANode haNode;
				try {
					haNode = getNodeTypeAndFeature(i, j);
					getNodeTopology().addNodeInfo(haNode);
				} catch (Exception e) {
					System.out.println("Got an error: " + e.getMessage());
					continue;
				}

			}

		}

		// _nodeTopology.addValue("topology","{"+stringBuffer.toString()+"}");
	}

	public void doDestroy() throws Exception {
		this.communicatorPort.close();
		// this.controller.close();

	}

	public synchronized void startDirectListen() throws Exception {

		DirectController directController = (DirectController) controller;
		directController.setEnrollmentListeningMode(true);
		// this.waitAcknowledgement();

	}

	public synchronized void clearEnrolledSources() throws Exception {

		DirectController directController = (DirectController) controller;
		directController.clearEnrolledSources();
		// this.waitAcknowledgement();

	}

	public synchronized void sendEnrollmentRequest(int port) throws Exception {
		// TODO check the range of the port
		if (port > 255) {
			throw new IllegalArgumentException("sendEnrollmentRequest(int port): param port is expected 0 to 255, now it is: " + port);
		}
		DirectController directController = (DirectController) controller;
		EnrollmentMessage msg = (EnrollmentMessage) directController.createMessage(EnrollmentMessage.class);
		msg.setSourcePort(port);
		directController.sendMessage(msg, RadioFrequency.AM);
		this.waitAcknowledgement();

	}

	public HAResponse queryVersionInfo() throws Exception {
		return HAResponse.getOKStatus().addValue("version", "hardcontroller/0.5 build@20130331");
	}

	public synchronized HAResponse switchOnLight(int network, int node) throws Exception {

		super.switchOnLight(network, node);
		emitLightCommand(network,node,LightCommandArg.UP);
		

		HAResponse response = getSingleReponse();
		if (response.isOK()) {
		    getNodeTopology().updateNodeStatus(network, node, "level", "100");
		}
		return response;

	}
	public synchronized HAResponse switchOnLights(int network, int nodes[]) throws Exception {

		super.switchOnLights(network, nodes);
		emitLightCommands(network,nodes,LightCommandArg.UP);
		

		HAResponse response = getSingleReponse();
		if (response.isOK()) {
			for(int node:nodes){
				getNodeTopology().updateNodeStatus(network, node, "level", "100");
			}
		}
		return response;

	}

	public synchronized HAResponse switchOffLights(int network, int nodes[]) throws Exception {
		super.switchOffLights(network, nodes);
		emitLightCommands(network,nodes,LightCommandArg.DOWN);
		
		HAResponse response = getSingleReponse();
		if (response.isOK()) {
			for(int node:nodes){
				this.getNodeTopology().updateNodeStatus(network, node, "level", "0");
			}
			
		}
		return response;

	}
	public synchronized HAResponse switchOffLight(int network, int node) throws Exception {
		super.switchOffLight(network, node);
		emitLightCommand(network,node,LightCommandArg.DOWN);
		
		HAResponse response = getSingleReponse();
		if (response.isOK()) {
			this.getNodeTopology().updateNodeStatus(network, node, "level", "0");
		}
		return response;

	}
	
	public synchronized HAResponse standOutLight(int network, int node) throws Exception {
		super.standOutLight(network, node);
		emitLightCommand(network,node,LightCommandArg.STAND_OUT);		
		HAResponse response = getSingleReponse();
		
		return response;

	}
	
	public synchronized HAResponse stopLight(int network, int node) throws Exception {
		super.stopLight(network, node);
		emitLightCommand(network,node,LightCommandArg.STOP);
		
		HAResponse response = getSingleReponse();
		if (response.isOK()) {
			this.getNodeTopology().updateNodeStatus(network, node, "level", "0");
			//TODO: here may be issue!
		}
		return response;

	}
	public synchronized HAResponse toggleLight(int network, int node) throws Exception {
		super.toggleLight(network, node);
		emitLightCommand(network,node,LightCommandArg.TOGGLE);
		
		HAResponse response = getSingleReponse();
		if (response.isOK()) {
			Integer value=this.getNodeTopology().getNodeStatusInt(network, node, "level");
			if(value!=null){
				this.getNodeTopology().updateNodeStatus(network, node, "level", (int)Math.abs(value-100)+"");
			}
		}
		return response;

	}
	public synchronized HAResponse rampUpLight(int network, int node) throws Exception {
		super.rampUpLight(network, node);
		emitLightCommand(network,node,LightCommandArg.UP_SLOW);
		
		HAResponse response = getSingleReponse();
		if (response.isOK()) {
			this.getNodeTopology().updateNodeStatus(network, node, "level", "100");
		}
		return response;
	}
	public synchronized HAResponse rampDownLight(int network, int node) throws Exception {
		super.rampDownLight(network, node);
		emitLightCommand(network,node,LightCommandArg.DOWN_SLOW);
		
		HAResponse response = getSingleReponse();
		if (response.isOK()) {
			this.getNodeTopology().updateNodeStatus(network, node, "level", "0");
		}
		return response;

	}
	


	public synchronized HAResponse setLightColor(int network, int node, int red, int green, int blue) throws Exception {
		super.setLightColor(network, node, red, green, blue);

		MeshController meshController = (MeshController) controller;

		Request req = meshController.createRequest(LightSetColorRequest.class);
		req.setNetwork(meshController.getNetwork(network));
		req.addNode(Node.valueOf(node), new LightColorArg(red, green, blue));

		meshController.beginTransaction(req);
		this.waitTransaction();
		HAResponse response = getSingleReponse();
		if (response.isOK()) {
			this.getNodeTopology().updateNodeStatus(network, node, "red", "" + red);
			this.getNodeTopology().updateNodeStatus(network, node, "blue", "" + blue);
			this.getNodeTopology().updateNodeStatus(network, node, "green", "" + green);
		}
		return response;

	}

	public synchronized HAResponse getLightColor(int network, int node) throws Exception {
		super.getLightColor(network, node);

		MeshController meshController = (MeshController) controller;

		Request req = meshController.createRequest(LightGetColorRequest.class);
		req.setNetwork(meshController.getNetwork(network));
		req.addNode(Node.valueOf(node), NodeArg.NONE);
		meshController.beginTransaction(req);

		this.waitTransaction();
		return getSingleReponse();

	}

	public synchronized HAResponse adjustLight(int network, int node, int percent) throws Exception {
		super.adjustLight(network, node, percent);
		emitLightCommand(network,node,LightCommandArg.percent(percent));
		
		HAResponse response = getSingleReponse();
		if (response.isOK()) {
			this.getNodeTopology().updateNodeStatus(network, node, "level", "" + percent);
		}
		return response;
	}
	//exact the same function with adjust light
	public synchronized HAResponse setLightLevel(int network, int node, int level) throws Exception {
		super.setLightLevel(network, node, level);
		emitLightCommand(network,node,LightCommandArg.percent(level));
		
		HAResponse response = getSingleReponse();
		if (response.isOK()) {
			this.getNodeTopology().updateNodeStatus(network, node, "level", "" + level);
		}
		return response;
	}
	
	protected void emitLightCommand(int network, int node,LightCommandArg commandArg)throws Exception
	{
		
		super.emitLightCommand(network,node,commandArg);
		MeshController meshController = (MeshController) controller;
		Request req = meshController.createRequest(LightCommandRequest.class);
		req.setNetwork(meshController.getNetwork(network));
		req.addNode(Node.valueOf(node), commandArg);
		meshController.beginTransaction(req);	
		this.waitTransaction();
	}

	protected void emitLightCommands(int network, int nodes[],LightCommandArg commandArg)throws Exception
	{
		
		super.emitLightCommands(network,nodes,commandArg);
		MeshController meshController = (MeshController) controller;
		Request req = meshController.createRequest(LightCommandRequest.class);
		req.setNetwork(meshController.getNetwork(network));
		for(int node:nodes){
			req.addNode(Node.valueOf(node), commandArg);
		}		
		meshController.beginTransaction(req);	
		this.waitTransaction();
	}
	public synchronized HAResponse openRollerShutter(int network, int node) throws Exception {
		super.openRollerShutter(network, node);
		emitRollerShutterCommand(network,node,RollerShutterCommandArg.UP);

		HAResponse response = getSingleReponse();
		if (response.isOK()) {
			this.getNodeTopology().updateNodeStatus(network, node, "position", "100");
		}
		return response;
	}

	public synchronized HAResponse openRollerShutterSlowly(int network, int node) throws Exception {
		super.openRollerShutterSlowly(network, node);
		emitRollerShutterCommand(network,node,RollerShutterCommandArg.UP_SLOW);

		HAResponse response = getSingleReponse();
		if (response.isOK()) {
			this.getNodeTopology().updateNodeStatus(network, node, "position", "100");
		}
		return response;
	}

	public synchronized HAResponse closeRollerShutter(int network, int node) throws Exception {
		super.closeRollerShutter(network, node);
		emitRollerShutterCommand(network,node,RollerShutterCommandArg.DOWN);


		HAResponse response = getSingleReponse();
		if (response.isOK()) {
			this.getNodeTopology().updateNodeStatus(network, node, "position", "0");
		}
		return response;
	}

	public synchronized HAResponse stopRollerShutter(int network, int node) throws Exception {
		super.stopRollerShutter(network, node);
		emitRollerShutterCommand(network,node,RollerShutterCommandArg.STOP);

		HAResponse response = getSingleReponse();
		if (response.isOK()) {
			this.getNodeTopology().updateNodeStatus(network, node, "position", "0");
		}
		return response;
	}
	
	
	public synchronized HAResponse toggleRollerShutter(int network, int node) throws Exception {
		super.toggleRollerShutter(network, node);

		emitRollerShutterCommand(network,node,RollerShutterCommandArg.TOGGLE);

		HAResponse response = getSingleReponse();
		if (response.isOK()) {
			this.getNodeTopology().updateNodeStatus(network, node, "position", "0");
		}
		return response;
	}
	public synchronized HAResponse setRollerShutterPosition(int network, int node,int position) throws Exception {
		super.setRollerShutterPosition(network, node,position);

		emitRollerShutterCommand(network,node,RollerShutterCommandArg.percent(position));

		HAResponse response = getSingleReponse();
		if (response.isOK()) {
			this.getNodeTopology().updateNodeStatus(network, node, "position", ""+position);
		}
		return response;
	}
	protected void emitRollerShutterCommand(int network, int node,RollerShutterCommandArg command) throws Exception{
		
		super.emitRollerShutterCommand(network,node,command);	
		MeshController meshController = (MeshController) controller;
		Request req = meshController.createRequest(RollerShutterCommandRequest.class);
		// System.out.println("network count: "+meshController.getNetworkCount());
		req.setNetwork(meshController.getNetwork(network));
		req.addNode(Node.valueOf(node), command);
		meshController.beginTransaction(req);

		this.waitTransaction();		
		
	}
	//
	public synchronized HAResponse standOutRollerShutter(int network, int node) throws Exception {
		super.standOutRollerShutter(network, node);	
		emitRollerShutterCommand(network,node,RollerShutterCommandArg.STAND_OUT);
		
		HAResponse response = getSingleReponse();
		if (response.isOK()) {
			this.getNodeTopology().updateNodeStatus(network, node, "position", "0");
		}
		return response;
	}
	
	// queryRollerShutterStatus
	/*
	 * req = meshController.createRequest(RollerShutterStatusRequest.class); //
	 * System.out.println("network count: "+meshController.getNetworkCount());
	 * req.setNetwork(meshController.getNetwork(0)); req.addNode(Node.NODE2,
	 * RollerShutterCommandArg.NONE); meshController.beginTransaction(req);
	 * this.waitTx();
	 */
	public synchronized HAResponse queryRollerShutterStatus(int network, int node) throws Exception {
		super.queryRollerShutterStatus(network, node);

		MeshController meshController = (MeshController) controller;
		Request req = meshController.createRequest(RollerShutterStatusRequest.class);
		// System.out.println("network count: "+meshController.getNetworkCount());
		req.setNetwork(meshController.getNetwork(network));
		req.addNode(Node.valueOf(node), RollerShutterCommandArg.NONE);
		meshController.beginTransaction(req);
		this.waitTransaction();
		return getSingleReponse();
	}

	public synchronized HAResponse queryTopology() throws Exception {
		updateTopology();
		return this.getNodeTopology();
	}

	public synchronized HAResponse queryCachedTopology() throws Exception {
		return this.getNodeTopology();
	}

	public synchronized HAResponse queryLightStatus(int network, int node) throws Exception {
		super.queryLightStatus(network, node);

		MeshController meshController = (MeshController) controller;
		Request req = meshController.createRequest(LightStatusRequest.class);
		// System.out.println("network count: "+meshController.getNetworkCount());
		req.setNetwork(meshController.getNetwork(network));
		req.addNode(Node.valueOf(node), NodeArg.NONE);
		meshController.beginTransaction(req);
		this.waitTransaction();
		return getSingleReponse();
	}

	public synchronized HAResponse queryLightInfo(int network, int node) throws Exception {
		super.queryLightInfo(network, node);

		MeshController meshController = (MeshController) controller;
		Request req = meshController.createRequest(LightInfoRequest.class);
		// System.out.println("network count: "+meshController.getNetworkCount());
		req.setNetwork(meshController.getNetwork(network));
		req.addNode(Node.valueOf(node), NodeArg.NONE);
		meshController.beginTransaction(req);
		this.waitTransaction();
		return getSingleReponse();
	}

	public synchronized HAResponse queryRollerShutterInfo(int network, int node) throws Exception {
		super.queryRollerShutterInfo(network, node);
		MeshController meshController = (MeshController) controller;
		Request req = meshController.createRequest(RollerShutterInfoRequest.class);
		// System.out.println("network count: "+meshController.getNetworkCount());
		req.setNetwork(meshController.getNetwork(network));
		req.addNode(Node.valueOf(node), NodeArg.NONE);
		meshController.beginTransaction(req);
		this.waitTransaction();
		return getSingleReponse();
	}

	public synchronized HAResponse closeRollerShutterSlowly(int network, int node) throws Exception {
		super.closeRollerShutterSlowly(network, node);
		emitRollerShutterCommand(network,node,RollerShutterCommandArg.DOWN_SLOW);

		HAResponse response = getSingleReponse();
		if (response.isOK()) {
			this.getNodeTopology().updateNodeStatus(network, node, "position", "0");
		}
		return response;
	}

	public synchronized HAResponse startNodeDiscovery(int network) throws InterruptedException {
		super.startNodeDiscovery(network);
		MeshController meshController = (MeshController) controller;
		meshController.getNetwork(network).startNodeDiscovery(true);
		currentFountNodeInfo = null;

		this.waitAcknowledgement();
		// waitNodeDiscoveryLock();

		if (currentFountNodeInfo == null) {
			return HAResponse.getOKStatus();
		}

		HANode currentFoundNode;
		try {
			currentFoundNode = getNodeTypeAndFeature(currentFountNodeInfo[0], currentFountNodeInfo[1]);
			getNodeTopology().addNodeInfo(currentFoundNode);
			HAResponse response = HAResponse.getOKStatus();
			response.addNodeInfo(currentFoundNode);
			return response;

		} catch (Exception e) {

			HAResponse response = HAResponse.getErrorStatus(e.getMessage());

			return response;
		}

	}

	public void deleteAllNodes(int network) throws InterruptedException {
		super.deleteAllNodes(network);
		MeshController meshController = (MeshController) controller;
		meshController.getNetwork(network).deleteAllNodes();
		this.waitAcknowledgement();
		//this.getNodeTopology().resetNodes();
		this.getNodeTopology().deleteNodesFromNetwork(network);
	}

	public void deleteNode(int network, int node) throws InterruptedException {
		super.deleteNode(network, node);
		MeshController meshController = (MeshController) controller;
		meshController.getNetwork(network).deleteNode(Node.valueOf(node), true);
		this.waitAcknowledgement();
		this.getNodeTopology().removeNode(network, node);
	}

	private Object acknowledgeLock;

	private Object transactionLock;

	protected void waitAcknowledgement() throws InterruptedException {
		if (acknowledgeLock == null)
			acknowledgeLock = new Object();
		synchronized (acknowledgeLock) {
			acknowledgeLock.wait();
		}

	}

	protected void unlockAcknowledge() throws InterruptedException {
		if (acknowledgeLock == null)
			acknowledgeLock = new Object();
		synchronized (acknowledgeLock) {
			acknowledgeLock.notifyAll();
		}
	}

	protected void waitTransaction() throws InterruptedException {
		if (transactionLock == null)
			transactionLock = new Object();
		synchronized (transactionLock) {
			transactionLock.wait();
		}
	}

	protected void unlockTransaction() throws InterruptedException {
		if (transactionLock == null)
			transactionLock = new Object();
		synchronized (transactionLock) {
			transactionLock.notifyAll();
		}
	}

	public static void main(String[] args) throws Exception {
		X2DTest test = new X2DTest();
		test.doInit();
	}

	public void messageReceived(MessageReceivedEvent evt) {

		try {
			this.unlockAcknowledge();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	Integer index = 1;

	private EndTransactionEvent event;

	private Response _singleReponse;

	protected HAResponse getSingleReponse() {

		return this.create(_singleReponse);

	}

	public void endTransaction(EndTransactionEvent evt) {
		this.event = evt;
		Request request = evt.getRequest();

		int counter = 0;
		Response temporaryReponse = null;
		for (Enumeration e = request.nodes(); e.hasMoreElements();) {
			Node node = (Node) e.nextElement();
			temporaryReponse = evt.getResponse(node);
			counter++;

		}
		if (counter == 1) {
			_singleReponse = temporaryReponse;
		}

		try {
			this.unlockTransaction();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void endTransaction2(EndTransactionEvent evt) {
		this.event = evt;
		Request request = evt.getRequest();

		for (Enumeration e = request.nodes(); e.hasMoreElements();) {
			Node node = (Node) e.nextElement();
			Response resp = evt.getResponse(node);
			try {
				peekObject(resp);
				peekObject(resp.getStatus());

			} catch (IllegalArgumentException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InvocationTargetException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		try {
			this.unlockTransaction();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// HANode currentFoundNode=null;

	private Object nodeDiscoveryLock;

	protected void waitNodeDiscoveryLock() throws InterruptedException {
		if (nodeDiscoveryLock == null)
			nodeDiscoveryLock = new Object();
		synchronized (nodeDiscoveryLock) {
			nodeDiscoveryLock.wait();
		}

	}

	protected void unlockNodeDiscoveryLock() throws InterruptedException {
		if (nodeDiscoveryLock == null)
			nodeDiscoveryLock = new Object();
		synchronized (nodeDiscoveryLock) {
			nodeDiscoveryLock.notifyAll();
		}
	}

	private int[] currentFountNodeInfo = null;

	public void nodeDiscovered(NodeDiscoveredEvent evt) {
		System.out.println("New node > Net" + evt.getNetwork().getIdentifier() + " " + evt.getNode().toString());

		currentFountNodeInfo = new int[2];
		currentFountNodeInfo[0] = evt.getNetwork().getIdentifier();
		currentFountNodeInfo[1] = evt.getNode().toInt();

	}

	public void acknowledgment(AcknowledgmentEvent evt) {
		System.out.println("acknowledgment(AcknowledgmentEvent evt) : "+evt.getAcknowledgment().toString());

		if (evt.getAcknowledgment() != Acknowledgment.ACK) {
			//System.err.print("Found error here, ");
			//System.exit(0);
			return;
		}

		try {
			this.unlockAcknowledge();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// printIneterfaces(evt.getAcknowledgment());
		// printIneterfaces(evt.getSource());

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

	public static void invokeAndPrint(Object object, Method method) throws IllegalArgumentException, InvocationTargetException {

		Object ret;
		try {
			ret = method.invoke(object, new Class[] {});
			System.out.println(method.getName() + "=" + ret);
		} catch (IllegalAccessException e) {
			System.out.println(method.getName() + "=@error@");
		}

	}

	public static void peekObject(Object object) throws IllegalArgumentException, InvocationTargetException {
		if (object == null) {
			return;
		}
		Class clazz = object.getClass();
		Method[] methods = clazz.getMethods();
		for (Method method : methods) {
			if (method.getReturnType().equals(Void.TYPE)) {
				continue;
			}
			if (method.getGenericParameterTypes().length != 0) {
				continue;
			}
			if (method.getName().startsWith("get")) {
				invokeAndPrint(object, method);
			}
			if (method.getName().startsWith("is")) {
				invokeAndPrint(object, method);
			}
			if (method.getName().startsWith("has")) {
				invokeAndPrint(object, method);
			}
			if (method.getName().startsWith("toString")) {
				invokeAndPrint(object, method);
			}

		}

	}

}
