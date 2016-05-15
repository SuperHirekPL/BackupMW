package servlets;
import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import service.connector.FbConnector;
import service.model.Recomendation;

/**
 * Servlet implementation class FBMenager
 */
@WebServlet("/FBMenager")
public class FBManager extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FBManager() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Cookie[] cookies =request.getCookies();
		String appToken = servlets.tools.Utils.getCookieValue(cookies, "fbToken");
		FbConnector fbconnector = new service.connector.FbConnector(appToken);
		String nameUser=fbconnector.getNameUser();
		
		response.setContentType("text/plain");  
	    response.setCharacterEncoding("UTF-8"); 
	    response.getWriter().write(nameUser);
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		// TODO Auto-generated method stub
	}
	


}
