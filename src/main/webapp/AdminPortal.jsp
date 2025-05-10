<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="java.util.List, java.util.Map, java.math.BigDecimal, java.text.NumberFormat, java.util.Locale" %>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Admin Portal</title>
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/css/styles.css">
</head>

<body>
	<!-- HEADER -->
	<jsp:include page="header.jsp" />
	<span id="contextPath" style="display: none"><%=request.getContextPath()%></span>

	<main class="main-content">
		<h1>Manage Employees</h1>
		<%
		List<Map<String, Object>> employees = (List<Map<String, Object>>) request.getAttribute("employees");
		%>
		<p>
			Employees:
			<%=employees != null ? employees.size() : 0%></p>

		<table border="1" cellpadding="8" cellspacing="0">
			<thead>
				<tr>
					<th>Username</th>
					<th>First Name</th>
					<th>Last Name</th>
					<th>Email</th>
					<th>Is Admin</th>
					<th>Is Customer Representative</th>
					<th>Actions</th>
					<th>Delete</th>
				</tr>
			</thead>
			<tbody>
				<%
				if (employees != null) {
					for (Map<String, Object> emp : employees) {
				%>
				<tr data-username="<%=emp.get("EmployeeID")%>">
					<td><%=emp.get("EmployeeID")%></td>
					<td class="editable" data-field="FirstName"><%=emp.get("FirstName")%></td>
					<td class="editable" data-field="LastName"><%=emp.get("LastName")%></td>
					<td class="editable" data-field="Email"><%=emp.get("Email")%></td>
					<td><input type="checkbox" name="isAdmin" disabled
						<%=Boolean.TRUE.equals(emp.get("isAdmin")) ? "checked" : ""%> />
					</td>
					<td><input type="checkbox" name="isCustomerRepresentative"
						disabled
						<%=Boolean.TRUE.equals(emp.get("isCustomerRepresentative")) ? "checked" : ""%> />
					</td>
					<td>
						<form action="<%=request.getContextPath()%>/EditEmployee"
							method="get">
							<input type="hidden" name="username"
								value="<%=emp.get("EmployeeID")%>" />
							<button type="submit" class="edit-btn btn-green">Edit</button>
						</form>
						<button class="cancel-btn btn-red"
							style="display: none; margin-left: 5px;">Cancel</button>
					</td>
					<td>
						<form action="<%=request.getContextPath()%>/DeleteEmployee"
							method="post" style="margin: 0;">
							<input type="hidden" name="username"
								value="<%=emp.get("EmployeeID")%>" />
							<button type="submit" class="btn-red"
								onclick="return confirm('Are you sure you want to delete this employee?');">
								Delete</button>
						</form>
					</td>
				</tr>
				<%
				}
				} else {
				%>
				<tr>
					<td colspan="8">No employees found.</td>
				</tr>
				<%
				}
				%>
			</tbody>
		</table>


		<h1>Manage Customers</h1>
		<%
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> customers = (List<Map<String, Object>>) request.getAttribute("customers");
		%>
		<p>
			Customers:
			<%=customers != null ? customers.size() : 0%></p>

		<table border="1" cellpadding="8" cellspacing="0">
			<thead>
				<tr>
					<th>Customer ID</th>
					<th>First Name</th>
					<th>Last Name</th>
					<th>Email</th>
					<th>Phone</th>
					<th>Address</th>
					<th>Actions</th>
					<th>Delete</th>
				</tr>
			</thead>
			<tbody>
				<%
				if (customers != null && !customers.isEmpty()) {
					for (Map<String, Object> cust : customers) {
				%>
				<tr data-customerid="<%=cust.get("CustomerID")%>">
					<td><%=cust.get("CustomerID")%></td>
					<td class="editable" data-field="FirstName"><%=cust.get("FirstName")%></td>
					<td class="editable" data-field="LastName"><%=cust.get("LastName")%></td>
					<td class="editable" data-field="Email"><%=cust.get("Email")%></td>
					<td class="editable" data-field="Phone"><%=cust.get("Phone")%></td>
					<td class="editable" data-field="Address"><%=cust.get("Address")%></td>
					<td>
						<form action="<%=request.getContextPath()%>/EditCustomer"
							method="get" style="display: inline;">
							<input type="hidden" name="customerID"
								value="<%=cust.get("CustomerID")%>" />
							<button type="submit" class="edit-btn btn-green">Edit</button>
						</form>
						<button class="cancel-btn btn-red"
							style="display: none; margin-left: 5px;">Cancel</button>
					</td>
					<td>
						<form action="<%=request.getContextPath()%>/DeleteCustomer"
							method="post" style="margin: 0;">
							<!-- in AdminPortal.jsp -->
							<input type="hidden" name="customerID" value="<%=cust.get("CustomerID")%>" />

							<button type="submit" class="btn-red"
								onclick="return confirm('Are you sure you want to delete this customer?');">
								Delete</button>
						</form>
					</td>
				</tr>
				<%
				} // end for
				} else {
				%>
				<tr>
					<td colspan="9">No customers found.</td>
				</tr>
				<%
				}
				%>
			</tbody>
		</table>

        <hr style="margin: 30px 0;"/>
        <h2 style="margin-bottom: 15px;">Admin Reports</h2>

        <div class="report-section" style="margin-bottom: 30px; padding: 15px; border: 1px solid #ccc; border-radius: 5px; background-color: #f9f9f9;">
            <h3 style="margin-top:0;">Monthly Sales Report</h3>
            
            <%-- Form action points to /SalesReport, which is handled by Admin.java --%>
