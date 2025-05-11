<%@ page import="java.util.List,java.util.Map" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>Waiting List for Flight <%= request.getAttribute("flightId") %></title>
  <link rel="stylesheet" href="<%=request.getContextPath()%>/css/styles.css">
</head>
<body>
  <jsp:include page="header.jsp"/>
  <main class="main-content">
    <h1>Waiting List for Flight <%= request.getAttribute("flightId") %></h1>
    <%
      List<Map<String,Object>> wait = 
         (List<Map<String,Object>>)request.getAttribute("waitlist");
      if (wait == null || wait.isEmpty()) {
    %>
      <p>No one is on the waiting list for this flight.</p>
    <%
      } else {
    %>
      <table>
        <thead>
          <tr>
            <th>Customer ID</th>
            <th>Name</th>
            <th>Class</th>
            <th>Requested At</th>
          </tr>
        </thead>
        <tbody>
        <%
          for (Map<String,Object> w : wait) {
        %>
          <tr>
            <td><%= w.get("CustomerID") %></td>
            <td><%= w.get("FirstName") %> <%= w.get("LastName") %></td>
            <td><%= w.get("Class") %></td>
            <td><%= w.get("RequestDateTime") %></td>
          </tr>
        <%
          }
        %>
        </tbody>
      </table>
    <%
      }
    %>
    <p><a href="<%=request.getContextPath()%>/CustRep">back to dashboard</a></p>
  </main>
</body>
</html>
