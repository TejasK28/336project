<%@ page import="java.util.List,java.util.Map" %>
<%@ page pageEncoding="UTF-8"
          contentType="text/html; charset=UTF-8" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>My Questions</title>
</head>
<body>
  <jsp:include page="header.jsp" />

  <h1>My Questions</h1>
	<p><a href="${pageContext.request.contextPath}/Home">Back to Home</a></p>

  <%
    @SuppressWarnings("unchecked")
    List<Map<String,Object>> qs = (List<Map<String,Object>>)request.getAttribute("questions");
    if (qs == null || qs.isEmpty()) {
  %>
	<p>You haven't asked any questions yet.</p>
  <%
    } else {
  %>
    <table border="1" cellpadding="5">
      <tr><th>ID</th><th>When</th><th>Question</th><th>Answers</th></tr>
      
 
 <% for (Map<String,Object> q : qs) {
       int id = (Integer) q.get("QuestionID");
       Object ts = q.get("SubmitDateTime");
       String msg = (String) q.get("Message");
%>
  <tr>
    <td><%= id %></td>
    <td><%= ts %></td>
    <td><%= msg %></td>
    <td>
      <a href="${pageContext.request.contextPath}/ViewQuestion?questionId=<%= id %>">
        View / Reply
      </a>
    </td>
  </tr>
<% } %>
         </tbody>     
      
      
      
      
    </table>
  <% } %>
</body>
</html>
