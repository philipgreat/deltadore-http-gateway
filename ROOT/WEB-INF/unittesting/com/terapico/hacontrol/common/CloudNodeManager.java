package com.terapico.hacontrol.common;

public class CloudNodeManager {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public CloudResponse handleRequest(CloudRequest request){
		
		if(request==null){
			throw new IllegalArgumentException("handleRequest(CloudRequest request) param request can not be null");
		}
		
		//1. create response with a id
		//2. add to a map with the key=id
		//3. allocate a worker, worker stores in a map
		//4. call the worker and assign the parameter
		//5. wait for the response
		//6. return the response
		
		return null;
		
		
		
	}

}
