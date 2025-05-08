<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Admin Login</title>
   <link rel="stylesheet" href="<%= request.getContextPath() %>/css/styles.css">  
</head>

<body>

    <!-- ---------- HEADER (imported from homepage) ---------- -->
  	<jsp:include page="header.jsp" /> 

    <!-- ---------- MAIN CONTENT ---------- -->
    <main class="main-content">
        <h1 class="page-title">Admin Login</h1>

        <form action="Login" method="post">
            <div class="form-group">
                <label for="uname">Username</label>
                <input type="text" id="uname" name="uname" autocomplete="username" required>
            </div>

            <div class="form-group">
                <label for="pwd">Password</label>
                <input type="password" id="pwd" name="password" autocomplete="current-password" required>
            </div>

            <div class="submit-row">
            <%
            	session.setAttribute("accType", "Admin");
            %>
                <input type="submit" name="login" value="Login">
            </div>

            <%-- Show the error banner only after a failed attempt --%>
            <%
                Boolean failed = (Boolean) session.getAttribute("failed");
                if (failed != null && failed) {
            %>
                    <div class="error-banner">
                        Incorrect username or password. Please try again!
                    </div>
            <%
                    session.removeAttribute("failed");   // clear flag for next visit
                }
            %>
        </form>
    </main>

</body>
</html>
