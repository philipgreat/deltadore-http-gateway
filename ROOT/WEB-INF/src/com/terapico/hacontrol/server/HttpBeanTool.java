package com.terapico.hacontrol.server;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;


public class HttpBeanTool {
	
	public static void main(String []args) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
	{
		HttpBeanTool tool=new HttpBeanTool();		
		//tool.invokeExpr(tool, "hello", new String[]{"Jack","5555"});
		tool.invokeExpr(tool, "helloArray", new String[]{"Jack","5555;888;999"});
		
		
	}
	
	public Object invokeExpr(Object object,String methodName, String[] parameters) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Method method = this.findMethod(object, methodName, parameters.length);
		if (method == null) {
			String message="'" + methodName+"' with "+parameters.length+" parameter(s) is not supported now!";
			throw new IllegalArgumentException(message);
		}
		Object[] params = getParameters(method.getGenericParameterTypes(), parameters);		
		return method.invoke(object, params);

	}
	public Method findMethod(Object object,String name, int paramLength){
		
		Class clazz = object.getClass();
		Method[] methods = clazz.getDeclaredMethods();		
		for (Method method : methods) {
			if (method.getGenericParameterTypes().length!=paramLength) {
				continue;
			}
			if (!method.getName().equalsIgnoreCase(name)) {
				continue;
			}
			return method;		
		}
		return null;
	}
	

	public Object[] getParameters(Type[] types, String[] parameters) {

		int length = parameters.length;
		if (length == 0) {
			return new Object[] {};
		}
		Object[] ret = new Object[length];

		for (int i = 0; i < length; i++) {
			ret[i] = converExprToObject(types[i], parameters[i]);
			//System.out.println(ret[i].getClass() + "" + ret[i]);
		}
		return ret;
	}
	public void hello(String somebody,int times) throws Exception {
		System.out.println("hello "+ somebody+", times: "+times);
	}
	public void helloArray(String somebody,int times[]) throws Exception {
		for(int time:times){
			System.out.println("hello "+ somebody+", times: "+time);
		}
		
	}
	public Object converExprToObject(Type type, String parameter) {
		
		if(type instanceof Class){		
			Class clazz=(Class)type;
			if(clazz.isArray()){			
				Class componentType=clazz.getComponentType();
				String []subParametersExpr=parameter.split(";");
				int arrayLength=subParametersExpr.length;
				if(arrayLength==0){
					return Array.newInstance(componentType, 0);
				}
				
				Object  objectArrayParameter=   Array.newInstance(componentType, arrayLength);
				int i=0;
				for(String subParameterExpr:subParametersExpr){
					//objectArrayParameter[i]=converExprToObject(componentType,subParameter);
					Object subParameter=converExprToObject(componentType,subParameterExpr);
					Array.set(objectArrayParameter, i, subParameter);					
					i++;
				}
				return  objectArrayParameter;
			}
		}
		
		if (type == String.class) {
			return parameter;
		}
		if (type == int.class || type == Integer.class) {
			return Integer.parseInt(parameter);
		}
		if (type == long.class || type == Long.class) {
			return Long.parseLong(parameter);
		}
		if (type == byte.class || type == Byte.class) {
			return Byte.parseByte(parameter);
		}

		return parameter;

	}
	
	public boolean isBasicTypeByClass(Type type) {

		if(type.getClass().isPrimitive()){
			return true;
		}
		
		if (type == String.class) {
			return true;
		}
		if (type == Number.class) {
			return true;
		}
		if (type == Byte.class) {
			return true;
		}
		// java.lang.Byte (implements java.lang.Comparable<T>)
		// java.lang.Double (implements java.lang.Comparable<T>)
		// java.lang.Float (implements java.lang.Comparable<T>)
		// java.lang.Integer (implements java.lang.Comparable<T>)
		// java.lang.Long (implements java.lang.Comparable<T>)
		// java.lang.Short (implements java.lang.Comparable<T>)
		if (type == Double.class) {
			return true;
		}
		if (type == Float.class) {
			return true;
		}
		if (type == Integer.class) {
			return true;
		}
		if (type == Long.class) {
			return true;
		}
		if (type == Short.class) {
			return true;
		}
		if (type == Boolean.class) {
			return true;
		}
		if (type == java.util.Date.class) {
			return true;
		}
		if (type == java.sql.Date.class) {
			return true;
		}
		return false;

	}
	public void invokeAndPrint(Object object, Method method) throws IllegalArgumentException, InvocationTargetException {

		Object ret;
		try {
			ret = method.invoke(object, new Class[] {});
			System.out.println(method.getName() + "=" + ret);
		} catch (IllegalAccessException e) {
			System.out.println(method.getName() + "=@error@");
		}

	}
	
	public void peekObject(Object object) throws IllegalArgumentException, InvocationTargetException {
		if (object == null) {
			return;
		}
		Class clazz = object.getClass();
		Method[] methods = clazz.getMethods();
		for (Method method : methods) {

			if(!isBasicTypeByClass(method.getReturnType())){
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
		}

	}
	
}
