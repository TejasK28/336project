<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.List,java.util.Map, java.time.LocalDate"%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Flight Home</title>

<!-- ===== BASIC STYLES ===== -->
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/css/styles.css">
</head>
<body>

	<!-- ========== HEADER ========== -->
	<jsp:include page="header.jsp"></jsp:include>

	<!-- ===== HERO / MAIN CONTENT PLACEHOLDER ===== -->

	<%
	String uname = (String) session.getAttribute("uname");
	if (uname == null) {
	%>
	<section class="hero">
		<h1>Book Your Next Flight</h1>
		<p>Fast • Easy • Secure</p>
	</section>
	<%
	} else {
	%>

	<h1>
		Welcome,
		<%=session.getAttribute("uname")%></h1>

	<%-- Flash message for added flight --%>
	<%
	if (request.getAttribute("FlightAdded") != null) {
	%>
	<p>Flight added!</p>
	<%
	}
	%>

	<%
	@SuppressWarnings("unchecked")
	List<Map<String, Object>> flightPlanResults = (List<Map<String, Object>>) request.getAttribute("flightPlanResults");

	if (flightPlanResults != null && !flightPlanResults.isEmpty()) {
	%>
	<h1>Your cart:</h1>
	<ul>
		<%
		for (Map<String, Object> flight : flightPlanResults) {
			String from = (String) flight.get("FromAirportID");
			String to = (String) flight.get("ToAirportID");
			/*             LocalDate departing = (LocalDate) flight.get("DepartTime");
			            LocalDate arriving =  (LocalDate) flight.get("ArrivalTime");
			 */
		%>
		<li><%=from%> &rarr; <%=to%></li>
		<%
		}
		%>
	</ul>
	<form method="get"
		action="<%=request.getContextPath() + "/reserveFlights"%>">
		<%
		for (int i = 0; i < flightPlanResults.size(); i++) {
		%>
		<input type="hidden" name="flight<%=i + 1%>"
			value="<%=flightPlanResults.get(i).get("FlightID")%>">
		<%
		}
		%>
		<button type="submit">Make reservations for all flights in
			cart.</button>
	</form>
	<%
	} else {
	%>
	<p>Your flight plan is empty.</p>
	<%
	}
	%>

	<%
    @SuppressWarnings("unchecked")
    List<Map<String, Object>> userFlightPlans =
        (List<Map<String, Object>>) request.getAttribute("reservedFlightPlans");
    if (userFlightPlans != null && !userFlightPlans.isEmpty()) {
%>
  <h2>Your Flight Plans</h2>
  <table border="1" cellpadding="5" cellspacing="0">
    <tr>
      <th>FlightPlanID</th>
      <th>Total Duration (min)</th>
      <th>Total Fare</th>
    </tr>
    <% for (Map<String, Object> flightPlan : userFlightPlans) {
         Object idObj   = flightPlan.get("FlightPlanID");
         Object durObj  = flightPlan.get("TotalDuration");
         Object fareObj = flightPlan.get("TotalFare");
         String id      = (idObj   != null ? idObj.toString()   : "");
         String dur     = (durObj  != null ? durObj.toString()  : "");
         double fare    = fareObj != null
                          ? ((Number)fareObj).doubleValue()
                          : 0.0;
    %>
    <tr>
      <td><a href="<%= request.getContextPath() + "/ViewFlightPlan/" + id %>"><%= id %></a></td>
      <td><%= dur %></td>
      <td>$<%= String.format("%.2f", fare) %></td>
    </tr>
    <% } %>
  </table>
<% 
    } else {
%>
  <p>No reserved flight plans found.</p>
<% 
    }
%>

	

	<a href="<%=request.getContextPath()%>/SearchFlightsServlet">Search
		flights!</a>
	<!-- This section focuses on the first 6 bullet points of user functionality -->


<p>
  <a href="<%=request.getContextPath()%>/MyQuestions">
    📝 My Questions
  </a>
</p>


<!-- This section focuses on the question form -->
<section class="ask-form">
  <h2>Have a question?</h2>
  <form action="${pageContext.request.contextPath}/PostQuestion" method="post">
    <input type="hidden" name="customerId" value="${sessionScope.uname}" />
    <textarea name="message" rows="3" required
              placeholder="Type your question here…"></textarea>
    <button type="submit">Send to support</button>
  </form>
</section>


<section class="search-questions">
  <form action="${pageContext.request.contextPath}/SearchQuestions" method="get">
    <label for="searchQ">Search Q&A:</label>
    <input type="text" id="searchQ" name="q" placeholder="keyword…">
    <button type="submit">🔎</button>
  </form>
</section>




	<%
	}
	%>

	<!-- ===== SCRIPTS ===== -->
</body>
</html>
