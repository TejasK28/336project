<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Create Account</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/styles.css"> 
</head>
<body>

    <!-- Include shared header -->
    <jsp:include page="header.jsp" />

    <main class="main-content">
        <h1 class="page-title">Create New Account</h1>

        <form class="full-form" action="<%= request.getContextPath() %>/CreateEmployee" method="post">
            <div class="form-group">
                <label for="EmployeeID">Username</label>
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
                <input type="text" id="Email" name="Email" required />
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
                <input type="submit" value="Create Account" class="btn-green" />
            </div>
        </form>
    </main>
</body>
</html>
