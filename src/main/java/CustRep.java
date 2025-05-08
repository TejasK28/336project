
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Servlet implementation class CustRep
 */
@WebServlet("/CustRep")
public class CustRep extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CustRep() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		MySQL r = new MySQL();
		if ("/CustRep".equals(request.getServletPath())) {
			// Grab Airport data from database
			List<Map<String, Object>> airports = r.getAllAirports();
			request.setAttribute("airports", airports);
			
			// Grab Airline data from database
			List<Map<String, Object>> airlines = r.getAllAirlines();
			request.setAttribute("airlines", airlines);
			
			
			RequestDispatcher dispatcher = request.getRequestDispatcher("CustRepPortal.jsp");
    		dispatcher.forward(request, response);
		}
		else {
			response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "GET method is not allowed on this route.");
    		return;
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
