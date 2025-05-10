

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Servlet implementation class MyQuestionsServlet
 */
@WebServlet("/MyQuestions")
public class MyQuestionsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MyQuestionsServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
    	      throws ServletException, IOException {
    	    // ensure customer logged in
    	    String custId = (String) req.getSession().getAttribute("uname");
    	    if (custId == null) {
    	      resp.sendRedirect(req.getContextPath() + "/Home");
    	      return;
    	    }

    	    // fetch questions
    	    MySQL db = new MySQL();
    	    List<Map<String,Object>> questions = db.getQuestionsByCustomer(custId);
    	    req.setAttribute("questions", questions);

    	    // forward to JSP
    	    req.getRequestDispatcher("/myQuestions.jsp")
    	       .forward(req, resp);
    	  }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
