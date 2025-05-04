<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Customer Login</title>

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

        /* ---------- FORM ---------- */
        form {
            width: 320px;
            background: #fff;
            padding: 2rem 2.2rem;
            border-radius: 8px;
            box-shadow: 0 4px 12px rgba(0,0,0,.08);
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
        .submit-row { text-align: center; margin-top: 1.2rem; }
        input[type="submit"] {
            width: 60%;
            padding: 0.55rem 0;
            background: #4CAF50;
            border: none;
            color: #fff;
            font-size: 1rem;
            border-radius: 4px;
            cursor: pointer;
            transition: background-color .15s ease-in-out;
        }
        input[type="submit"]:hover { background: #45a049; }

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
        <h1 class="page-title">Customer Login</h1>

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
            	session.setAttribute("accType", "Customer");
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
