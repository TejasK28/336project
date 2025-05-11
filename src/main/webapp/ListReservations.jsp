<%@ page import="java.util.List,java.util.Map" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>Manage Reservations</title>
</head>
<body>
  <h1>Manage Reservations</h1>

  <!-- 1) pick a customer -->
  <form method="get" action="${pageContext.request.contextPath}/CustRep/reservations">
    <label>Customer:
      <select name="customerId" onchange="this.form.submit()">
        <option value="">-- select --</option>
        <% 
           List<Map<String,Object>> customers = (List<Map<String,Object>>)request.getAttribute("customers");
           String selected = (String)request.getAttribute("selectedCustomerId");
           for (Map<String,Object> c : customers) {
             String id   = (String)c.get("CustomerID");
             String name = c.get("FirstName") + " " + c.get("LastName");
        %>
          <option value="<%=id%>" <%= id.equals(selected) ? "selected" : "" %>>
            <%= name %> (<%= id %>)
          </option>
        <% } %>
      </select>
    </label>
  </form>

  <!-- 2) show their reservations -->
  <%
    List<Map<String,Object>> res = (List<Map<String,Object>>)request.getAttribute("reservations");
    if (res != null && !res.isEmpty()) {
  %>
    <table border="1" cellpadding="4">
      <tr>
        <th>Flight#</th>
        <th>From/To</th>
        <th>Class</th>
        <th>Fare</th>
        <th>Booked At</th>
        <th>Edit</th>
      </tr>
      <% for (Map<String,Object> t : res) {
           int    fnum   = ((Number)t.get("FlightNumber")).intValue();
           String from   = (String)t.get("FromAirportID");
           String to     = (String)t.get("ToAirportID");
           String airline= (String)t.get("AirlineName");
           String cls    = (String)t.get("Class");
           Object fareO  = t.get("TicketFare");
           Object booked = t.get("PurchaseDateTime");
           int    fid    = ((Number)t.get("FlightID")).intValue();
           int    seat   = ((Number)t.get("SeatNumber")).intValue();
      %>
        <tr>
          <td><%= fnum %></td>
          <td><%= from %> to <%= to %> (<%= airline %>)</td>
          <td><%= cls %></td>
          <td><%= fareO %></td>
          <td><%= booked %></td>
          <td>
            <a href="${pageContext.request.contextPath}/CustRep/reservation/edit
                ?customerId=${param.customerId}
                &flightId=${t.FlightID}
                &seatNo=${t.SeatNumber}">
              Edit
            </a>
          </td>
        </tr>
      <% } %>
    </table>
  <% } %>

  <% if (request.getParameter("customerId") != null) { %>
    <p>
      <a href="${pageContext.request.contextPath}/CustRep/reservation/new?customerId=${param.customerId}">
        + New Reservation
      </a>
    </p>
  <% } %>

  <p>
    <a href="${pageContext.request.contextPath}/CustRep">Back to Dashboard</a>
  </p>
</body>
</html>
