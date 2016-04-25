package com.terapico.hacontrol.server;

public class HAControllerFactory {
	 public static HAController createInstance(String instanceType)
	 {
		 if("softcontroller".equals(instanceType)){
			 return new SoftHAController();
		 }
		 
		 if("hardcontroller".equals(instanceType)){
			 return  DeltaDoreController.instance();
		 }
		 if("deltadorecontroller".equals(instanceType)){
			 return  DeltaDoreController.instance();
		 }
		 return DeltaDoreController.instance();
		 
	 }
}
