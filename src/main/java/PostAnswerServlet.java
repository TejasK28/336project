

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Servlet implementation class PostAnswerServlet
 */
@WebServlet("/PostAnswer")
public class PostAnswerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PostAnswerServlet() {
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
	
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
	        throws ServletException, IOException {

	    // 1) get questionId param
	    String qParam = req.getParameter("questionId");
	    if (qParam == null) {
	        resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing questionId");
	        return;
	    }
	    int qid = Integer.parseInt(qParam);

	    // 2) get the logged-in user from session
	    String empId   = (String) req.getSession().getAttribute("uname");
	    String accType = (String) req.getSession().getAttribute("accType");

	    // only customer-reps and admins may answer
	    if (empId == null ||
	        !( "CustRep".equals(accType) || "Admin".equals(accType) ))
	    {
	        resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Not authorized");
	        return;
	    }

	    // 3) get the message
	    String msg = req.getParameter("message");
	    LocalDateTime now = LocalDateTime.now();

	    // 4) insert
	    boolean ok = new MySQL().addAnswer(qid, empId, now, msg);
	    if (!ok) {
	        resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not save answer");
	        return;
	    }

	    // 5) back to the same question view
	    resp.sendRedirect(req.getContextPath() + "/ViewQuestion?questionId=" + qid);
	}


	
}
