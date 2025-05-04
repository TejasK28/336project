<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Flight Home</title>

    <!-- ===== BASIC STYLES ===== -->
    <style>
        /* --- RESET / UTILS --- */
        * { box-sizing: border-box; margin: 0; padding: 0; }

        /* --- LAYOUT CONTAINER --- */
        .container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 0 1rem;
        }

        /* --- HEADER --- */
        header {
            background: #0c1c3d;      /* navy backdrop */
            color: #fff;
        }
        .header-inner {
            display: flex;
            justify-content: flex-end;   /* push everything to the right */
            align-items: center;
            padding: 0.75rem 0;
            gap: 1rem;                  /* space between brand & dropdown */
        }

        /* brand (site name) */
        .brand {
            font-size: 1.5rem;
            font-weight: 600;
            text-decoration: none;
            color: inherit;
            white-space: nowrap;
        }

        /* dropdown */
        #roleSelect {
            padding: 0.45rem 0.6rem;
            border-radius: 4px;
            border: none;
            font-size: 1rem;
        }

        /* --- HERO PLACEHOLDER --- */
        .hero {
            background: url('images/hero.jpg') center/cover no-repeat;
            min-height: 60vh;
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            color: #fff;
            text-shadow: 0 2px 6px rgba(0,0,0,.5);
        }
        .hero h1 {
            font-size: 3rem;
            margin-bottom: 0.5rem;
        }
        .hero p {
            font-size: 1.25rem;
        }
    </style>
</head>
<body>

<!-- ========== HEADER ========== -->
<header>
    <div class="container">
        <div class="header-inner">
            <!-- Website name -->
            <a href="home.jsp" class="brand">Flight Home</a>

            <!-- Login-as dropdown -->
            <label for="roleSelect" class="visually-hidden">Log in as: </label>
            <select id="roleSelect" onchange="handleLogin(this.value)">
                <option hidden selected>Select</option>
                <option value="customer">Customer</option>
                <option value="admin">Administrator</option>
                <option value="rep">Customer Rep</option>
            </select>
        </div>
    </div>
</header>

<!-- ===== HERO / MAIN CONTENT PLACEHOLDER ===== -->
<section class="hero">
    <h1>Book Your Next Flight</h1>
    <p>Fast • Easy • Secure</p>
</section>

<!-- ===== SCRIPTS ===== -->
<script>
    /**
     * Simple role-based redirect.
     * Adapt the target URLs to your own controllers / servlets.
     */
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
