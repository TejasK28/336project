<%@ page session="true" %>
<header class="site-header">
    <div class="container">
        <a href="<%= request.getContextPath() %>/Home" class="brand">Flight Home</a>

        <div class="auth-menu">
            <%
                String uname = (String) session.getAttribute("uname");
                if (uname != null) {
            %>
                <span>Hello, <%= uname %>!</span>
                <form action="<%= request.getContextPath() %>/Logout" method="post" style="display: inline;">
                    <button type="submit">Logout</button>
                </form>
            <%
                } else {
            %>
                <label for="roleSelect" class="visually-hidden">Log in as:</label>
                <select id="roleSelect" onchange="handleLogin(this.value)">
                    <option hidden selected>Select</option>
                    <option value="customer">Customer</option>
                    <option value="admin">Administrator</option>
                    <option value="rep">Customer Rep</option>
                </select>
            <%
                }
            %>
        </div>
    </div>
</header>

<script>
function handleLogin(role) {
    if (role === 'customer') {
        window.location.href = '<%= request.getContextPath() %>/CustomerLogin';
    } else if (role === 'admin') {
        window.location.href = '<%= request.getContextPath() %>/AdminLogin';
    } else if (role === 'rep') {
        window.location.href = '<%= request.getContextPath() %>/CustRepLogin';
    }
}
</script>
