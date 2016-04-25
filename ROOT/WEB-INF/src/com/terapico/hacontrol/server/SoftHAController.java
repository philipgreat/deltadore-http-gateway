package com.terapico.hacontrol.server;

import com.terapico.hacontrol.common.HANode;
import com.terapico.hacontrol.common.HAResponse;

import deltadore.x2d.light.LightCommandArg;
import deltadore.x2d.rollershutter.RollerShutterCommandArg;

public class SoftHAController implements HAController, java.io.Serializable{
	
	/**
     * 
     */
    private static final long serialVersionUID = 1L;
	private HAResponse _nodeTopology;
	public void doInit() throws Exception {
		// TODO Auto-generated method stub
	    internalInit();
	}
	private void internalInit()
	{
	    _nodeTopology=HAResponse.getOKStatus();
	    
	    _nodeTopology.addNodeInfo(HANode.createLightNode(11, 0));
	    _nodeTopology.addNodeInfo(HANode.createLightNode(11, 1));
	    _nodeTopology.addNodeInfo(HANode.createLightNode(11, 2));
	    _nodeTopology.addNodeInfo(HANode.createLightNode(11, 3));
	    
	    _nodeTopology.addNodeInfo(HANode.createLightNodeWithLuminarcControl(11, 4));
	    _nodeTopology.addNodeInfo(HANode.createLightNodeWithLuminarcControl(11, 5));
	    _nodeTopology.addNodeInfo(HANode.createLightNodeWithLuminarcControl(11, 6));
	    _nodeTopology.addNodeInfo(HANode.createLightNodeWithLuminarcControl(11, 7));
	    
	    _nodeTopology.addNodeInfo(HANode.createRollerShutterNode(11, 8));
	    _nodeTopology.addNodeInfo(HANode.createRollerShutterNode(11, 9));
	    _nodeTopology.addNodeInfo(HANode.createRollerShutterNode(11, 10));
	    _nodeTopology.addNodeInfo(HANode.createRollerShutterNode(11, 11));
 
	}
	/* (non-Javadoc)
	 * @see com.terapico.hacontrol.DeltaDoreControler#setNetworkCount(int)
	 */
	
	/* (non-Javadoc)
	 * @see com.terapico.hacontrol.DeltaDoreControler#startDirectListen()
	 */
	
	/**
	 * @return the nodeTopology
	 */
	protected HAResponse getNodeTopology() {
	    return _nodeTopology;
	}

	/**
	 * @param nodeTopology the nodeTopology to set
	 */
	protected void setNodeTopology(HAResponse nodeTopology) {
	    _nodeTopology = nodeTopology;
	}

	public synchronized void startDirectListen() throws Exception {
		// TODO Auto-generated method stub
		//super.startDirectListen();
	}

	/* (non-Javadoc)
	 * @see com.terapico.hacontrol.DeltaDoreControler#switchOnLight(int, int)
	 */
	public synchronized HAResponse switchOnLight(int network, int node) throws Exception {
		verifyNetwork(network);
		verifyNode(node);
		getNodeTopology().updateNodeStatus(network, node, "level", "100");
		// TODO Auto-generated method stub
		return HAResponse.getOKStatus();
	}

	/* (non-Javadoc)
	 * @see com.terapico.hacontrol.DeltaDoreControler#switchOffLight(int, int)
	 */
	public synchronized HAResponse switchOffLight(int network, int node) throws Exception {
		verifyNetwork(network);
		verifyNode(node);
		// TODO Auto-generated method stub
		getNodeTopology().updateNodeStatus(network, node, "level", "0");
		// TODO Auto-generated method stub
		return HAResponse.getOKStatus();
	}

	/* (non-Javadoc)
	 * @see com.terapico.hacontrol.DeltaDoreControler#startNodeDiscovery(int)
	 */
	public synchronized HAResponse startNodeDiscovery(int network) throws InterruptedException {
		// TODO Auto-generated method stub
		return HAResponse.getOKStatus();
		
	}

	public void doInit(String communicationResource) throws Exception {
		// TODO Auto-generated method stub
	    internalInit();
	}