<form action="<%=request.getContextPath()%>/SalesReportServlet" method="POST" style="margin-bottom: 20px;">
                <label for="reportMonthYear_jsp">Select Month (YYYY-MM):</label>
                <%
                    String reportMonthYearValue = ""; // Default
                    Object reportForMonthYearInputAttr = request.getAttribute("reportForMonthYearInput");
                    if (reportForMonthYearInputAttr != null) {
                        reportMonthYearValue = (String) reportForMonthYearInputAttr;
                    }
                %>
                <input type="month" id="reportMonthYear_jsp" name="reportMonthYear" required value="<%= reportMonthYearValue %>" style="padding: 5px; margin-right: 10px;">
                <input type="submit" value="Generate Report" class="btn-green" style="padding: 6px 12px;">
            </form>

            <%
                Boolean reportGenerated = (Boolean) request.getAttribute("reportGenerated");
                String reportError = (String) request.getAttribute("reportError");
                Map<String, Object> salesData = null;
                Object salesDataAttr = request.getAttribute("salesData");
                if (salesDataAttr instanceof Map) {
                     salesData = (Map<String, Object>) salesDataAttr;
                }

                if (reportError != null && !reportError.isEmpty()) {
            %>
                <p style="color: red; font-weight: bold;"><%= reportError %></p>
            <%
                }

                if (reportGenerated != null && reportGenerated && salesData != null) {
                    String displayMonthYear = reportMonthYearValue;
            %>
                <h4 style="margin-bottom: 10px;">Sales Report for <%= (displayMonthYear != null && !displayMonthYear.isEmpty() ? displayMonthYear : "Selected Period") %></h4>
            <%
                    long numberOfTickets = 0;
                    Object rawNumberOfTickets = salesData.get("numberOfTickets");
                    if (rawNumberOfTickets instanceof Number) {
                        numberOfTickets = ((Number) rawNumberOfTickets).longValue();
                    }

                    NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
                    
                    // Helper for formatting (can be a static method in a utility class if used often)
                    class JspFormatHelper { // Defined locally for this scriptlet block
                        static String formatCurrencyValue(Object value, NumberFormat formatter) {
                            if (value instanceof BigDecimal) {
                                return formatter.format(value);
                            } else if (value instanceof Number) {
                                 return formatter.format(((Number)value).doubleValue());
                            } else if (value == null) {
                                return formatter.format(0.0);
                            }
                            try { // Attempt to parse if it's a string representing a number
                                return formatter.format(Double.parseDouble(value.toString()));
                            } catch (Exception e) {
                                return "$Error"; // Or some other error indication
                            }
                        }
                    }
                    
                    String totalFareStr = JspFormatHelper.formatCurrencyValue(salesData.get("totalFare"), currencyFormatter);
                    String totalBookingFeeStr = JspFormatHelper.formatCurrencyValue(salesData.get("totalBookingFee"), currencyFormatter);
                    String totalRevenueStr = JspFormatHelper.formatCurrencyValue(salesData.get("totalRevenue"), currencyFormatter);

                    if (numberOfTickets > 0) {
            %>
                        <table border="1" cellpadding="8" cellspacing="0" style="width:auto; min-width: 350px; border-collapse: collapse;">
                            <thead style="background-color: #f0f0f0;">
                                <tr>
                                    <th style="padding: 8px; text-align:left;">Metric</th>
                                    <th style="padding: 8px; text-align:left;">Value</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td style="padding: 8px;">Number of Tickets Sold</td>
                                    <td style="padding: 8px;"><%= numberOfTickets %></td>
                                </tr>
                                <tr>
                                    <td style="padding: 8px;">Total Fare Collected</td>
                                    <td style="padding: 8px;"><%= totalFareStr %></td>
                                </tr>
                                <tr>
                                    <td style="padding: 8px;">Total Booking Fees</td>
                                    <td style="padding: 8px;"><%= totalBookingFeeStr %></td>
                                </tr>
                                <tr>
                                    <td style="padding: 8px;"><strong>Total Revenue</strong></td>
                                    <td style="padding: 8px;"><strong><%= totalRevenueStr %></strong></td>
                                </tr>
                            </tbody>
                        </table>
            <%
                    } else { // No tickets sold, but report was generated (and no error)
                        if (reportError == null || reportError.isEmpty()) { // Only show "no data" if no other error
            %>
                        <p>No sales data found for <%= (displayMonthYear != null && !displayMonthYear.isEmpty() ? displayMonthYear : "the selected period") %>.</p>
            <%
                        }
                    }
                } else if (reportGenerated != null && reportGenerated && salesData == null && (reportError == null || reportError.isEmpty())) {
            %>
                    <p>Report data could not be retrieved or is empty for <%= (reportMonthYearValue != null && !reportMonthYearValue.isEmpty() ? reportMonthYearValue : "the selected period") %>.</p>
            <%
                }
            %>
        </div>

		<h1>Create New Employee</h1>
		<form class="full-form" action="CreateEmployee" method="post">
			<div class="form-group">
				<label for="EmployeeID">Username</label> <input type="text"
					id="EmployeeID" name="EmployeeID" required />
			</div>
			<div class="form-group">
				<label for="Password">Password</label> <input type="password"
					id="Password" name="Password" required />
			</div>
			<div class="form-group">
				<label for="FirstName">First Name</label> <input type="text"
					id="FirstName" name="FirstName" required />
			</div>
			<div class="form-group">
				<label for="LastName">Last Name</label> <input type="text"
					id="LastName" name="LastName" required />
			</div>
			<div class="form-group">
				<label for="Email">Email</label> <input type="text" id="Email"
					name="Email" required />
			</div>
			<div class="form-group">
				<label> <input type="checkbox" name="isAdmin" /> Is Admin
				</label>
			</div>
			<div class="form-group">
				<label> <input type="checkbox"
					name="isCustomerRepresentative" /> Is Customer Representative
				</label>
			</div>
			<div class="submit-row">
				<input type="submit" value="Create Employee" />
			</div>
		</form>
		
		
		<!-- Create new Customer -->

		<form action="${pageContext.request.contextPath}/CreateCustomer?fromAdmin=true"
	      method="get" style="display:inline">
		  <button type="submit" class="btn-green">Add New Customer</button>
		</form>
		
	</main>
	<%-- 	<script src="<%= request.getContextPath() %>/js/adminPortal.js"></script> --%>
</body>
</html>
