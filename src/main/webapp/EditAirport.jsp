<%@ page contentType="text/html; charset=UTF-8" import="java.util.Map" %>
<%
  @SuppressWarnings("unchecked")
  Map<String, Object> airport = (Map<String, Object>) request.getAttribute("airport");
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Edit Airport</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/css/styles.css">
</head>
<body>
  <jsp:include page="header.jsp" />
  <h1>Edit Airport</h1>
  <form method="post" action="<%= request.getContextPath() %>/editAirport">
    <!-- Hidden original PK -->
    <input
      type="hidden"
      name="originalAID"
      value='<%= String.valueOf(airport.get("AirportID")) %>' />

    <label for="identifierCode">Airport ID (identifier code):</label>
    <input
      type="text"
      id="identifierCode"
      name="identifierCode"
      value='<%= String.valueOf(airport.get("AirportID")) %>'
      required />

    <label for="name">Name:</label>
    <input
      type="text"
      id="name"
      name="Name"
      value='<%= String.valueOf(airport.get("Name")) %>'
      required />

    <label for="city">City:</label>
    <input
      type="text"
      id="city"
      name="City"
      value='<%= String.valueOf(airport.get("City")) %>'
      required />

    <label for="country">Country:</label>
    <input
      type="text"
      id="country"
      name="Country"
      value='<%= String.valueOf(airport.get("Country")) %>'
      required />

    <button type="submit" class="btn-save">Save Changes</button>
    <a href="<%= request.getContextPath() %>/CustRep">
      <button type="button" class="btn-cancel">Cancel</button>
    </a>
  </form>
</body>
</html>
