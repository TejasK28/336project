import jakarta.servlet.ServletException; 
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet implementation class Login
 */
@WebServlet("/Login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String uname = request.getParameter("uname");
		String password = request.getParameter("password");
		
		User user = new User(uname, password);
		
		MySQL r = new MySQL();
	
		// Get location of request
		System.out.println(request.getSession().getAttribute("accType"));
		String accType = (String) request.getSession().getAttribute("accType");
		String accTypeJSP = accType + ".jsp";

		boolean res = r.validateUser(user, accType);
		
		if(res)
		{
			// Create a session and store username
	        request.getSession().setAttribute("uname", uname);
	        // Redirect to welcome page
	        response.sendRedirect("Welcome.jsp");
		}
		else
		{
			request.getSession().setAttribute("failed", true);
			response.sendRedirect(accType);
		}
	}

}
