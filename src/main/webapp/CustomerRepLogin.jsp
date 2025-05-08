<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Representative Login</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/styles.css"> 
</head>

<body>

    <!-- ---------- HEADER ---------- -->
    <jsp:include page="header.jsp" />

    <!-- ---------- MAIN CONTENT ---------- -->
    <main class="main-content">
        <h1 class="page-title">Representative Login</h1>

        <form class="full-form" action="Login" method="post">
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
                    // Tell the servlet this is a Representative login
                    session.setAttribute("accType", "CustRep");
                %>
                <input type="submit" name="login" value="Login" class="btn-green">
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
                    session.removeAttribute("failed");
                }
            %>
            <%
                Boolean noPerm = (Boolean) session.getAttribute("noPerm");
                if (noPerm != null && noPerm) {
            %>
                    <div class="error-banner">
                    	You do not have permission to visit this page!
                    </div>
            <%
                    session.removeAttribute("noPerm");   // clear flag for next visit
                }
            %>
        </form>
    </main>
</body>
</html>
