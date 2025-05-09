<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%
    // capture the “did we come here from the admin portal?” flag
    String fromAdmin = request.getParameter("fromAdmin");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Create New Customer</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/styles.css"> 
</head>
<body>
    <jsp:include page="header.jsp" />

    <main class="main-content">
        <h1 class="page-title">Create New Customer</h1>

        <form class="full-form"
              action="<%= request.getContextPath() %>/CreateCustomer"
              method="post">
              
            <%-- if we came from AdminPortal, carry that flag through --%>
            <% if (fromAdmin != null) { %>
                <input type="hidden" name="fromAdmin" value="<%= fromAdmin %>" />
            <% } %>

            <div class="form-group">
                <label for="CustomerID">Customer ID</label>
                <input type="text" id="CustomerID" name="CustomerID" required />
            </div>

            <div class="form-group">
                <label for="FirstName">First Name</label>
                <input type="text" id="FirstName" name="FirstName" required />
            </div>

            <div class="form-group">
                <label for="LastName">Last Name</label>
                <input type="text" id="LastName" name="LastName" required />
            </div>

            <div class="form-group">
                <label for="Email">Email</label>
                <input type="email" id="Email" name="Email" required />
            </div>

            <div class="form-group">
                <label for="Password">Password</label>
                <input type="password" id="Password" name="Password" required />
            </div>

            <div class="form-group">
                <label for="Phone">Phone Number</label>
                <input type="text" id="Phone" name="Phone" required />
            </div>

            <div class="form-group">
                <label for="Address">Address</label>
                <input type="text" id="Address" name="Address" required />
            </div>

            <div class="submit-row">
                <input type="submit" value="Create Customer" class="btn-green" />
            </div>
        </form>

        <%-- offer a back link if admin came in here --%>
        <% if (fromAdmin != null) { %>
            <p style="margin-top:1em;">
                <a href="<%= request.getContextPath() %>/Admin"
                   class="btn btn-secondary">
                   ← Back to Admin Portal
                </a>
            </p>
        <% } %>
    </main>
</body>
</html>
