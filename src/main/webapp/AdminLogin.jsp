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
    <header class="site-header">
        <div class="container">
            <a href="Home.jsp" class="brand">Flight Home</a>

            <div class="auth-menu">
                <label for="roleSelect" class="visually-hidden">Log in as:</label>
                <select id="roleSelect" onchange="handleLogin(this.value)">
                    <option hidden selected>Select</option>
                    <option value="customer">Customer</option>
                    <option value="admin">Administrator</option>
                    <option value="rep">Customer Rep</option>
                </select>
            </div>
        </div>
    </header>

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

    <!-- ---------- ROLE DROPDOWN HANDLER ---------- -->
    <script>
        function handleLogin(role) {
            if (!role) return;
            const target = {
                customer: 'CustomerLogin.jsp',
                admin:    'AdminLogin.jsp',
                rep:      'RepLogin.jsp'
            }[role];
            if (target) window.location.href = target;
        }
    </script>

</body>
</html>
