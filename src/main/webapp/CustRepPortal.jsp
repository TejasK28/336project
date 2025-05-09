<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" import="java.util.List,java.util.Map"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Representative Portal</title>
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/css/styles.css">
</head>
<body>
	<!-- HEADER -->
	<jsp:include page="header.jsp" />

	<!-- store contextPath for client-side use -->
	<span id="contextPath" style="display: none"><%=request.getContextPath()%></span>

	<main class="main-content">

		<!-- Airports Table -->
		<section>
			<h2>Airports</h2>
			<%
			List<Map<String, Object>> airports = (List<Map<String, Object>>) request.getAttribute("airports");
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
					for (Map<String, Object> airport : airports) {
					%>
					<tr>
						<td><a
							href="<%=request.getContextPath() + "/CustRep/airport/" + airport.get("AirportID")%>">
								<%=airport.get("Name")%>
						</a></td>
						<td><%=airport.get("City")%></td>
						<td><%=airport.get("Country")%></td>
						<td><%=airport.get("numArriving")%></td>
						<td><%=airport.get("numDeparting")%></td>
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
			List<Map<String, Object>> airlines = (List<Map<String, Object>>) request.getAttribute("airlines");
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
					for (Map<String, Object> airline : airlines) {
					%>
					<tr>
						<td><a
							href="<%=request.getContextPath() + request.getServletPath() + "/" + airline.get("AirlineID")%> ">
								<%=airline.get("Name")%>
						</a></td>
						<td><%=airline.get("numSchedFlights")%></td>
						<td><%=airline.get("numOwnedAircrafts")%></td>
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

		<section>
			<h2>All Flights</h2>
			<%
			List<Map<String, Object>> flights = (List<Map<String, Object>>) request.getAttribute("flights");
			if (flights != null && !flights.isEmpty()) {
			%>
			<table>
				<thead>
					<tr>
						<th>Flight Number</th>
						<th>Airline</th>
						<th>Aircraft</th>
						<th>Capacity</th>
						<th>Seats Available</th>
						<th>Departing From</th>
						<th>Arriving At</th>
						<th>Departure Time</th>
						<th>Arrival Time</th>
						<th>Operating Days</th>
					</tr>
				</thead>
				<tbody>
					<%
					for (Map<String, Object> flight : flights) {
					%>
					<tr>
						<td><%=flight.get("FlightNumber")%></td>
						<td><%=flight.get("AirlineID")%></td>
						<td><%=flight.get("AircraftID")%></td>
						<td><%=flight.get("capacity")%></td>
						<td><%=flight.get("seatsAvailable")%></td>
						<td><%=flight.get("FromAirportID")%></td>
						<td><%=flight.get("ToAirportID")%></td>
						<td><%=flight.get("DepartTime")%></td>
						<td><%=flight.get("ArrivalTime")%></td>
						<td><%=flight.get("OperatingDays")%></td>
					</tr>
					<%
					}
					%>
				</tbody>
			</table>
			<%
			} else {
			%>
			<p>No flights found.</p>
			<%
			}
			%>
		</section>

		<section>
			<h2>Create Flight</h2>
			<form action="<%=request.getContextPath()%>/createFlight"
				method="post">

				<!-- Airline selector -->
				<label for="airline">Airline</label><br /> <select id="airline"
					name="AirlineID" required size="5"
					style="width: 100%; max-width: 300px;">
					<%
					if (airlines != null) {
						for (Map<String, Object> a : airlines) {
					%>
					<option value="<%=a.get("AirlineID")%>">
						<%=a.get("Name")%>
					</option>
					<%
					}
					}
					%>
				</select><br />
				<br />

				<!-- Flight Number -->
				<label for="flightNumber">Flight Number</label><br /> <input
					type="number" id="flightNumber" name="FlightNumber" required
					min="1" step="1" /><br />
				<br />

				<!-- Departing From -->
				<label for="fromAirport">Departing From Airport</label><br /> <select
					id="fromAirport" name="FromAirportID" required>
					<%
					for (Map<String, Object> airport : airports) {
					%>
					<option value="<%=airport.get("AirportID")%>">
						<%=airport.get("AirportID")%> –
						<%=airport.get("Name")%>
					</option>
					<%
					}
					%>
				</select><br />
				<br />

				<!-- Arriving At -->
				<label for="toAirport">Arriving At Airport</label><br /> <select
					id="toAirport" name="ToAirportID" required>
					<%
					for (Map<String, Object> airport : airports) {
					%>
					<option value="<%=airport.get("AirportID")%>">
						<%=airport.get("AirportID")%> –
						<%=airport.get("Name")%>
					</option>
					<%
					}
					%>
				</select><br />
				<br />

				<!-- Times -->
				<label for="departTime">Departure Time</label><br /> <input
					type="datetime-local" id="departTime" name="DepartTime" required /><br />
				<br /> <label for="arrivalTime">Arrival Time</label><br /> <input
					type="datetime-local" id="arrivalTime" name="ArrivalTime" required /><br />
				<br />

				<!-- Operating Days -->
				<label for="operatingDays">Operating Days</label><br /> <input
					type="text" id="operatingDays" name="OperatingDays" maxlength="10"
					pattern="[A-Za-z0-9,\\- ]{1,10}"
					title="Up to 10 letters, numbers, commas, hyphens or spaces" /><br />
				<br />

				<button type="submit">Create Flight</button>

				<%
				if (request.getAttribute("error") != null) {
				%>
				<h1 style="color: red;"><%=request.getAttribute("error")%></h1>
				<%
				}
				%>

			</form>

			<script>
    // Ensure Departing and Arriving airports are not the same
    const fromSelect = document.getElementById('fromAirport');
    const toSelect   = document.getElementById('toAirport');

    function syncOptions() {
      const fromVal = fromSelect.value;
      Array.from(toSelect.options).forEach(opt => {
        opt.disabled = (opt.value === fromVal);
      });
    }

    fromSelect.addEventListener('change', syncOptions);
    // initialize on page load
    syncOptions();
  </script>
		</section>


	</main>
</body>
</html>
