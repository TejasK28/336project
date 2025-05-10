

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Servlet implementation class ViewQuestionServlet
 */
@WebServlet("/ViewQuestion")
public class ViewQuestionServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ViewQuestionServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
    	      throws ServletException, IOException {
    	    String idParam = req.getParameter("questionId");
    	    if (idParam == null) {
    	      resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing questionId");
    	      return;
    	    }
    	    int qid = Integer.parseInt(idParam);
    	    Map<String, Object> question = new MySQL().getQuestionById(qid);
    	    List<Map<String,Object>> answers = new MySQL().getAnswersForQuestion(qid);
    	    req.setAttribute("question", question);
    	    req.setAttribute("answers", answers);
    	    req.getRequestDispatcher("/viewQuestion.jsp")
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
