<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="java.util.List,java.util.Map"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Representative Portal</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/css/styles.css">
</head>
<body>
	<jsp:include page="header.jsp" />
    <main class="main-content">
        <h1 class="page-title">Customer Representative Dashboard</h1>

        <!-- Tables Grid -->
        <div class="tables-grid">
            <section class="table-section">
                <h2>Airports</h2>
                <% List<Map<String,Object>> airports = (List<Map<String,Object>>)request.getAttribute("airports"); %>
                <% if (airports!=null && !airports.isEmpty()) { %>
                <table>
                    <thead>
                        <tr>
                            <th>Name</th>
                            <th>Code</th>
                            <th>City</th>
                            <th>Country</th>
                            <th>Arrivals</th>
                            <th>Departures</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Map<String,Object> ap:airports) { %>
                        <tr>
                            <td><a href="<%=request.getContextPath()%>/CustRep/airport/<%=ap.get("AirportID")%>"><%=ap.get("Name")%></a></td>
                            <td><%=ap.get("AirportID")%></td>
                            <td><%=ap.get("City")%></td>
                            <td><%=ap.get("Country")%></td>
                            <td><%=ap.get("numArriving")%></td>
                            <td><%=ap.get("numDeparting")%></td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
                <% } else { %>
                <p>No airports found.</p>
                <% } %>
            </section>

            <section class="table-section">
                <h2>Airlines</h2>
                <% List<Map<String,Object>> airlines = (List<Map<String,Object>>)request.getAttribute("airlines"); %>
                <% if (airlines!=null && !airlines.isEmpty()) { %>
                <table>
                    <thead>
                        <tr>
                            <th>Name</th>
                            <th>Flights</th>
                            <th>Aircraft</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Map<String,Object> al:airlines) { %>
                        <tr>
                            <td><%=al.get("Name")%></td>
                            <td><%=al.get("numSchedFlights")%></td>
                            <td><%=al.get("numOwnedAircrafts")%></td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
                <% } else { %>
                <p>No airlines found.</p>
                <% } %>
            </section>
        </div>

        <!-- Forms Grid -->
        <div class="forms-grid">
            <!-- Flight Form -->
            <form class="full-form" action="<%=request.getContextPath()%>/createFlight" method="post">
                <h2>Create Flight</h2>
                <div class="form-group">
                    <label for="airline">Airline</label>
                    <select id="airline" name="AirlineID" required size="5">
                        <% for (Map<String,Object> a:airlines) { %>
                        <option value="<%=a.get("AirlineID")%>"><%=a.get("Name")%></option>
                        <% } %>
                    </select>
                </div>
                <div class="form-group">
                    <label for="flightNumber">Flight Number</label>
                    <input type="number" id="flightNumber" name="FlightNumber" required min="1"/>
                </div>
                <div class="form-group">
                    <label for="fromAirport">From Airport</label>
                    <select id="fromAirport" name="FromAirportID" required>
                        <% for (Map<String,Object> ap:airports) { %>
                        <option value="<%=ap.get("AirportID")%>"><%=ap.get("AirportID")%> – <%=ap.get("Name")%></option>
                        <% } %>
                    </select>
                </div>
                <div class="form-group">
                    <label for="toAirport">To Airport</label>
                    <select id="toAirport" name="ToAirportID" required>
                        <% for (Map<String,Object> ap:airports) { %>
                        <option value="<%=ap.get("AirportID")%>"><%=ap.get("AirportID")%> – <%=ap.get("Name")%></option>
                        <% } %>
                    </select>
                </div>
                <div class="form-group">
                    <label for="departTime">Departure Time</label>
                    <input type="datetime-local" id="departTime" name="DepartTime" required/>
                </div>
                <div class="form-group">
                    <label for="arrivalTime">Arrival Time</label>
                    <input type="datetime-local" id="arrivalTime" name="ArrivalTime" required/>
                </div>
                <div class="form-group">
                    <label for="operatingDays">Operating Days</label>
                    <input type="text" id="operatingDays" name="OperatingDays" maxlength="10" pattern="[A-Za-z0-9,\- ]{1,10}"/>
                </div>
                <div class="submit-row">
                    <button type="submit" class="btn-green">Create Flight</button>
                </div>
                <% if (request.getAttribute("error") != null) { %>
                <div class="error-banner"><%=request.getAttribute("error")%></div>
                <% } %>
            </form>

            <!-- Aircraft Form -->
            <form class="full-form" id="createAircraftForm" action="<%=request.getContextPath()%>/createAircraft" method="post" onsubmit="return validateAircraftSeats()">
                <h2>Create Aircraft</h2>
                <div class="form-group">
                    <label for="aircAirline">Airline</label>
                    <select id="aircAirline" name="AirlineID" required size="5">
                        <% for (Map<String,Object> a:airlines) { %>
                        <option value="<%=a.get("AirlineID")%>"><%=a.get("Name")%></option>
                        <% } %>
                    </select>
                </div>
                <div class="form-group">
                    <label for="aircModel">Model</label>
                    <input type="text" id="aircModel" name="Model" required maxlength="20"/>
                </div>
                <div class="form-group">
                    <label for="aircTotalSeats">Total Seats</label>
                    <input type="number" id="aircTotalSeats" name="TotalSeats" required min="1"/>
                </div>
                <fieldset class="form-group">
                    <legend>Class Config</legend>
                    <label for="aircEco">Economy</label>
                    <input type="number" id="aircEco" name="EconomySeats" required min="0"/>
                    <label for="aircBiz">Business</label>
                    <input type="number" id="aircBiz" name="BusinessSeats" required min="0"/>
                    <label for="aircFirst">First Class</label>
                    <input type="number" id="aircFirst" name="FirstClassSeats" required min="0"/>
                </fieldset>
                <div class="submit-row">
                    <button type="submit" class="btn-green">Create Aircraft</button>
                </div>
                <div id="aircraftError" class="error-banner" style="display:none;"></div>
            </form>

            <!-- Airport Form -->
            <form class="full-form" id="createAirportForm" action="<%=request.getContextPath()%>/createAirport" method="post" onsubmit="return validateAirportID()">
                <h2>Create Airport</h2>
                <div class="form-group">
                    <label for="apID">Airport ID</label>
                    <input type="text" id="apID" name="AirportID" required pattern="[A-Za-z0-9]{3}" maxlength="3"/>
                </div>
                <div class="form-group">
                    <label for="apName">Name</label>
                    <input type="text" id="apName" name="Name" required maxlength="150"/>
                </div>
                <div class="form-group">
                    <label for="apCity">City</label>
                    <input type="text" id="apCity" name="City" required maxlength="100"/>
                </div>
                <div class="form-group">
                    <label for="apCountry">Country</label>
                    <input type="text" id="apCountry" name="Country" required maxlength="50"/>
                </div>
                <div class="submit-row">
                    <button type="submit" class="btn-green">Create Airport</button>
                </div>
                <div id="airportError" class="error-banner" style="display:none;"></div>
            </form>
        </div>
    </main>

    <script>
        // Aircraft validation
        function validateAircraftSeats() {
            const total = +document.getElementById('aircTotalSeats').value || 0;
            const eco   = +document.getElementById('aircEco').value        || 0;
            const biz   = +document.getElementById('aircBiz').value        || 0;
            const first = +document.getElementById('aircFirst').value      || 0;
            const el    = document.getElementById('aircraftError');
            if (eco + biz + first !== total) {
                el.textContent = `Class seats (${eco+biz+first}) must equal Total Seats (${total}).`;
                el.style.display = 'block'; return false;
            }
            el.style.display = 'none'; return true;
        }
        // Airport validation
        function validateAirportID() {
            const id = document.getElementById('apID').value;
            const el = document.getElementById('airportError');
            if (!/^[A-Za-z0-9]{3}$/.test(id)) {
                el.textContent = 'AirportID must be exactly 3 alphanumeric chars.';
                el.style.display = 'block'; return false;
            }
            el.style.display = 'none'; return true;
        }
    </script>
</body>
</html>
