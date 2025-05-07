<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
         import="java.util.Map" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Edit Employee <%= request.getParameter("username") %></title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/styles.css"> 
</head>
<body>
    <jsp:include page="header.jsp" />

    <main class="main-content">
        <h1>Edit Employee</h1>

        <%
            Map<String, Object> employee = (Map<String, Object>) request.getAttribute("employee");
            if (employee == null) {
        %>
            <p>No employee data found.</p>
        <%
            } else {
        %>

        <form class="full-form" action="<%= request.getContextPath() %>/EditEmployee" method="post">
            <div class="form-group">
                <label for="EmployeeID">Username</label>
                <input type="text" id="EmployeeID" name="username" value="<%= employee.get("EmployeeID") %>" readonly />
            </div>

            <div class="form-group">
                <label for="Password">Password</label>
                <input type="password" id="Password" name="Password" value="<%= employee.get("Password") %>" required />
            </div>

            <div class="form-group">
                <label for="FirstName">First Name</label>
                <input type="text" id="FirstName" name="FirstName" value="<%= employee.get("FirstName") %>" required />
            </div>

            <div class="form-group">
                <label for="LastName">Last Name</label>
                <input type="text" id="LastName" name="LastName" value="<%= employee.get("LastName") %>" required />
            </div>

            <div class="form-group">
                <label for="Email">Email</label>
                <input type="text" id="Email" name="Email" value="<%= employee.get("Email") %>" required />
            </div>

            <div class="form-group">
                <label>
                    <input type="checkbox" name="isAdmin" <%= Boolean.TRUE.equals(employee.get("isAdmin")) ? "checked" : "" %> />
                    Is Admin
                </label>
            </div>

            <div class="form-group">
                <label>
                    <input type="checkbox" name="isCustomerRepresentative" <%= Boolean.TRUE.equals(employee.get("isCustomerRepresentative")) ? "checked" : "" %> />
                    Is Customer Representative
                </label>
            </div>

            <div class="submit-row">
                <input type="submit" value="Save Changes" class="btn-green" />
            </div>
        </form>

        <% } %>
    </main>
</body>
</html>
