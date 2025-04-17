<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Welcome Page</title>
</head>
<body>

<%
    String uname = (String) session.getAttribute("uname");
    if (uname == null) {
        response.sendRedirect("login.jsp"); // Not logged in
        return;
    }
%>

<h2>Welcome, <%= uname %>!</h2>

<form action="Logout" method="post">
    <input type="submit" value="Logout">
</form>

</body>
</html>