	public void doDestroy() throws Exception {
		// TODO Auto-generated method stub
		
	}
	/**
	 * @throws Exception
	 * @see com.terapico.hacontrol.server.DeltaDoreController#clearEnrolledSources()
	 */
	public void clearEnrolledSources() throws Exception {

	}

	/**
	 * @param port
	 * @throws Exception
	 * @see com.terapico.hacontrol.server.DeltaDoreController#sendEnrollmentRequest(int)
	 */
	public void sendEnrollmentRequest(int port) throws Exception {

	}

	/**
	 * @param network
	 * @param node
	 * @param red
	 * @param green
	 * @param blue
	 * @return
	 * @throws Exception
	 * @see com.terapico.hacontrol.server.DeltaDoreController#setLightColor(int, int, int, int, int)
	 */
	public HAResponse setLightColor(int network, int node, int red, int green, int blue) throws Exception {
		verifyNetwork(network);
		verifyNode(node);
		verifyColor(red);
		verifyColor(green);
		verifyColor(blue);

		this.getNodeTopology().updateNodeStatus(network, node, "red", "" + red);
		this.getNodeTopology().updateNodeStatus(network, node, "blue", "" + blue);
		this.getNodeTopology().updateNodeStatus(network, node, "green", "" + green);
		return HAResponse.getOKStatus();
	}

	/**
	 * @param network
	 * @param node
	 * @return
	 * @throws Exception
	 * @see com.terapico.hacontrol.server.DeltaDoreController#getLightColor(int, int)
	 */
	public HAResponse getLightColor(int network, int node) throws Exception {
		verifyNetwork(network);
		verifyNode(node);
		return HAResponse.getOKStatus();
	}

	/**
	 * @param network
	 * @param node
	 * @param percent
	 * @return
	 * @throws Exception
	 * @see com.terapico.hacontrol.server.DeltaDoreController#adjustLight(int, int, int)
	 */
	public HAResponse adjustLight(int network, int node, int percent) throws Exception {
		verifyNetwork(network);
		verifyNode(node);
		verifyPercent(percent);
		this.getNodeTopology().updateNodeStatus(network, node, "level", "" + percent);
		return HAResponse.getOKStatus();
	}
	public HAResponse setLightLevel(int network, int node, int level) throws Exception {
		verifyNetwork(network);
		verifyNode(node);
		verifyPercent(level);
		this.getNodeTopology().updateNodeStatus(network, node, "level", "" + level);
		return HAResponse.getOKStatus();
	}
	protected void verifyPercent(int percent) {
		// TODO Auto-generated method stub
		if (percent >= 0 && percent <= 100) {
			return;
		}
		throw new IllegalArgumentException("percent number: '" + percent + "' is not in range[0,100]");
	}
	

	/**
	 * @param network
	 * @param node
	 * @return
	 * @throws Exception
	 * @see com.terapico.hacontrol.server.DeltaDoreController#openRollerShutter(int, int)
	 */
	public HAResponse openRollerShutter(int network, int node) throws Exception {
		verifyNetwork(network);
		verifyNode(node);
		this.getNodeTopology().updateNodeStatus(network, node, "position", "100");
		return HAResponse.getOKStatus();
	}

	/**
	 * @param network
	 * @param node
	 * @return
	 * @throws Exception
	 * @see com.terapico.hacontrol.server.DeltaDoreController#openRollerShutterSlowly(int, int)
	 */
	public HAResponse openRollerShutterSlowly(int network, int node) throws Exception {
		verifyNetwork(network);
		verifyNode(node);
		this.getNodeTopology().updateNodeStatus(network, node, "position", "100");
		return HAResponse.getOKStatus();
	}

	/**
	 * @param network
	 * @param node
	 * @return
	 * @throws Exception
	 * @see com.terapico.hacontrol.server.DeltaDoreController#closeRollerShutter(int, int)
	 */
	public HAResponse closeRollerShutter(int network, int node) throws Exception {
		verifyNetwork(network);
		verifyNode(node);
		this.getNodeTopology().updateNodeStatus(network, node, "position", "0");
		return HAResponse.getOKStatus();
	}

