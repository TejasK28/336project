<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"
         import="java.util.List, java.util.Map" %>
<jsp:include page="header.jsp"/>

<main class="main-content">
  <h1>Search Questions</h1>

  <form action="${pageContext.request.contextPath}/SearchQuestions" method="get">
    <input type="text"
           name="q"
           value="${param.q}"
           placeholder="Enter keyword…"
           required/>
    <button type="submit">🔎 Search</button>
  </form>

  <%
    @SuppressWarnings("unchecked")
    List<Map<String,Object>> qs = (List<Map<String,Object>>)request.getAttribute("questions");
    String kw = (String) request.getAttribute("keyword");
  %>

  <h2>Results for “<%= kw %>”</h2>

  <c:choose>
    <c:when test="${empty qs}">
      <p><em>No questions found containing “${keyword}”.</em></p>
    </c:when>
    <c:otherwise>
      <table>
        <thead>
          <tr>
            <th>ID</th><th>Customer</th><th>When</th><th>Message</th><th>→</th>
          </tr>
        </thead>
        <tbody>
        <% for (Map<String,Object> q : qs) { %>
          <tr>
            <td><%= q.get("QuestionID") %></td>
            <td><%= q.get("FirstName") %> <%= q.get("LastName") %></td>
            <td><%= q.get("SubmitDateTime") %></td>
            <td><%= q.get("Message") %></td>
            <td>
              <a href="${pageContext.request.contextPath}/ViewQuestion?questionId=<%= q.get("QuestionID") %>">
				  View / Answer
				</a>

            </td>
          </tr>
        <% } %>
        </tbody>
      </table>
    </c:otherwise>
  </c:choose>
</main>
