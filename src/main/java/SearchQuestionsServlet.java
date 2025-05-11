

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;


@WebServlet("/SearchQuestions")
public class SearchQuestionsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public SearchQuestionsServlet() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
    	      throws ServletException, IOException {
    	    String kw = req.getParameter("q");
    	    List<Map<String,Object>> qs = new MySQL().searchQuestions(kw);
    	    req.setAttribute("questions", qs);
    	    req.setAttribute("keyword", kw);
    	    req.getRequestDispatcher("/searchQuestions.jsp")
    	       .forward(req, resp);
    	  }

    // if you want POST → GET too:
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
      doGet(req, resp);
    }
}