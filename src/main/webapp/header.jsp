<%@ page session="true" %>
<header class="site-header">
    <div class="container">
        <a href="<%= request.getContextPath() %>/Home" class="brand">Flight Home</a>

        <div class="auth-menu">
            <%
                String uname   = (String) session.getAttribute("uname");
                String accType = (String) session.getAttribute("accType");
                if (uname != null) {
            %>
                <span>Hello, <%= uname %>!</span>

                <% if ("Admin".equals(accType)) { %>
                    <a href="<%= request.getContextPath() %>/Admin">Portal Home</a>
                <% } else if ("CustRep".equals(accType)) { %>
                    <a href="<%= request.getContextPath() %>/CustRep">Portal Home</a>
                <% } %>

                <form action="<%= request.getContextPath() %>/Logout"
                      method="post"
                      style="display:inline;">
                    <button type="submit">Logout</button>
                </form>
            <%
                } else {
            %>
                <label for="roleSelect" class="visually-hidden">Log in as:</label>
                <select id="roleSelect" onchange="handleLogin(this.value)">
                    <option hidden selected>Select role</option>
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
    var ctx = '<%= request.getContextPath() %>';
    if (role === 'customer') {
        window.location.href = ctx + '/CustomerLogin';
    } else if (role === 'admin') {
        window.location.href = ctx + '/AdminLogin';
    } else if (role === 'rep') {
        window.location.href = ctx + '/CustRepLogin';
    }
}
</script>
