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
						<th>Identifier Code</th>
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
						<td><%=airport.get("AirportID")%></td>
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
				</select><br /> <br />

				<!-- Flight Number -->
				<label for="flightNumber">Flight Number</label><br /> <input
					type="number" id="flightNumber" name="FlightNumber" required
					min="1" step="1" /><br /> <br />

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
				</select><br /> <br />

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
				</select><br /> <br />

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


		<!-- Create Aircraft Form -->
		<section>
			<h2>Create Aircraft</h2>
			<form id="createAircraftForm"
				action="<%=request.getContextPath()%>/createAircraft" method="post"
				onsubmit="return validateAircraftSeats();">

				<!-- Airline selector -->
				<label for="aircAirline">Airline</label><br /> <select
					id="aircAirline" name="AirlineID" required size="5"
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

				<!-- Model -->
				<label for="aircModel">Model</label><br /> <input type="text"
					id="aircModel" name="Model" required maxlength="20" /><br />
				<br />

				<!-- Total Seats -->
				<label for="aircTotalSeats">Total Seats</label><br /> <input
					type="number" id="aircTotalSeats" name="TotalSeats" required
					min="1" /><br />
				<br />

				<!-- Class Configurations -->
				<fieldset>
					<legend>Class Configuration</legend>

					<label for="aircEco">Economy Seats</label><br /> <input
						type="number" id="aircEco" name="EconomySeats" required min="0" /><br />
					<br /> <label for="aircBiz">Business Seats</label><br /> <input
						type="number" id="aircBiz" name="BusinessSeats" required min="0" /><br />
					<br /> <label for="aircFirst">First Class Seats</label><br /> <input
						type="number" id="aircFirst" name="FirstClassSeats" required
						min="0" /><br />
					<br />
				</fieldset>

				<button type="submit">Create Aircraft</button>
				<h1 id="aircraftError" style="color: red; display: none;"></h1>
			</form>
		</section>

		<!-- Create Airport Form -->
		<section>
			<h2>Create Airport</h2>
			<form id="createAirportForm"
				action="<%=request.getContextPath()%>/createAirport" method="post"
				onsubmit="return validateAirportID();">

				<!-- AirportID -->
				<label for="apID">Airport ID</label><br /> <input type="text"
					id="apID" name="AirportID" required pattern="[A-Za-z0-9]{3}"
					maxlength="3" title="Exactly 3 letters or numbers" /><br />
				<br />

				<!-- Name -->
				<label for="apName">Name</label><br /> <input type="text"
					id="apName" name="Name" required maxlength="150" /><br />
				<br />

				<!-- City -->
				<label for="apCity">City</label><br /> <input type="text"
					id="apCity" name="City" required maxlength="100" /><br />
				<br />

				<!-- Country -->
				<label for="apCountry">Country</label><br /> <input type="text"
					id="apCountry" name="Country" required maxlength="50" /><br />
				<br />

				<button type="submit">Create Airport</button>
				<h1 id="airportError" style="color: red; display: none;"></h1>
			</form>
		</section>

		<script>
// ---- Aircraft seats validation ----
function validateAircraftSeats() {
  const total = parseInt(document.getElementById('aircTotalSeats').value, 10) || 0;
  const eco   = parseInt(document.getElementById('aircEco').value,        10) || 0;
  const biz   = parseInt(document.getElementById('aircBiz').value,        10) || 0;
  const first = parseInt(document.getElementById('aircFirst').value,      10) || 0;
  const errorEl = document.getElementById('aircraftError');

  if (eco + biz + first !== total) {
    errorEl.textContent = 
      `Class seats (${eco + biz + first}) must equal Total Seats (${total}).`;
    errorEl.style.display = 'block';
    return false;
  }

  errorEl.style.display = 'none';
  return true;
}

// ---- AirportID format validation ----
function validateAirportID() {
  const id = document.getElementById('apID').value;
  const errorEl = document.getElementById('airportError');
  const valid = /^[A-Za-z0-9]{3}$/.test(id);

  if (!valid) {
    errorEl.textContent = 'AirportID must be exactly 3 letters or numbers.';
    errorEl.style.display = 'block';
    return false;
  }

  errorEl.style.display = 'none';
  return true;
}
</script>



	</main>
</body>
</html>
