<%@ page import="java.util.List,java.util.Map" %>
<%
  @SuppressWarnings("unchecked")
  Map<String,Object> ticket   = (Map<String,Object>) request.getAttribute("ticket");
  @SuppressWarnings("unchecked")
  List<Map<String,Object>> flights = (List<Map<String,Object>>) request.getAttribute("flights");
  String custId = (String) request.getAttribute("customerId");
  boolean editing = (ticket != null);
%>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title><%= editing ? "Edit" : "New" %> Reservation</title>
</head>
<body>
  <h1><%= editing ? "Edit" : "New" %> Reservation</h1>

  <form method="post"
        action="${pageContext.request.contextPath}/CustRep/reservation/<%= editing ? "update" : "create" %>">

    <!-- always send customerId -->
    <input type="hidden" name="customerId" value="<%= custId %>"/>

    <% if (editing) { %>
      <!-- so update knows what to delete first -->
      <input type="hidden" name="oldFlightId" value="<%= ticket.get("FlightID") %>"/>
      <input type="hidden" name="oldSeatNo"   value="<%= ticket.get("SeatNumber") %>"/>
    <% } %>

    <p>
      <label for="flight">Flight:</label>
      <select name="flightId" id="flight" required>
        <option value="">-- select flight --</option>
        <% for (Map<String,Object> f : flights) {
             int fid  = ((Number)f.get("FlightID")).intValue();
             int num  = ((Number)f.get("FlightNumber")).intValue();
             String from = (String)f.get("FromAirportID");
             String to   = (String)f.get("ToAirportID");
             boolean sel = editing 
                       && fid == ((Number)ticket.get("FlightID")).intValue();
        %>
          <option value="<%= fid %>" <%= sel ? "selected" : "" %>>
            <%= num %> / <%= to %> / <%= from %>
          </option>
        <% } %>
      </select>
    </p>

    <p>
      <label for="cls">Class:</label>
      <select name="travelClass" id="cls" required>
        <option value="Economy"  <%= editing && "Economy".equals(ticket.get("Class"))  ? "selected" : "" %>>Economy</option>
        <option value="Business" <%= editing && "Business".equals(ticket.get("Class")) ? "selected" : "" %>>Business</option>
        <option value="First"    <%= editing && "First".equals(ticket.get("Class"))    ? "selected" : "" %>>First</option>
      </select>
    </p>

    <p>
      <label for="fare">Fare:</label>
      <input type="number" step="0.01" name="fare" id="fare"
             value="<%= editing ? ticket.get("TicketFare") : "" %>" required/>
    </p>

    <p>
      <button type="submit"><%= editing ? "Update" : "Book" %></button>
      <a href="${pageContext.request.contextPath}/CustRep/reservations?customerId=<%=custId%>">
        Back
      </a>
    </p>

  </form>
</body>
</html>
