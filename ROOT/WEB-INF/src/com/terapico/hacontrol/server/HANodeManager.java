package com.terapico.hacontrol.server;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.terapico.hacontrol.common.HANode;

public class HANodeManager {
	private Map<String,HANode> haNodes;
	
	public void addNode(HANode node)
	{
		if(haNodes==null){			
			haNodes=new HashMap();
		}		
		haNodes.put(node.getKey(), node);	
	}
	public void removeNode(HANode node)
	{
		if(haNodes==null){			
			haNodes=new HashMap();
		}		
		haNodes.remove(node.getKey());	
	}
	public void removeNode(int networkId,int nodeId)
	{
		if(haNodes==null){			
			haNodes=new HashMap();
		}
		haNodes.remove(HANode.getKey(networkId, nodeId));	
	}
	public void updateNodeProperty(int networkId,int nodeId,String key,String value)
	{
		if(haNodes==null){			
			haNodes=new HashMap();
		}		
		haNodes.remove(HANode.getKey(networkId, nodeId));	
	}
	
	public String getTopologyExpr()
	{
		Collection haNodeList=haNodes.values();		
		Iterator it=haNodeList.iterator();
		StringBuffer stringBuffer=new StringBuffer(100);
		while(it.hasNext()){			
			HANode node=(HANode)it.next();
			stringBuffer.append(node.toExpression());			
		}		
		return stringBuffer.toString();	
	}
	
	
}
