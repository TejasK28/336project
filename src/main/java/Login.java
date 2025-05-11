import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Servlet implementation class Login
 */
@WebServlet({ "/Login", "/CustomerLogin", "/AdminLogin", "/CustRepLogin", "/Home", "/CreateCustomer" })
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
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String path = request.getServletPath();
		  HttpSession sess = request.getSession();
		
		
		// TODO Auto-generated method stub
		  if ("/AdminLogin".equals(path)) {
			    sess.setAttribute("accType", "Admin");
			    request.getRequestDispatcher("AdminLogin.jsp").forward(request, response);

			  } else if ("/CustomerLogin".equals(path)) {
			    sess.setAttribute("accType", "Customer");
			    request.getRequestDispatcher("CustomerLogin.jsp").forward(request, response);

			  } else if ("/CustRepLogin".equals(path)) {
			    sess.setAttribute("accType", "CustRep");
			    request.getRequestDispatcher("CustomerRepLogin.jsp").forward(request, response);
		} else if ("/Home".equals(request.getServletPath())) {
			MySQL db = new MySQL();
			
			@SuppressWarnings("unchecked")
			List<String> plan = (List<String>) request.getSession()
			                                         .getAttribute("CustFlightPlan");

			if (plan == null || plan.isEmpty()) {
			    // nothing to query
			    request.setAttribute("flightPlanResults", Collections.emptyList());
			}
			else {
			    // 2) Build a placeholders string like "?, ?, ?, …"
			    String placeholders = plan.stream()
			                              .map(id -> "?")
			                              .collect(Collectors.joining(", "));
			    
			    // 3) Build the SQL
			    String sql = "SELECT * FROM Flight WHERE FlightID IN (" + placeholders + ")";
			    
			    // 4) Execute using your helper's varargs interface
			    //    (It will bind each plan.get(i) to the i+1'th "?.")
			    List<Map<String,Object>> flights = db.executeQuery(
			        sql,
			        plan.toArray(new String[0])
			    );
			    
			    request.setAttribute("flightPlanResults", flights);
			}
			
			String uname = (String) request.getSession().getAttribute("uname");
			request.setAttribute("reservedFlightPlans", db.getUserFlightPlans(uname));
			
			// Add scheduled flights attribute
			request.setAttribute("scheduledFlights", db.getAllFlights(uname));
			
			// Add past flights attribute (assuming you have a method to get past flights)
			List<Map<String, Object>> pastFlights = db.getPastFlights(uname);
			System.out.println("Past flights size: " + pastFlights.size());
			request.setAttribute("pastFlights", pastFlights);
			
			// Add waitlisted flights attribute
			request.setAttribute("waitlistedFlights", db.getWaitlistedFlights(uname));
			
			RequestDispatcher dispatcher = request.getRequestDispatcher("Home.jsp");
			dispatcher.forward(request, response);
		} else if ("/CreateCustomer".equals(request.getServletPath())) {
			RequestDispatcher dispatcher = request.getRequestDispatcher("CreateCustomer.jsp");
			dispatcher.forward(request, response);
		} else {
			response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "GET method is not allowed on this route.");
			return;
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		MySQL r = new MySQL();

		if ("/Login".equals(request.getServletPath())) {
			String uname = request.getParameter("uname");
			String password = request.getParameter("password");

			User user = new User(uname, password);

			// Get location of request
			System.out.println(request.getSession().getAttribute("accType"));
			String accType = (String) request.getSession().getAttribute("accType");

			boolean res = r.validateUser(user, accType);

			if (res) {

				Map<String, Object> employee = null;
				// Check employee account permissions, if employee
				if ("Admin".equals(accType) || "CustRep".equals(accType)) {
					employee = r.getEmployee(uname);
				}

				// Redirect to welcome page

				if (accType.equals("Admin")) {
					if (employee == null || employee.get("isAdmin") == null) {
						request.getSession().setAttribute("noPerm", true);
						response.sendRedirect(request.getContextPath() + "/" + accType + "Login");
						return;
					}

					response.sendRedirect(request.getContextPath() + "/Admin");
				} else if ("Customer".equals(accType)) {
					request.getSession().setAttribute("CustFlightPlan", new ArrayList<>());
					response.sendRedirect(request.getContextPath() + "/Home");
				} else if ("CustRep".equals(accType)) {
					System.out.println("custrepaldskjfasldjf: " + employee.get("isCustomerRepresentative"));
					if (employee == null || !(boolean) employee.get("isCustomerRepresentative")) {
						request.getSession().setAttribute("noPerm", true);
						response.sendRedirect(request.getContextPath() + "/" + accType + "Login");
						return;
					}
					response.sendRedirect(request.getContextPath() + "/CustRep");

				} else {
					response.sendRedirect(request.getContextPath() + "/Home");

				}
				// Create a session and store username
				request.getSession().setAttribute("uname", uname);
				request.getSession().setAttribute("authenticated", true);
			} else {
				request.getSession().setAttribute("failed", true);
				response.sendRedirect(request.getContextPath() + "/" + accType + "Login");
			}

		} else if ("/CreateCustomer".equals(request.getServletPath())) {
		    // 1) read form fields
		    String custId    = request.getParameter("CustomerID");
		    String firstName = request.getParameter("FirstName");
		    String lastName  = request.getParameter("LastName");
		    String email     = request.getParameter("Email");
		    String password  = request.getParameter("Password");
		    String phone     = request.getParameter("Phone");
		    String address   = request.getParameter("Address");

		    // 2) add the customer
		    boolean success = r.addCustomer(custId, firstName, lastName, email,
		                                    password, phone, address);

		    // 3) detect if we came from AdminPortal
		    String fromAdmin = request.getParameter("fromAdmin");

		    if (!success) {
		        // insertion failed: re-show the form (you can also set an error message)
		        request.setAttribute("custCreated", false);
		        request.getRequestDispatcher("CreateCustomer.jsp")
		               .forward(request, response);
		    } else {
		        // successful: if we came from the admin, go back there
		        if ("true".equals(fromAdmin)) {
		            response.sendRedirect(request.getContextPath() + "/Admin");
		        } else {
		            // normal flow for new customer signup
		            response.sendRedirect(request.getContextPath() + "/CustomerLogin.jsp");
		        }
		    }
		    return;
		}

	}

}
