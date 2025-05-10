<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"
         import="java.util.List, java.util.Map" %>
<jsp:include page="header.jsp"/>

<main class="main-content">
  <h1>All Customer Questions</h1>

  <%
    @SuppressWarnings("unchecked")
    List<Map<String,Object>> qs = (List<Map<String,Object>>) request.getAttribute("questions");
    if (qs == null || qs.isEmpty()) {
  %>
    <p>No questions to display.</p>
  <%
    } else {
  %>
    <table border="1" cellpadding="5" cellspacing="0">
      <thead>
        <tr>
          <th>ID</th>
          <th>Customer</th>
          <th>Submitted At</th>
          <th>Question</th>
          <th>Action</th>
        </tr>
      </thead>
      <tbody>
        <%
          for (Map<String,Object> q : qs) {
            int    id    = (Integer) q.get("QuestionID");
            String fname = (String)  q.get("FirstName");
            String lname = (String)  q.get("LastName");
            Object ts    =           q.get("SubmitDateTime");
            String msg   = (String)  q.get("Message");
        %>
        <tr>
          <td><%= id %></td>
          <td><%= fname %> <%= lname %></td>
          <td><%= ts %></td>
          <td><%= msg %></td>
          <td>
            <a href="${pageContext.request.contextPath}/ViewQuestion?questionId=<%= id %>">
  View / Answer
</a>

          </td>
        </tr>
        <% } %>
      </tbody>
    </table>
  <% } %>
</main>