	/**
	 * @param network
	 * @param node
	 * @return
	 * @throws Exception
	 * @see com.terapico.hacontrol.server.DeltaDoreController#queryRollerShutterStatus(int, int)
	 */
	public HAResponse queryRollerShutterStatus(int network, int node) throws Exception {
		verifyNetwork(network);
		verifyNode(node);
		return HAResponse.getOKStatus();
	}

	/**
	 * @return
	 * @throws Exception
	 * @see com.terapico.hacontrol.server.DeltaDoreController#queryTopology()
	 */
	public HAResponse queryTopology() throws Exception {
		
		return this.getNodeTopology();
	}
	public HAResponse queryCachedTopology() throws Exception {
		
		return this.getNodeTopology();
	}
	/**
	 * @param network
	 * @param node
	 * @return
	 * @throws Exception
	 * @see com.terapico.hacontrol.server.DeltaDoreController#queryLightStatus(int, int)
	 */
	public HAResponse queryLightStatus(int network, int node) throws Exception {
		verifyNetwork(network);
		verifyNode(node);
		return HAResponse.getOKStatus().putNodeToStatusReponse(this.getNodeTopology(),network, node);
	}

	/**
	 * @param network
	 * @param node
	 * @return
	 * @throws Exception
	 * @see com.terapico.hacontrol.server.DeltaDoreController#queryLightInfo(int, int)
	 */
	public HAResponse queryLightInfo(int network, int node) throws Exception {
		verifyNetwork(network);
		verifyNode(node);
		return HAResponse.getOKStatus().putNodeToStatusReponse(this.getNodeTopology(),network, node);
	}
	
	
	
	public HAResponse queryVersionInfo() throws Exception {
		
		return HAResponse.getOKStatus().addValue("version", "softcontroller1.0");
	}

	/**
	 * @param network
	 * @param node
	 * @return
	 * @throws Exception
	 * @see com.terapico.hacontrol.server.DeltaDoreController#queryRollerShutterInfo(int, int)
	 */
	public HAResponse queryRollerShutterInfo(int network, int node) throws Exception {
		
		verifyNetwork(network);
		verifyNode(node);
		return HAResponse.getOKStatus();
	}

	/**
	 * @param network
	 * @param node
	 * @return
	 * @throws Exception
	 * @see com.terapico.hacontrol.server.DeltaDoreController#closeRollerShutterSlowly(int, int)
	 */
	public HAResponse closeRollerShutterSlowly(int network, int node) throws Exception {
		verifyNetwork(network);
		verifyNode(node);
		this.getNodeTopology().updateNodeStatus(network, node, "position", "0");
		return HAResponse.getOKStatus();
	}

	/**
	 * @param network
	 * @throws InterruptedException
	 * @see com.terapico.hacontrol.server.DeltaDoreController#deleteAllNodes(int)
	 */
	public void deleteAllNodes(int network) throws InterruptedException {
		verifyNetwork(network);
	}

	/**
	 * @param network
	 * @param node
	 * @throws InterruptedException
	 * @see com.terapico.hacontrol.server.DeltaDoreController#deleteNode(int, int)
	 */
	public void deleteNode(int network, int node) throws InterruptedException {
		verifyNetwork(network);
		verifyNode(node);

	}
	protected void verifyNetwork(int network) {
		if (network >= 0 && network < this.getNetworkCount()) {
			return;
		}
		throw new IllegalArgumentException("network number: '" + network + "' is not in range[0," + (this.getNetworkCount()-1) + "]");

	}
	

	protected int getNetworkCount() {
		// TODO Auto-generated method stub
		return 12;
	}

