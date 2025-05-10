

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
      // 1) Read the keyword param
      String kw = req.getParameter("q");
      if (kw == null) kw = "";

      // 2) Query the DAO
      MySQL db = new MySQL();
      List<Map<String,Object>> results = db.searchQuestions(kw);

      // 3) Attach to request and forward
      req.setAttribute("questions", results);
      req.setAttribute("keyword", kw);
      req.getRequestDispatcher("searchResults.jsp")
         .forward(req, resp);
    }

    // if you want POST → GET too:
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
      doGet(req, resp);
    }
}