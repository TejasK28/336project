

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servlet implementation class Admin
 */
@WebServlet({"/Admin", "/CreateEmployee", "/DeleteEmployee"})
public class Admin extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Admin() {
        super();
        // TODO Auto-generated constructor stub
    }
    

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	// Prevent CreateEmployee route from using GET request
    	if (!request.getServletPath().equals("/Admin")) {
			response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "GET method is not allowed on this route.");
    		return;
    	}
    	
    	
    	// Check if user is authenticated 
    	
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("authenticated") == null) {
            response.sendRedirect("Home.jsp");
            return;
        }

        // Fetch employee data
        MySQL r = new MySQL();
        String sql = "SELECT * FROM Employee";
        List<Map<String, Object>> employeeList = new ArrayList<>();

        try (Connection con = r.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();

            while (rs.next()) {
                Map<String, Object> emp_row = new HashMap<>();
//                for (int i = 1; i <= columnCount; i++) {
//                	System.out.println(meta.getColumnName(i));
//                    emp_row.put(meta.getColumnName(i), rs.getObject(i));
//                }
                emp_row.put("EmployeeID", rs.getString("EmployeeID"));
                emp_row.put("FirstName", rs.getString("FirstName"));
                emp_row.put("LastName", rs.getString("LastName"));
                emp_row.put("Email", rs.getString("Email"));
                emp_row.put("isAdmin", rs.getBoolean("isAdmin"));
                emp_row.put("isCustomerRepresentative", rs.getBoolean("isCustomerRepresentative"));
                employeeList.add(emp_row);
            }

        } catch (SQLException e) {
            System.out.println(e);
            return;
        }
       
        System.out.println("Session ID: " + session.getId());
        System.out.println("Is New: " + request.getSession().isNew());

        // Put the data in the session
        request.setAttribute("employees", employeeList);

        // Forward to JSP
        RequestDispatcher dispatcher = request.getRequestDispatcher("AdminPortal.jsp");
        dispatcher.forward(request, response);
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (request.getServletPath().equals("/CreateEmployee")) {
			String emp_id = (String) request.getParameter("EmployeeID");
			String fname = (String) request.getParameter("FirstName");
			String lname = (String) request.getParameter("LastName");
			String password = (String) request.getParameter("Password");
			String email = (String) request.getParameter("Email");
			boolean isAdmin = "on".equals(request.getParameter("isAdmin"));
			boolean isCustRep = "on".equals(request.getParameter("isCustomerRepresentative"));
			
			boolean isCustomer = !(isAdmin || isCustRep);
			System.out.printf("%s: %s, %s (%s), Email: %s, isAdmin: %s, isCustRep: %s", 
					emp_id, fname, lname, password, email, isAdmin, isCustRep);
			response.sendRedirect(request.getContextPath() + "/Admin");
			
			if (isCustomer) {
				// Add to Customer database
			}
			else {
				// Add to Employee table
				MySQL r = new MySQL();
				
				
				r.addEmployee(emp_id, fname, lname, email, password, isAdmin, isCustRep);
			}
		}
		else if (request.getServletPath().equals("/DeleteEmployee")) {
			
		}
		else {
			response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "GET method is not allowed on this route.");
    		return;
		}
		
		
	}

}