	protected void verifyColor(int color) {
		if (color >= 0 && color < 256) {
			return;
		}
		throw new IllegalArgumentException("color number: '" + color + "' is not in range[0,255]");

	}
	protected void verifyNode(int node) {
		if (node >= 0 && node < 16) {
			return;
		}
		throw new IllegalArgumentException("node number: '" + node + "' is not in range[0,15]");

	}
	public HAResponse stopRollerShutter(int network, int node) throws Exception {
		// TODO Auto-generated method stub
		verifyNetwork(network);
		verifyNode(node);
		return HAResponse.getOKStatus();
	}
	public HAResponse toggleRollerShutter(int network, int node) throws Exception {
		// TODO Auto-generated method stub
		verifyNetwork(network);
		verifyNode(node);
		return HAResponse.getOKStatus();
	}
	public HAResponse setRollerShutterPosition(int network, int node,int position) throws Exception {
		// TODO Auto-generated method stub
		verifyNetwork(network);
		verifyNode(node);
		return HAResponse.getOKStatus();
	}
	public HAResponse standOutRollerShutter(int network, int node) throws Exception {
		// TODO Auto-generated method stub
		verifyNetwork(network);
		verifyNode(node);
		return HAResponse.getOKStatus();
	}
	protected void emitRollerShutterCommand(int network, int node, RollerShutterCommandArg command)throws Exception {
		// TODO Auto-generated method stub
		verifyNetwork(network);
		verifyNode(node);
		
	}
	protected void emitLightCommand(int network, int node, LightCommandArg commandArg) throws Exception{
		// TODO Auto-generated method stub
		verifyNetwork(network);
		verifyNode(node);
	}
	public HAResponse stopLight(int network, int node) throws Exception{
		// TODO Auto-generated method stub
		verifyNetwork(network);
		verifyNode(node);
		return HAResponse.getOKStatus();
	}
	public HAResponse rampUpLight(int network, int node)throws Exception {
		// TODO Auto-generated method stub
		verifyNetwork(network);
		verifyNode(node);
		return HAResponse.getOKStatus();
	}
	
	public HAResponse rampDownLight(int network, int node)throws Exception {
		// TODO Auto-generated method stub
		verifyNetwork(network);
		verifyNode(node);
		return HAResponse.getOKStatus();
	}
	
	
	public HAResponse gotoLightFavorate1(int network, int node)throws Exception {
		// TODO Auto-generated method stub
		verifyNetwork(network);
		verifyNode(node);
		return HAResponse.getOKStatus();
	}
	
	public HAResponse setLightFavorate1(int network, int node)throws Exception {
		// TODO Auto-generated method stub
		verifyNetwork(network);
		verifyNode(node);
		return HAResponse.getOKStatus();
	}
	
	public HAResponse setLightFavorate2(int network, int node)throws Exception {
		// TODO Auto-generated method stub
		verifyNetwork(network);
		verifyNode(node);
		return HAResponse.getOKStatus();
	}
	
	public HAResponse gotoLightFavorate2(int network, int node)throws Exception {
		// TODO Auto-generated method stub
		verifyNetwork(network);
		verifyNode(node);
		return HAResponse.getOKStatus();
	}
	public HAResponse standOutLight(int network, int node)throws Exception {
		// TODO Auto-generated method stub
		verifyNetwork(network);
		verifyNode(node);
		return HAResponse.getOKStatus();
	}
	public HAResponse toggleLight(int network, int node)throws Exception {
		// TODO Auto-generated method stub
		verifyNetwork(network);
		verifyNode(node);
		return HAResponse.getOKStatus();
	}
	protected void emitLightCommands(int network, int[] nodes, LightCommandArg commandArg) throws Exception {
		// TODO Auto-generated method stub
		verifyNetwork(network);
		for(int node:nodes){
			verifyNode(node);
		}
		
	}
	public HAResponse switchOnLights(int network, int[] nodes) throws Exception {
		// TODO Auto-generated method stub
		
		verifyNetwork(network);
		for(int node:nodes){
			verifyNode(node);
			// TODO Auto-generated method stub
			getNodeTopology().updateNodeStatus(network, node, "level", "100");
		}

		// TODO Auto-generated method stub
		return HAResponse.getOKStatus();
		
	}
	public HAResponse switchOffLights(int network, int[] nodes) throws Exception {
		// TODO Auto-generated method stub
		verifyNetwork(network);
		for(int node:nodes){
			verifyNode(node);
			// TODO Auto-generated method stub
			getNodeTopology().updateNodeStatus(network, node, "level", "0");
		}

		// TODO Auto-generated method stub
		return HAResponse.getOKStatus();
	}

	
	
	


}
