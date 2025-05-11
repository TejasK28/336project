<%@ page import="java.util.Map" %>
<%
  Map<String,Object> t    = (Map<String,Object>)request.getAttribute("ticket");
  boolean            edit = (t != null);
  String             cust = (String)request.getAttribute("customerId");
%>
<!DOCTYPE html>
<html>
<head><meta charset="UTF-8">
<title><%= edit? "Edit":"New" %> Reservation</title>
</head>
<body>
  <h1><%= edit? "Edit":"New" %> Reservation</h1>

  <form method="post"
        action="<%=request.getContextPath()%>/CustRep/reservation/<%= edit? "update":"create" %>">
    <input type="hidden" name="customerId" value="<%=cust%>"/>
    <% if (edit) { %>
      <input type="hidden" name="oldFlightId" value="<%=t.get("FlightID")%>"/>
      <input type="hidden" name="oldSeatNo"   value="<%=t.get("SeatNumber")%>"/>
    <% } %>

    <label>Flight:
      <select name="flightId">
        <% 
           for (Map<String,Object> f : (java.util.List<Map<String,Object>>)request.getAttribute("flights")) {
             int   id    = ((Number)f.get("FlightID")).intValue();
             int   num   = ((Number)f.get("FlightNumber")).intValue();
             String frm  = (String)f.get("FromAirportID");
             String to   = (String)f.get("ToAirportID");
             boolean sel = edit && id == ((Number)t.get("FlightID")).intValue();
        %>
          <option value="<%=id%>" <%= sel? "selected":"" %>>
            <%= num %> - <%= frm %> - <%= to %>
          </option>
        <% } %>
      </select>
    </label>
    <br/>

    <label>Class:
      <select name="travelClass">
        <% for (String c : new String[]{"Economy","Business","First"}) {
             boolean sel = edit && c.equals(t.get("Class"));
        %>
          <option <%= sel? "selected":"" %>><%=c%></option>
        <% } %>
      </select>
    </label>
    <br/>

    <label>Fare:
      <input type="number" name="fare" step="0.01" required
             value="<%= edit? t.get("TicketFare") : "" %>"/>
    </label>
    <br/>

    <button type="submit"><%= edit? "Update":"Create" %></button>
  </form>

  <p>
    <a href="<%=request.getContextPath()%>/CustRep/reservations
             ?customerId=<%=cust%>">
      Back
    </a>
  </p>
</body>
</html>
