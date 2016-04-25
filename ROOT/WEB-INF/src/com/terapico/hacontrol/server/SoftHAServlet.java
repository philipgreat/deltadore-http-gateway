package com.terapico.hacontrol.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.terapico.hacontrol.common.HAResponse;

public class SoftHAServlet extends HttpServlet {

	
	static protected final Logger log =   Logger.getLogger(SoftHAServlet.class.getName());
	HttpBeanTool beanTool;
	public void destroy() {
		// TODO Auto-generated method stub	
	
	}

	public void init(ServletConfig config) throws ServletException {		
		//System.getProperties().list(System.out);		
		beanTool = new HttpBeanTool();

	}
	protected void logInfo(String message)
	{
		log.log(Level.INFO,message);
		
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
			logInfo("error: request.getServletPath()+request.getPathInfo();="+fullPath+" has too less '/'.");
			return;
		}
		logInfo(fullPath);
		
		HAController controller=(HAController)request.getSession(true).getAttribute("contoller");		
		if(controller==null){		
			controller=new SoftHAController();	
			try {
			    controller.doInit();
			} catch (Exception exception) {
			    // TODO Auto-generated catch block
			    exception.printStackTrace();
			    return;
			}
			request.getSession(true).setAttribute("contoller",controller);		
		}	
		
		String methodName=pathNames[2];		
		String parameters[]=getParameters(pathNames);		
		PrintWriter out=response.getWriter();
		try {
			
			long start=System.currentTimeMillis();
			Object object=beanTool.invokeExpr(controller, methodName, parameters);
			//System.out.println("exection time: "+ (System.currentTimeMillis()-start)+"ms");
			Thread.currentThread().sleep(300+(long)(Math.random()*200));
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
			
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			String message=HAResponse.getErrorStatus(e.getCause().getMessage()).toXML();
			out.print(message);
			return;
			
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			String message=HAResponse.getOKStatus().getErrorStatus(e.getMessage()).toXML();
			
			out.print(message);
			return;
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			String message=HAResponse.getOKStatus().getErrorStatus(e.getMessage()).toXML();			
			out.print(message);
			return;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		

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