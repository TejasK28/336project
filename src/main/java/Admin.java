
import jakarta.servlet.annotation.MultipartConfig; 

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
@WebServlet({"/Admin", "/CreateEmployee", "/DeleteEmployee", "/EditEmployee",
			"/EditCustomer", "/DeleteCustomer"})
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
		MySQL r = new MySQL();
		// Ensure authenticated account
		if (request.getSession().getAttribute("authenticated") == null) {
			response.sendRedirect(request.getContextPath() + "/Home");
			return;
		}
    	// Prevent CreateEmployee route from using GET request
    	if (request.getServletPath().equals("/Admin")) {
			// Check if user is authenticated 
			
			HttpSession session = request.getSession(false);
			if (session == null || session.getAttribute("authenticated") == null) {
				response.sendRedirect("Home.jsp");
				return;
			}

			// Fetch employee data
			String sql = "SELECT * FROM Employee";
			List<Map<String, Object>> employeeList = new ArrayList<>();

			try (Connection con = r.getConnection();
				 PreparedStatement ps = con.prepareStatement(sql);
				 ResultSet rs = ps.executeQuery()) {

				ResultSetMetaData meta = rs.getMetaData();
				int columnCount = meta.getColumnCount();

				while (rs.next()) {
					Map<String, Object> emp_row = new HashMap<>();
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
		   

			// Put the data in the session
			request.setAttribute("employees", employeeList);
			
			List<Map<String, Object>> customerList = r.getAllCustomers();
			request.setAttribute("customers", customerList);
			

			// Forward to JSP
			RequestDispatcher dispatcher = request.getRequestDispatcher("AdminPortal.jsp");
			dispatcher.forward(request, response);
			return;
    	}
    	else if ("/EditEmployee".equals(request.getServletPath())) {
    		String emp_id = request.getParameter("username");
    		request.setAttribute("employee", r.getEmployee(emp_id));
    		RequestDispatcher dispatcher = request.getRequestDispatcher("EditEmployee.jsp");
    		dispatcher.forward(request, response);
    		return;
    	}
    	else if ("/EditCustomer".equals(request.getServletPath())) {
    		String cust_id = request.getParameter("customerID");
    		request.setAttribute("customer", r.getCustomer(cust_id));
    		RequestDispatcher dispatcher = request.getRequestDispatcher("EditCustomer.jsp");
    		dispatcher.forward(request, response);
    		return;
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
		MySQL r = new MySQL();
		if (request.getServletPath().equals("/CreateEmployee")) {
			String emp_id = (String) request.getParameter("EmployeeID");
			String fname = (String) request.getParameter("FirstName");
			String lname = (String) request.getParameter("LastName");
			String password = (String) request.getParameter("Password");
			String email = (String) request.getParameter("Email");
			boolean isAdmin = "on".equals(request.getParameter("isAdmin"));
			boolean isCustRep = "on".equals(request.getParameter("isCustomerRepresentative"));
			
			boolean isCustomer = !(isAdmin || isCustRep);
			response.sendRedirect(request.getContextPath() + "/Admin");
			
			if (isCustomer) {
				// Add to Customer database
			}
			else {
				// Add to Employee table
				r.addEmployee(emp_id, fname, lname, email, password, isAdmin, isCustRep);
			}
		}
		else if (request.getServletPath().equals("/DeleteEmployee")) {
			String emp_id = (String) request.getParameter("username");
			r.deleteEmployee(emp_id);
			response.sendRedirect(request.getContextPath() + "/Admin");
		}
		else if (request.getServletPath().equals("/DeleteCustomer")) {
			String cust_id = (String) request.getParameter("customerID");
			r.deleteCustomer(cust_id);
			response.sendRedirect(request.getContextPath() + "/Admin");
			return;
		}
		else if ("/EditEmployee".equals(request.getServletPath())) {
		    String empId    = request.getParameter("username");
		    String first    = request.getParameter("FirstName");
		    String last     = request.getParameter("LastName");
		    String email    = request.getParameter("Email");
		    String newPass  = request.getParameter("Password");
		    boolean isAdmin = "on".equals(request.getParameter("isAdmin"));
		    boolean isRep   = "on".equals(request.getParameter("isCustomerRepresentative"));

		    // if the form didn’t supply a password, reload the old one
		    if (newPass == null || newPass.isEmpty()) {
		        Map<String,Object> existing = r.getEmployee(empId);
		        newPass = (String) existing.get("Password");
		    }

		    r.editEmployee(empId, first, last, email, newPass, isAdmin, isRep);
		    response.sendRedirect(request.getContextPath() + "/Admin");
		    return;
		}

		else if (request.getServletPath().equals("/EditCustomer")) {
		    // 1) Pull every field out of the POST
		    String custId    = request.getParameter("customerId");  // match your hidden input name
		    String password  = request.getParameter("Password");
		    String firstName = request.getParameter("FirstName");
		    String lastName  = request.getParameter("LastName");
		    String email     = request.getParameter("Email");
		    String phone     = request.getParameter("Phone");
		    String address   = request.getParameter("Address");

		    boolean success = r.editCustomer(custId,
		                                     firstName,
		                                     lastName,
		                                     email,
		                                     password,
		                                     phone,
		                                     address);
		    response.sendRedirect(request.getContextPath() + "/Admin");
		    return;
		}
		else if (request.getServletPath().equals("/CreateCustomer")) {
		    String custId    = request.getParameter("CustomerID");
		    String password  = request.getParameter("Password");
		    String firstName = request.getParameter("FirstName");
		    String lastName  = request.getParameter("LastName");
		    String email     = request.getParameter("Email");
		    String phone     = request.getParameter("Phone");
		    String address   = request.getParameter("Address");

		    boolean ok = r.addCustomer(custId, firstName, lastName, email, password, phone, address);

		    response.sendRedirect(request.getContextPath() + "/Admin");
		    return;
		}
		else {
			response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "GET method is not allowed on this route.");
    		return;
		}
		
		
	}

}
