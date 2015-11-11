package servlets.tools;

import javax.servlet.http.Cookie;

public class Utils {

	public Utils() {
		// TODO Auto-generated constructor stub
	}
	public static String getCookieValue(Cookie[] cookies, String nameCookie){
			String result="";
			if (cookies.length>0){
			for(int i=0;i<cookies.length;i++){
				if(cookies[i].getName().equals(nameCookie)){
					if(cookies[i].getValue()!=null){
						result=cookies[i].getValue();
						return result;
					}
					
				}				
			}
			}
			return result;
		
	}

}
