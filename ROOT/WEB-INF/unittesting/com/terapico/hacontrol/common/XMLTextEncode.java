package com.terapico.hacontrol.common;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class XMLTextEncode {

	public static String escapeXMLChinese(String expr)
	{
		StringBuffer sb=new StringBuffer();
		char[] chs=expr.toCharArray();
		 for(char ch:chs){			 
			 
			 
			 if(ch=='\n'){
				 
				 sb.append("\\n");
				 sb.append("\r\n");
				 continue;
			 }
			 if(ch=='\r'){				 
				 continue;
			 }
			 if(ch<256){
				 sb.append(ch);
				 continue;
			 }
			 sb.append("&#x"+Integer.toHexString(ch)+";");			 
		 }
		return sb.toString();
	}
	public static String getTextFromFile(String filePath) {
		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader in = new BufferedReader(new FileReader(filePath));
			String str;
			while ((str = in.readLine()) != null) {
				sb.append(str);
				sb.append("\r\n");
			}
			in.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return null;
		}
		return sb.toString();
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if(args.length<1){
			System.out.println("Please use like: java XMLTextEncode <filename>");
			return;
		}
		 String name=getTextFromFile(args[0]);
		 System.out.println(escapeXMLChinese(name));
		 
	}

}
