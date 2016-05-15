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
 * Servlet implementation class ServletMenager
 */
@WebServlet(
		name = "ServletManager",
		urlPatterns = {"/servletManager"})
public class ServletManager extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ServletManager() {
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
		//String nameUser=fbconnector.getNameUser();	
		
		forwardListEmployees(request,response,fbconnector.getAllRecomendation());
		
	}
	
	
	
    private void forwardListEmployees(HttpServletRequest req, HttpServletResponse resp, List<Recomendation> recomendationList)
            throws ServletException, IOException {
        String nextJSP = "/mainPage.jsp";
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextJSP);
        req.setAttribute("recomendationList", recomendationList);
        dispatcher.forward(req, resp);
    } 
    

}
