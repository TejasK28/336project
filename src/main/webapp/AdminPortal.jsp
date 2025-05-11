<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	import="java.util.List, java.util.Map, java.math.BigDecimal, java.text.NumberFormat, java.util.Locale, java.sql.Timestamp, java.util.Date"%>
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
				<tr data-username='<%=emp.get("EmployeeID")%>'>
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
								value='<%=emp.get("EmployeeID")%>' />
							<button type="submit" class="edit-btn btn-green">Edit</button>
						</form>
						<button class="cancel-btn btn-red"
							style="display: none; margin-left: 5px;">Cancel</button>
					</td>
					<td>
						<form action="<%=request.getContextPath()%>/DeleteEmployee"
							method="post" style="margin: 0;">
							<input type="hidden" name="username"
								value='<%=emp.get("EmployeeID")%>' />
							<button type="submit" class="btn-red"
								onclick="return confirm('Are you sure you want to delete this employee?');">
								Delete</button>
						</form>
					</td>
				</tr>
				<%
				} // end for emp
				} else {
				%>
				<tr>
					<td colspan="8">No employees found.</td>
				</tr>
				<%
				} // end if employees != null
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
				<tr data-customerid='<%=cust.get("CustomerID")%>'>
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
								value='<%=cust.get("CustomerID")%>' />
							<button type="submit" class="edit-btn btn-green">Edit</button>
						</form>
						<button class="cancel-btn btn-red"
							style="display: none; margin-left: 5px;">Cancel</button>
					</td>
					<td>
						<form action="<%=request.getContextPath()%>/DeleteCustomer"
							method="post" style="margin: 0;">
							<input type="hidden" name="customerID"
								value='<%=cust.get("CustomerID")%>' />
							<button type="submit" class="btn-red"
								onclick="return confirm('Are you sure you want to delete this customer?');">
								Delete</button>
						</form>
					</td>
				</tr>
				<%
				} // end for cust
				} else {
				%>
				<tr>
					<td colspan="9">No customers found.</td>
				</tr>
				<%
				} // end if customers
				%>
			</tbody>
		</table>

		<hr style="margin: 30px 0;" />
		<h2 style="margin-bottom: 15px;">Admin Reports</h2>

		<div class="report-section"
			style="margin-bottom: 30px; padding: 15px; border: 1px solid #ccc; border-radius: 5px; background-color: #f9f9f9;">
			<h3 style="margin-top: 0;">Monthly Sales Report</h3>

			<form action="<%=request.getContextPath()%>/SalesReportServlet"
				method="POST" style="margin-bottom: 20px;">
				<label for="reportMonthYear_jsp">Select Month (YYYY-MM):</label>
				<%
				String reportMonthYearValue = "";
				Object reportForMonthYearInputAttr = request.getAttribute("reportForMonthYearInput");
				if (reportForMonthYearInputAttr != null) {
					reportMonthYearValue = (String) reportForMonthYearInputAttr;
				}
				%>
				<input type="month" id="reportMonthYear_jsp" name="reportMonthYear"
					required value="<%=reportMonthYearValue%>"
					style="padding: 5px; margin-right: 10px;"> <input
					type="submit" value="Generate Report" class="btn-green"
					style="padding: 6px 12px;">
			</form>

			<%
			// Variables for Sales Report
			String displayMonthYear = reportMonthYearValue;
			String totalFareStr = "N/A";
			String totalBookingFeeStr = "N/A";
			String totalRevenueStr = "N/A";
			long numberOfTickets = 0;
			NumberFormat salesCurrencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);

			Boolean salesReportGenerated = (Boolean) request.getAttribute("reportGenerated"); // Renamed to avoid conflict
			String salesReportError = (String) request.getAttribute("reportError"); // Renamed
			Map<String, Object> salesData = null;
			Object salesDataAttr = request.getAttribute("salesData");
			if (salesDataAttr instanceof Map) {
				salesData = (Map<String, Object>) salesDataAttr;
			}

			if (salesReportError != null && !salesReportError.isEmpty()) {
			%>
			<p style="color: red; font-weight: bold;"><%=salesReportError%></p>
			<%
			} // Closes: if (salesReportError != null && !salesReportError.isEmpty())

			if (Boolean.TRUE.equals(salesReportGenerated)) {
			if (salesData != null) {
				// displayMonthYear is already initialized from reportMonthYearValue
				Object rawNumberOfTickets = salesData.get("numberOfTickets");
				if (rawNumberOfTickets instanceof Number) {
					numberOfTickets = ((Number) rawNumberOfTickets).longValue();
				}

				totalFareStr = JspFormatHelper.formatCurrencyValue(salesData.get("totalFare"), salesCurrencyFormatter);
				totalBookingFeeStr = JspFormatHelper.formatCurrencyValue(salesData.get("totalBookingFee"), salesCurrencyFormatter);
				totalRevenueStr = JspFormatHelper.formatCurrencyValue(salesData.get("totalRevenue"), salesCurrencyFormatter);

				if (numberOfTickets > 0) {
			%>
			<h4 style="margin-bottom: 10px;">
				Sales Report for
				<%=(displayMonthYear != null && !displayMonthYear.isEmpty() ? displayMonthYear : "Selected Period")%></h4>
			<table border="1" cellpadding="8" cellspacing="0"
				style="width: auto; min-width: 350px; border-collapse: collapse;">
				<thead style="background-color: #f0f0f0;">
					<tr>
						<th style="padding: 8px; text-align: left;">Metric</th>
						<th style="padding: 8px; text-align: left;">Value</th>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td style="padding: 8px;">Number of Tickets Sold</td>
						<td style="padding: 8px;"><%=numberOfTickets%></td>
					</tr>
					<tr>
						<td style="padding: 8px;">Total Fare Collected</td>
						<td style="padding: 8px;"><%=totalFareStr%></td>
					</tr>
					<tr>
						<td style="padding: 8px;">Total Booking Fees</td>
						<td style="padding: 8px;"><%=totalBookingFeeStr%></td>
					</tr>
					<tr>
						<td style="padding: 8px;"><strong>Total Revenue</strong></td>
						<td style="padding: 8px;"><strong><%=totalRevenueStr%></strong></td>
					</tr>
				</tbody>
			</table>
			<%
			} else { // No tickets sold, but report was generated for the month
			if (salesReportError == null || salesReportError.isEmpty()) {
			%>
			<h4 style="margin-bottom: 10px;">
				Sales Report for
				<%=(displayMonthYear != null && !displayMonthYear.isEmpty() ? displayMonthYear : "Selected Period")%></h4>
			<p>
				No sales data found for
				<%=(displayMonthYear != null && !displayMonthYear.isEmpty() ? displayMonthYear : "the selected period")%>.
			</p>
			<%
			}
			} // End if (numberOfTickets > 0)
			} else { // salesData is null but report was generated (and no specific error already shown)
			if (salesReportError == null || salesReportError.isEmpty()) {
			%>
			<p>
				Report data could not be retrieved or is empty for
				<%=(reportMonthYearValue != null && !reportMonthYearValue.isEmpty() ? reportMonthYearValue
		: "the selected period")%>.
			</p>
			<%
			}
			} // End if (salesData != null)
			} // End if (Boolean.TRUE.equals(salesReportGenerated))
			%>
		</div>

		<hr style="margin: 30px 0;" />

		<div class="report-section"
			style="margin-bottom: 30px; padding: 15px; border: 1px solid #ccc; border-radius: 5px; background-color: #f9f9f9;">
			<h3 style="margin-top: 0;">List Reservations</h3>
			<form action="<%=request.getContextPath()%>/ReservationReportServlet"
				method="POST" style="margin-bottom: 20px;">
				<label for="searchBy_jsp" style="margin-right: 5px;">Search
					By:</label> <select name="searchBy" id="searchBy_jsp"
					style="padding: 5px; margin-right: 10px;">
					<%
					String currentSearchBy = (String) request.getAttribute("searchByInput");
					if (currentSearchBy == null && request.getParameter("searchBy") != null) {
						currentSearchBy = request.getParameter("searchBy");
					}
					%>
					<option value="flightNumber"
						<%="flightNumber".equals(currentSearchBy) ? "selected" : ""%>>Flight
						Number</option>
					<option value="customerName"
						<%="customerName".equals(currentSearchBy) ? "selected" : ""%>>Customer
						Name</option>
					<option value="customerID"
						<%="customerID".equals(currentSearchBy) ? "selected" : ""%>>Customer
						ID</option>
				</select> <label for="searchValue_jsp" style="margin-right: 5px;">Search
					Value:</label>
				<%
				String currentSearchValue = (String) request.getAttribute("searchValueInput");
				if (currentSearchValue == null && request.getParameter("searchValue") != null) {
					currentSearchValue = request.getParameter("searchValue");
				}
				%>
				<input type="text" id="searchValue_jsp" name="searchValue" required
					value='<%=currentSearchValue != null ? currentSearchValue : ""%>'
					style="padding: 5px; margin-right: 10px; width: 200px;"> <input
					type="submit" value="Get Reservations" class="btn-green"
					style="padding: 6px 12px;">
			</form>

			<%
			Boolean reservationReportGenerated = (Boolean) request.getAttribute("reservationReportGenerated");
			String reservationReportError = (String) request.getAttribute("reservationReportError");
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> reservationsList = (List<Map<String, Object>>) request.getAttribute("reservationsList");
			String reservationReportTitle = (String) request.getAttribute("reservationReportTitle");

			NumberFormat reservationCurrencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
			java.text.SimpleDateFormat dateTimeFormatter = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			if (reservationReportError != null && !reservationReportError.isEmpty()) {
			%>
			<p style="color: red; font-weight: bold;"><%=reservationReportError%></p>
			<%
			} // End if reservationReportError

			if (Boolean.TRUE.equals(reservationReportGenerated)) {
			if (reservationReportTitle == null || reservationReportTitle.isEmpty()) {
				reservationReportTitle = "Reservation Search Results";
			}
			%>
			<h4 style="margin-bottom: 10px;"><%=reservationReportTitle%></h4>
			<%
			if (reservationsList != null && !reservationsList.isEmpty()) {
			%>
			<table border="1" cellpadding="8" cellspacing="0"
				style="width: 100%; border-collapse: collapse; margin-top: 10px;">
				<thead style="background-color: #e9ecef;">
					<tr>
						<th>Ticket ID</th>
						<th>Customer</th>
						<th>Flight Details</th>
						<th>Airline</th>
						<th>Depart Time</th>
						<th>Arrival Time</th>
						<th>Fare</th>
						<th>Booking Fee</th>
						<th>Purchase Date</th>
					</tr>
				</thead>
				<tbody>
					<%
					for (Map<String, Object> res : reservationsList) {
					%>
					<tr>
						<td><%=res.get("TicketID")%></td>
						<td><%=res.get("CustomerFirstName")%> <%=res.get("CustomerLastName")%><br />
							(<%=res.get("CustomerID")%>)</td>
						<td>Flt #: <%=res.get("FlightNumber")%> (ID: <%=res.get("FlightID")%>)<br />
							<%=res.get("FromAirportID")%> &rarr; <%=res.get("ToAirportID")%>
						</td>
						<td><%=res.get("AirlineName") != null ? res.get("AirlineName") : "N/A"%></td>
						<td>
							<%
							Object departTimeObj = res.get("DepartTime");
							String formattedDepartTime = "N/A";
							if (departTimeObj instanceof Timestamp) {
								formattedDepartTime = dateTimeFormatter.format(new Date(((Timestamp) departTimeObj).getTime()));
							} else if (departTimeObj != null) {
								formattedDepartTime = departTimeObj.toString();
							}
							%> <%=formattedDepartTime%>
						</td>
						<td>
							<%
							Object arrivalTimeObj = res.get("ArrivalTime");
							String formattedArrivalTime = "N/A";
							if (arrivalTimeObj instanceof Timestamp) {
								formattedArrivalTime = dateTimeFormatter.format(new Date(((Timestamp) arrivalTimeObj).getTime()));
							} else if (arrivalTimeObj != null) {
								formattedArrivalTime = arrivalTimeObj.toString();
							}
							%> <%=formattedArrivalTime%>
						</td>
						<td><%=JspFormatHelper.formatCurrencyValue(res.get("TicketFare"), reservationCurrencyFormatter)%></td>
						<td><%=JspFormatHelper.formatCurrencyValue(res.get("BookingFee"), reservationCurrencyFormatter)%></td>
						<td>
							<%
							Object purchaseTimeObj = res.get("PurchaseDateTime");
							String formattedPurchaseTime = "N/A";
							if (purchaseTimeObj instanceof Timestamp) {
								formattedPurchaseTime = dateTimeFormatter.format(new Date(((Timestamp) purchaseTimeObj).getTime()));
							} else if (purchaseTimeObj != null) {
								formattedPurchaseTime = purchaseTimeObj.toString();
							}
							%> <%=formattedPurchaseTime%>
						</td>
					</tr>
					<%
					} // End for reservationsList
					%>
				</tbody>
			</table>
			<%
			} else { // reservationsList is null or empty
			if ((reservationReportError == null || reservationReportError.isEmpty())
					&& (currentSearchValue != null && !currentSearchValue.isEmpty())) {
				// Only show "No data" if a search was performed and no other error was displayed
			%>
			<p>No reservation data found for the specified criteria.</p>
			<%
			}
			} // End if reservationsList not empty
			} // End if reservationReportGenerated
			%>
		</div>

		<hr style="margin: 30px 0;" />

		<%-- New Section: Top Revenue Customer --%>
		<div class="report-section"
			style="margin-bottom: 30px; padding: 15px; border: 1px solid #ccc; border-radius: 5px; background-color: #f9f9f9;">
			<h3 style="margin-top: 0;">Top Revenue Customer</h3>
			<form action="<%=request.getContextPath()%>/AdminStatsServlet"
				method="GET" style="margin-bottom: 10px;">
				<input type="hidden" name="action" value="topCustomer"> <input
					type="submit" value="Find Top Customer" class="btn-green">
			</form>

			<%
			Boolean topCustomerReportGenerated = (Boolean) request.getAttribute("topCustomerReportGenerated");
			if (Boolean.TRUE.equals(topCustomerReportGenerated)) {
				@SuppressWarnings("unchecked")
				Map<String, Object> topCustomerData = (Map<String, Object>) request.getAttribute("topCustomerData");
				NumberFormat statsCurrencyFormatter = NumberFormat.getCurrencyInstance(Locale.US); // Local instance

				if (topCustomerData != null && !topCustomerData.isEmpty()) {
			%>
			<p>
				<strong>Customer ID:</strong>
				<%=topCustomerData.get("CustomerID")%><br /> <strong>Name:</strong>
				<%=topCustomerData.get("FirstName")%>
				<%=topCustomerData.get("LastName")%><br /> <strong>Total
					Revenue Generated:</strong>
				<%=JspFormatHelper.formatCurrencyValue(topCustomerData.get("TotalRevenue"), statsCurrencyFormatter)%>
			</p>
			<%
			} else {
			%>
			<p>No customer revenue data found or no tickets sold.</p>
			<%
			}
			}
			%>
		</div>

		<hr style="margin: 30px 0;" />

		<%-- New Section: Most Active Flights --%>
		<div class="report-section"
			style="margin-bottom: 30px; padding: 15px; border: 1px solid #ccc; border-radius: 5px; background-color: #f9f9f9;">
			<h3 style="margin-top: 0;">Most Active Flights (Top 5)</h3>
			<form action="<%=request.getContextPath()%>/AdminStatsServlet"
				method="GET" style="margin-bottom: 10px;">
				<input type="hidden" name="action" value="activeFlights"> <input
					type="submit" value="Show Most Active Flights" class="btn-green">
			</form>

			<%
			Boolean activeFlightsReportGenerated = (Boolean) request.getAttribute("activeFlightsReportGenerated");
			if (Boolean.TRUE.equals(activeFlightsReportGenerated)) {
				@SuppressWarnings("unchecked")
				List<Map<String, Object>> activeFlightsData = (List<Map<String, Object>>) request.getAttribute("activeFlightsData");

				if (activeFlightsData != null && !activeFlightsData.isEmpty()) {
			%>
			<table border="1" cellpadding="8" cellspacing="0"
				style="width: auto; min-width: 500px; border-collapse: collapse; margin-top: 10px;">
				<thead style="background-color: #e9ecef;">
					<tr>
						<th>Flight ID</th>
						<th>Flight Number</th>
						<th>Route</th>
						<th>Airline</th>
						<th>Tickets Sold</th>
					</tr>
				</thead>
				<tbody>
					<%
					for (Map<String, Object> flight : activeFlightsData) {
					%>
					<tr>
						<td><%=flight.get("FlightID")%></td>
						<td><%=flight.get("FlightNumber")%></td>
						<td><%=flight.get("FromAirportID")%> &rarr; <%=flight.get("ToAirportID")%></td>
						<td><%=flight.get("AirlineName")%></td>
						<td><%=flight.get("TicketCount")%></td>
					</tr>
					<%
					}
					%>
				</tbody>
			</table>
			<%
			} else {
			%>
			<p>No flight activity data found or no tickets sold.</p>
			<%
			}
			}
			%>
		</div>

		<hr style="margin: 30px 0;" />

		<%-- New Section: Revenue by Entity Report --%>
		<div class="report-section"
			style="margin-bottom: 30px; padding: 15px; border: 1px solid #ccc; border-radius: 5px; background-color: #f9f9f9;">
			<h3 style="margin-top: 0;">Revenue by Entity</h3>
			<form action="<%=request.getContextPath()%>/RevenueReportServlet"
				method="POST" style="margin-bottom: 20px;">
				<label for="revenueReportType_jsp" style="margin-right: 5px;">Report
					For:</label> <select name="revenueReportType" id="revenueReportType_jsp"
					style="padding: 5px; margin-right: 10px;">
					<%
					String currentRevReportType = (String) request.getAttribute("revenueReportTypeInput");
					%>
					<option value="flight"
						<%="flight".equals(currentRevReportType) ? "selected" : ""%>>Flight
						(by ID)</option>
					<option value="airline"
						<%="airline".equals(currentRevReportType) ? "selected" : ""%>>Airline
						(by ID)</option>
					<option value="customer"
						<%="customer".equals(currentRevReportType) ? "selected" : ""%>>Customer
						(by ID)</option>
				</select> <label for="revenueIdentifier_jsp" style="margin-right: 5px;">Enter
					ID:</label> <input type="text" id="revenueIdentifier_jsp"
					name="revenueIdentifier" required
					value='<%=request.getAttribute("revenueIdentifierInput") != null ? (String) request.getAttribute("revenueIdentifierInput")
				: ""%>'
					style="padding: 5px; margin-right: 10px; width: 150px;"> <input
					type="submit" value="Get Revenue" class="btn-green"
					style="padding: 6px 12px;">
			</form>

			<%
			Boolean revenueByEntityReportGenerated = (Boolean) request.getAttribute("revenueByEntityReportGenerated");
			if (Boolean.TRUE.equals(revenueByEntityReportGenerated)) {
				String revenueReportErrorMsg = (String) request.getAttribute("revenueReportErrorMsg");
				BigDecimal totalRevenueForEntity = (BigDecimal) request.getAttribute("totalRevenueForEntity");
				String revenueReportTitleStr = (String) request.getAttribute("revenueReportTitle"); // Renamed to avoid conflict
				boolean revenueDataFound = Boolean.TRUE.equals(request.getAttribute("revenueReportDataFound"));

				NumberFormat entityRevenueFormatter = NumberFormat.getCurrencyInstance(Locale.US);

				if (revenueReportErrorMsg != null && !revenueReportErrorMsg.isEmpty()) {
			%>
			<p style="color: red; font-weight: bold;"><%=revenueReportErrorMsg%></p>
			<%
			} else if (revenueDataFound) {
			%>
			<h4 style="margin-bottom: 10px;"><%=revenueReportTitleStr != null ? revenueReportTitleStr : "Revenue Report"%></h4>
			<p>
				<strong>Total Revenue:</strong>
				<%=JspFormatHelper.formatCurrencyValue(totalRevenueForEntity, entityRevenueFormatter)%></p>
			<%
			} else if (!revenueDataFound && request.getParameter("revenueIdentifier") != null) { // Attempted search but no data
			%>
			<h4 style="margin-bottom: 10px;"><%=revenueReportTitleStr != null ? revenueReportTitleStr : "Revenue Report"%></h4>
			<p>No revenue data found for the specified identifier, or revenue
				is $0.00.</p>
			<%
			}
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


		<form
			action="${pageContext.request.contextPath}/CreateCustomer?fromAdmin=true"
			method="get" style="display: inline; margin-top: 20px;">
			<button type="submit" class="btn-green">Add New Customer</button>
		</form>

	</main>
	<%-- 	<script src="<%= request.getContextPath() %>/js/adminPortal.js"></script> --%>

	<%!// JSP Declaration: Makes JspFormatHelper a static inner class of the generated servlet
	// so its static methods are accessible from anywhere in the JSP
	static class JspFormatHelper {
		static String formatCurrencyValue(Object value, NumberFormat formatter) {
			if (value == null) {
				return formatter.format(0.0);
			}
			if (value instanceof BigDecimal) {
				return formatter.format(value);
			} else if (value instanceof Number) {
				return formatter.format(((Number) value).doubleValue());
			}
			try {
				return formatter.format(Double.parseDouble(value.toString()));
			} catch (NumberFormatException e) {
				System.err.println("JspFormatHelper: Could not parse to double: " + value);
				return "N/A";
			}
		}
	}%>
</body>
</html>