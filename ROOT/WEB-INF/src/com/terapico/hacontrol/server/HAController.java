package com.terapico.hacontrol.server;

public interface HAController {
	public void doInit() throws Exception ;
	public void doInit(String communicationResource) throws Exception;
	
	public void doDestroy() throws Exception;
	
}
