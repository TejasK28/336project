<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
         import="java.util.Map" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Edit Customer <%= request.getParameter("customerId") %></title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/styles.css"> 
</head>
<body>
    <jsp:include page="header.jsp" />

    <main class="main-content">
        <h1>Edit Customer</h1>

        <%
            @SuppressWarnings("unchecked")
            Map<String, Object> customer = (Map<String, Object>) request.getAttribute("customer");
            if (customer == null) {
        %>
            <p>No customer data found.</p>
        <%
            } else {
        %>

        <form class="full-form" action="<%= request.getContextPath() %>/EditCustomer" method="post">
            <div class="form-group">
                <label for="CustomerID">Customer ID</label>
                <input type="text" id="CustomerID" name="customerId" 
                       value="<%= customer.get("CustomerID") %>" readonly />
            </div>

            <div class="form-group">
                <label for="Password">Password</label>
                <input type="password" id="Password" name="Password" 
                       value="<%= customer.get("Password") %>" required />
            </div>

            <div class="form-group">
                <label for="FirstName">First Name</label>
                <input type="text" id="FirstName" name="FirstName" 
                       value="<%= customer.get("FirstName") %>" required />
            </div>

            <div class="form-group">
                <label for="LastName">Last Name</label>
                <input type="text" id="LastName" name="LastName" 
                       value="<%= customer.get("LastName") %>" required />
            </div>

            <div class="form-group">
                <label for="Email">Email</label>
                <input type="email" id="Email" name="Email" 
                       value="<%= customer.get("Email") %>" required />
            </div>

            <div class="form-group">
                <label for="Phone">Phone</label>
                <input type="text" id="Phone" name="Phone" 
                       value="<%= customer.get("Phone") %>" required />
            </div>

            <div class="form-group">
                <label for="Address">Address</label>
                <input type="text" id="Address" name="Address" 
                       value="<%= customer.get("Address") %>" required />
            </div>

            <div class="submit-row">
                <input type="submit" value="Save Changes" class="btn-green" />
            </div>
        </form>

        <% } %>
    </main>
</body>
</html>
