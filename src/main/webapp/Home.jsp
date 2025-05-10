<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Flight Home</title>

    <!-- ===== BASIC STYLES ===== -->
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/styles.css"> 
</head>
<body>

<!-- ========== HEADER ========== -->
<jsp:include page="header.jsp"></jsp:include>

<!-- ===== HERO / MAIN CONTENT PLACEHOLDER ===== -->

<% String uname = (String) session.getAttribute("uname");
if (uname == null) { %>
<section class="hero">
    <h1>Book Your Next Flight</h1>
    <p>Fast • Easy • Secure</p>
</section>
<% } else { %>
<h1>Welcome, <%= session.getAttribute("uname") %></h1>

<!-- This section focuses on the first 6 bullet points of user functionality -->




<% } %>

<!-- ===== SCRIPTS ===== -->
</body>
</html>
