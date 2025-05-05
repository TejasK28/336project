<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ page import="java.util.List, java.util.Map" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Admin Login</title>

    <style>
    /* ---------- RESET ---------- */
    *, *::before, *::after { box-sizing: border-box; }

    /* ---------- HEADER ---------- */
    .site-header {
        background: #0c1c3d;
        color: #fff;
    }
    .site-header .container {
        max-width: 1200px;
        margin: 0 auto;
        padding: 0.75rem 1rem;
        display: flex;
        align-items: center;
        justify-content: space-between;
    }
    .brand {
        font-size: 1.5rem;
        font-weight: 600;
        text-decoration: none;
        color: inherit;
        white-space: nowrap;
    }
    .auth-menu select {
        padding: 0.45rem 0.6rem;
        border-radius: 4px;
        border: none;
        font-size: 1rem;
    }
    .visually-hidden {
        position: absolute;
        width: 1px;
        height: 1px;
        overflow: hidden;
        clip: rect(0 0 0 0);
        white-space: nowrap;
        border: 0;
        padding: 0;
        margin: -1px;
    }

    /* ---------- PAGE LAYOUT ---------- */
    body {
        font-family: Arial, sans-serif;
        background-color: #f2f2f2;
        display: flex;
        flex-direction: column;
        min-height: 100vh;
        margin: 0;
    }
    .main-content {
        flex: 1;
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        padding: 2rem 1rem;
    }
    h1.page-title {
        margin-bottom: 1.25rem;
        font-size: 2rem;
    }

    /* ---------- FULL FORM STYLING (login/register forms only) ---------- */
    form.full-form {
        width: 320px;
        background: #fff;
        padding: 2rem 2.2rem;
        border-radius: 8px;
        box-shadow: 0 4px 12px rgba(0,0,0,.08);
    }

    /* ---------- INLINE FORM RESET (edit/delete) ---------- */
    form.inline-button {
        all: unset;
        display: inline;
    }

    .form-group { margin-bottom: 1.1rem; }
    label {
        display: block;
        margin-bottom: 0.35rem;
        font-weight: 600;
    }

    input[type="text"],
    input[type="password"] {
        width: 100%;
        padding: 0.55rem 0.6rem;
        border: 1px solid #ccc;
        border-radius: 4px;
        font-size: 0.95rem;
    }

    /* ---------- SUBMIT ---------- */
    .submit-row {
        text-align: center;
        margin-top: 1.2rem;
    }

    input[type="submit"],
    button[type="submit"] {
        padding: 0.5rem 1rem;
        border: none;
        color: #fff;
        font-size: 0.95rem;
        border-radius: 4px;
        cursor: pointer;
        transition: background-color 0.15s ease-in-out;
    }

    .btn-green {
        background-color: #4CAF50;
    }
    .btn-green:hover {
        background-color: #45a049;
    }

    .btn-red {
        background-color: #e02127;
    }
    .btn-red:hover {
        background-color: #c1121c;
    }

    /* ---------- ERROR ---------- */
    .error-banner {
        margin-top: 1rem;
        padding: 0.9rem 1rem;
        background: #e02127;
        color: #fff;
        border-radius: 4px;
        text-align: center;
        font-weight: 600;
    }
</style>

</head>

<body>

    <!-- ---------- HEADER (imported from homepage) ---------- -->
    <header class="site-header">
        <div class="container">
            <a href="Home.jsp" class="brand">Flight Home</a>

            <div class="auth-menu">
                <label for="roleSelect" >Hello, <%= (String) session.getAttribute("uname") %>!</label>
                <form action="${pageContext.request.contextPath}/Logout" method="post">
					<button type="submit">Logout</button>
				</form>
            </div>
        </div>
    </header>
    
    <!-- Manage Employees Table -->
    <h1>Manage Employees</h1>
    
   	<%
   		List<Map<String, Object>> employees = (List<Map<String, Object>>) request.getAttribute("employees");
   	%> 
   	<p>Employees: <%= employees.size() %></p>
   	
   	<%@ page import="java.util.List, java.util.Map" %>

<table border="1" cellpadding="8" cellspacing="0">
    <thead>
        <tr>
            <th>Username</th>
            <th>First Name</th>
            <th>Last Name</th>
            <th>Email</th>
            <th>Is Admin</th>
            <th>Is Customer Representative</th>
            <th>Edit</th>
            <th>Delete</th>
        </tr>
    </thead>
    <tbody>
    <%
        if (employees != null) {
            for (Map<String, Object> emp : employees) {
    %>
        <tr>
            <td><%= emp.get("EmployeeID") %></td>
            <td><%= emp.get("FirstName") %></td>
            <td><%= emp.get("LastName") %></td>
            <td><%= emp.get("Email") %></td>
            <td>
                <input type="checkbox" disabled <%= Boolean.TRUE.equals(emp.get("isAdmin")) ? "checked" : "" %> />
            </td>
            <td>
                <input type="checkbox" disabled <%= Boolean.TRUE.equals(emp.get("isCustomerRepresentative")) ? "checked" : "" %> />
            </td>
            <td>
                <form action="<%= request.getContextPath() %>/EditEmployee" method="get" style="margin: 0;">
                    <input type="hidden" name="username" value="<%= emp.get("EmployeeID") %>" />
                    <button type="submit" style="background-color: green; color: white;">Edit</button>
                </form>
            </td>
            <td>
                <form action="<%= request.getContextPath() %>/DeleteEmployee" method="post" style="margin: 0;">
                    <input type="hidden" name="username" value="<%= emp.get("EmployeeID") %>" />
                    <button type="submit" style="background-color: red; color: white;" onclick="return confirm('Are you sure you want to delete this employee?');">
                        Delete
                    </button>
                </form>
            </td>
        </tr>
    <%
            }
        } else {
    %>
        <tr><td colspan="8">No employees found.</td></tr>
    <%
        }
    %>
    </tbody>
</table>


<h1>Create New Employee</h1>

<form class="full-form" action="CreateEmployee" method="post">
    <div class="form-group">
        <label for="EmployeeID">Username</label>
        <input type="text" id="EmployeeID" name="EmployeeID" required />
    </div>

    <div class="form-group">
        <label for="Password">Password</label>
        <input type="password" id="Password" name="Password" required />
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
        <label>
            <input type="checkbox" name="isAdmin" />
            Is Admin
        </label>
    </div>

    <div class="form-group">
        <label>
            <input type="checkbox" name="isCustomerRepresentative" />
            Is Customer Representative
        </label>
    </div>

    <div class="submit-row">
        <input type="submit" value="Create Employee" />
    </div>
</form>


   
</body>
</html>