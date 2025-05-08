import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

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
		// TODO Auto-generated method stub
		if ("/AdminLogin".equals(request.getServletPath())) {
			RequestDispatcher dispatcher = request.getRequestDispatcher("AdminLogin.jsp");
			dispatcher.forward(request, response);
		} else if ("/CustomerLogin".equals(request.getServletPath())) {
			RequestDispatcher dispatcher = request.getRequestDispatcher("CustomerLogin.jsp");
			dispatcher.forward(request, response);
		} else if ("/CustRepLogin".equals(request.getServletPath())) {
			RequestDispatcher dispatcher = request.getRequestDispatcher("CustomerRepLogin.jsp");
			dispatcher.forward(request, response);
		} else if ("/Home".equals(request.getServletPath())) {
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
			// Get form parameters

			System.out.println("Testaetasdf");
			String cust_id = request.getParameter("CustomerID"); // or use "CustomerID" if you update the form
			String fname = request.getParameter("FirstName");
			String lname = request.getParameter("LastName");
			String email = request.getParameter("Email");
			String password = request.getParameter("Password");
			String phone = request.getParameter("Phone");
			String address = request.getParameter("Address");

			System.out.println("Customer form data received:");
			System.out.println("CustomerID: " + cust_id);
			System.out.println("FirstName: " + fname);
			System.out.println("LastName: " + lname);
			System.out.println("Email: " + email);
			System.out.println("Password: " + password);
			System.out.println("Phone: " + phone);
			System.out.println("Address: " + address);

			// Call DB method
			boolean success = r.addCustomer(cust_id, fname, lname, email, password, phone, address);

			System.out.println("Did it add a customer: " + success);

			if (!success) {
				// Set failure flag and forward to form again
				request.setAttribute("custCreated", false);
				request.getRequestDispatcher("CreateCustomer.jsp").forward(request, response);
			} else {
				// Redirect to success page or login
				request.setAttribute("custCreated", true);
				response.sendRedirect(request.getContextPath() + "/CustomerLogin.jsp");
			}
		}
	}

}
