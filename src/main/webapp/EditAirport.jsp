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
  <form method="post" action="<%= request.getContextPath() %>/CustRep/editAirport">
    <!-- Hidden original PK -->
    <input
      type="hidden"
      name="originalAID"
      value="<%= airport.get("AirportID") %>" />

    <label for="identifierCode">Airport ID (identifier code):</label>
    <input
      type="text"
      id="identifierCode"
      name="identifierCode"
      value="<%= airport.get("AirportID") %>"
      required />

    <label for="name">Name:</label>
    <input
      type="text"
      id="name"
      name="Name"
      value="<%= airport.get("Name") %>"
      required />

    <label for="city">City:</label>
    <input
      type="text"
      id="city"
      name="City"
      value="<%= airport.get("City") %>"
      required />

    <label for="country">Country:</label>
    <input
      type="text"
      id="country"
      name="Country"
      value="<%= airport.get("Country") %>"
      required />

    <button
  type="submit"
  style="
    background-color: #007bff;
    color: #fff;
    padding: 0.5em 1em;
    font-size: 1rem;
    border: none;
    border-radius: 4px;
    cursor: pointer;
  ">
  Save Changes
</button>

    <a href="<%= request.getContextPath() %>/CustRep" class="btn-cancel">Cancel</a>
  </form>
</body>
</html>
