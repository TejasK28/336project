<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"
         import="java.util.List,java.util.Map" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Admin Login</title>
   	<link rel="stylesheet" href="<%= request.getContextPath() %>/css/styles.css"> 
</head>

<body>
    <!-- HEADER -->
   	<jsp:include page="header.jsp"/> 
    <span id="contextPath" style="display:none"><%=request.getContextPath()%></span>

    <main class="main-content">
        <h1>Manage Employees</h1>
        <%
            List<Map<String, Object>> employees = (List<Map<String, Object>>) request.getAttribute("employees");
        %>
        <p>Employees: <%= employees != null ? employees.size() : 0 %></p>

        <table border="1" cellpadding="8" cellspacing="0">
            <thead>
                <tr>
                    <th>Username</th>
                    <th>First Name</th>
                    <th>Last Name</th>
                    <th>Email</th>
                    <th>Is Admin</th>
                    <th>Is Customer Representative</th>
                    <th>Actions</th>
                    <th>Delete</th>
                </tr>
            </thead>
            <tbody>
                <%
                if (employees != null) {
                    for (Map<String, Object> emp : employees) {
                %>
                <tr data-username="<%= emp.get("EmployeeID") %>">
                    <td><%= emp.get("EmployeeID") %></td>
                    <td class="editable" data-field="FirstName"><%= emp.get("FirstName") %></td>
                    <td class="editable" data-field="LastName"><%= emp.get("LastName") %></td>
                    <td class="editable" data-field="Email"><%= emp.get("Email") %></td>
                    <td>
                        <input type="checkbox" name="isAdmin" disabled <%= Boolean.TRUE.equals(emp.get("isAdmin")) ? "checked" : "" %> />
                    </td>
                    <td>
                        <input type="checkbox" name="isCustomerRepresentative" disabled <%= Boolean.TRUE.equals(emp.get("isCustomerRepresentative")) ? "checked" : "" %> />
                    </td>
                    <td>
                    	<form action="<%= request.getContextPath() %>/EditEmployee" method="get">
							<input type="hidden" name="username" value="<%= emp.get("EmployeeID") %>" />
							<button type="submit" class="edit-btn btn-green">Edit</button>
                    	</form>
                        <button class="cancel-btn btn-red" style="display:none; margin-left:5px;">Cancel</button>
                    </td>
                    <td>
                        <form action="<%= request.getContextPath() %>/DeleteEmployee" method="post" style="margin:0;">
                            <input type="hidden" name="username" value="<%= emp.get("EmployeeID") %>" />
                            <button type="submit" class="btn-red" onclick="return confirm('Are you sure you want to delete this employee?');">
                                Delete
                            </button>
                        </form>
                    </td>
                </tr>
                <%      }
                } else { %>
                    <tr><td colspan="8">No employees found.</td></tr>
                <% } %>
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
    </main>
<%-- 	<script src="<%= request.getContextPath() %>/js/adminPortal.js"></script> --%>
 </body>
</html>
