package com.terapico.hacontrol.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.terapico.hacontrol.common.HAResponse;

public class HAServlet extends HttpServlet {

	private RXTXDeltaDoreController controller;
	HttpBeanTool beanTool;
	public void destroy() {
		// TODO Auto-generated method stub
		if (controller == null) {
			return;
		}
		if(true){
			try {
				controller.doDestroy();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("error when doing the destroy! " + e.getMessage());
			}
		}
	
	}

	public void init(ServletConfig config) throws ServletException {
		
		log("Init the context for the server!");
		
		//System.getProperties().list(System.out);		
		controller = RXTXDeltaDoreController.instance();
		beanTool = new HttpBeanTool();
		// TODO Auto-generated method stub
		String selectedPort = config.getInitParameter("port");
		if (selectedPort == null) {
			log("No port configured in web.xml. Trying to find a single port to run");
			try {
				controller.doInit();
				return;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ServletException(e.getMessage());
			}
		}
		
		log("A configured port is found, use the port: "+selectedPort );

		try {
			controller.doInit(selectedPort);
			return;
		} catch (Exception e) {
			e.printStackTrace();
			// TODO Auto-generated catch block
			throw new ServletException(e.getMessage());
		}

	}

	public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		// TODO Auto-generated method stub
		response.addHeader("Pragma" , "No-Cache") ;
		response.addHeader("Cache-Control", "No-Cache") ;
		response.addDateHeader("Expires", 0);
		
		String fullPath=request.getServletPath()+request.getPathInfo();
		
		String pathNames[]=fullPath.split("\\/");
		int length=pathNames.length;
		if(length<3){
			System.err.println("error: request.getServletPath()+request.getPathInfo();="+fullPath+" has too less '/'.");
			return;
		}
		System.err.println(fullPath);		
		String methodName=pathNames[2];		
		String parameters[]=getParameters(pathNames);		
		PrintWriter out=response.getWriter();
		try {
			
			long start=System.currentTimeMillis();
			Object object=beanTool.invokeExpr(controller, methodName, parameters);
			System.out.println("exection time: "+ (System.currentTimeMillis()-start)+"ms");
			if(object==null){
				out.print(HAResponse.getOKStatus().toXML());
				return;
			}
			if(object instanceof HAResponse){
				HAResponse resp=(HAResponse)object;
				resp.addValue("executionTime",  (System.currentTimeMillis()-start)+"ms");
				out.print(resp.toXML());
				return;
			}
				
				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			String errorMessage=getErrorMessage(e);
			String message=HAResponse.getErrorStatus(errorMessage).toXML();
			out.print(message);
			return;		
		} 		

	}
	
	private String getErrorMessage(Exception e)
	{
		if(e==null){
			return "No explicit error message found.";
		}
		if(e.getMessage()!=null){
			return e.getMessage();
		}
		if(e.getCause()==null){
			return "No explicit error message found.";
		}
		if(e.getCause().getMessage()!=null){
			return e.getCause().getMessage();
		}
		return "No explicit error message found.";
		
	}
	public String[] getParameters(String pathNames[])
	{
		final int startFrom=3;
		
		int length=pathNames.length;
		if(length<startFrom){
			throw new IllegalArgumentException("the length of pathName[]'s is expected more than 2, now length is: "+pathNames);
		}
		if(length==3){
			return new String[]{};
		}
		
		String []params=new String[length-startFrom];
		System.arraycopy(pathNames, startFrom, params, 0,length-startFrom);
		return params;
	}
	
	
}
