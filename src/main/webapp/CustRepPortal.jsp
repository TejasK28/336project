<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="java.util.List,java.util.Map" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Representative Portal</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/styles.css">
</head>
<body>
    <!-- HEADER -->
    <jsp:include page="header.jsp" />

    <!-- store contextPath for client-side use -->
    <span id="contextPath" style="display: none"><%= request.getContextPath() %></span>

    <main class="main-content">

        <!-- Airports Table -->
        <section>
            <h2>Airports</h2>
            <%
                List<Map<String,Object>> airports =
                    (List<Map<String,Object>>) request.getAttribute("airports");
                if (airports != null && !airports.isEmpty()) {
            %>
            <table>
                <thead>
                    <tr>
                        <th>Airport Name</th>
                        <th>City</th>
                        <th>Country</th>
                        <th># of Flights Arriving</th>
                        <th># of Flights Departing</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        for (Map<String,Object> airport : airports) {
                    %>
                    <tr>
                        <td>
                            <a href="<%= request.getContextPath() + "/" + airport.get("AirportID") %>">
                                <%= airport.get("Name") %>
                            </a>
                        </td>
                        <td><%= airport.get("City") %></td>
                        <td><%= airport.get("Country") %></td>
                        <td><%= airport.get("numArriving") %></td>
                        <td><%= airport.get("numDeparting") %></td>
                    </tr>
                    <%
                        }
                    %>
                </tbody>
            </table>
            <%
                } else {
            %>
            <p>No airports found.</p>
            <%
                }
            %>
        </section>

        <!-- Airlines Table -->
        <section>
            <h2>Airlines</h2>
            <%
                List<Map<String,Object>> airlines =
                    (List<Map<String,Object>>) request.getAttribute("airlines");
                if (airlines != null && !airlines.isEmpty()) {
            %>
            <table>
                <thead>
                    <tr>
                        <th>Airline Name</th>
                        <th># of Flights Scheduled</th>
                        <th># of Aircraft Owned</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        for (Map<String,Object> airline : airlines) {
                    %>
                    <tr>
                        <td>
                            <a href="">
                                <%= airline.get("Name") %>
                            </a>
                        </td>
                        <td><%= airline.get("numSchedFlights") %></td>
                        <td><%= airline.get("numOwnedAircrafts") %></td>
                    </tr>
                    <%
                        }
                    %>
                </tbody>
            </table>
            <%
                } else {
            %>
            <p>No airlines found.</p>
            <%
                }
            %>
        </section>

    </main>
</body>
</html>
