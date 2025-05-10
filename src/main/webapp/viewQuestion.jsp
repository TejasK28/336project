<%@page import="java.util.Map,java.util.List"%>
<jsp:include page="header.jsp"/>
<%
  Map<String,Object> q = (Map)request.getAttribute("question");
  List<Map<String,Object>> answers = (List)request.getAttribute("answers");
%>

<h1>Question #<%=q.get("QuestionID")%></h1>
<p><strong>From:</strong> <%=q.get("FirstName")%> <%=q.get("LastName")%>
   <strong>At:</strong> <%=q.get("SubmitDateTime")%></p>
<blockquote><%=q.get("Message")%></blockquote>

<h2>Answers</h2>
<% if (answers.isEmpty()) { %>
  <p><em>No one has answered yet.</em></p>
<% } else { %>
  <ul>
  <% for (Map<String,Object> a : answers) { %>
    <li>
      <strong><%=a.get("FirstName")%> <%=a.get("LastName")%></strong>
      at <%=a.get("ResponseDateTime")%><br/>
      <%=a.get("Message")%>
    </li>
  <% } %>
  </ul>
<% } %>

<h3>Your Reply</h3>
<form action="${pageContext.request.contextPath}/PostAnswer" method="post">
  <input type="hidden" name="questionId" value="<%=q.get("QuestionID")%>"/>
  <input type="hidden" name="employeeId"  value="${sessionScope.uname}"/>
  <textarea name="message" rows="3" required></textarea><br/>
  <button type="submit">Send Answer</button>
</form>
