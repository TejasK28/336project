<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
         <%@ page import="java.util.List" %>
<%@ page import="java.util.List,java.util.Map, java.time.LocalDate" %>
         
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Flight Home</title>

    <!-- ===== BASIC STYLES ===== -->
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/styles.css"> 
</head>
<body>

<!-- ========== HEADER ========== -->
<jsp:include page="header.jsp"></jsp:include>

<!-- ===== HERO / MAIN CONTENT PLACEHOLDER ===== -->

<% String uname = (String) session.getAttribute("uname");
if (uname == null) { %>
<section class="hero">
    <h1>Book Your Next Flight</h1>
    <p>Fast • Easy • Secure</p>
</section>
<% } else { %>

<h1>Welcome, <%= session.getAttribute("uname") %></h1>

<%-- Flash message for added flight --%>
<% if (request.getAttribute("FlightAdded") != null) { %>
    <p>Flight added!</p>
<% } %>

<%
    @SuppressWarnings("unchecked")
    List<Map<String,Object>> flightPlanResults =
        (List<Map<String,Object>>) request.getAttribute("flightPlanResults");

    if (flightPlanResults != null && !flightPlanResults.isEmpty()) {
%>
    <ul>
    <%
        for (Map<String,Object> flight : flightPlanResults) {
            String from = (String) flight.get("FromAirportID");
            String to   = (String) flight.get("ToAirportID");
/*             LocalDate departing = (LocalDate) flight.get("DepartTime");
            LocalDate arriving =  (LocalDate) flight.get("ArrivalTime");
 */    %>
        <li><%= from %> &rarr; <%= to %></li>
    <%
        }
    %>
    </ul>
<%
    } else {
%>
    <p>Your flight plan is empty.</p>
<%
    }
%>


<a href="<%= request.getContextPath() %>/SearchFlightsServlet">Search flights!</a>
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




<% } %>

<!-- ===== SCRIPTS ===== -->
</body>
</html>
