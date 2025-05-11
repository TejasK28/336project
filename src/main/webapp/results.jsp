<%@ page contentType="text/html;charset=UTF-8" language="java"
	import="java.util.*"%>
<%
String tripType = request.getParameter("tripType");

@SuppressWarnings("unchecked")
List<Map<String, Object>> outbound = (List<Map<String, Object>>) request.getAttribute("outbound");
@SuppressWarnings("unchecked")
List<Map<String, Object>> inbound = (List<Map<String, Object>>) request.getAttribute("inbound");

// Build filter-sets
Set<String> airlines = new TreeSet<>();
Set<String> takeoffTimes = new TreeSet<>();
Set<String> landingTimes = new TreeSet<>();
List<Map<String, Object>> allFlights = new ArrayList<>(outbound);
if (inbound != null)
	allFlights.addAll(inbound);

for (Map<String, Object> f : allFlights) {
	// airlineName
	Object aObj = f.get("airlineName");
	if (aObj != null)
		airlines.add(aObj.toString());

	// depart_time (HH:MM)
	Object dObj = f.get("depart_time");
	if (dObj != null) {
		String dep = dObj.toString().substring(11, 16);
		takeoffTimes.add(dep);
	}

	// arrival_time (HH:MM)
	Object rObj = f.get("arrival_time");
	if (rObj != null) {
		String arr = rObj.toString().substring(11, 16);
		landingTimes.add(arr);
	}
}
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8" />
<title>Flight Results</title>
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/css/styles.css" />
<script>
	/* your filterFlights JS unchanged… */
</script>
</head>
<body>
	<jsp:include page="header.jsp" />

	<h2>Filter Flights</h2>
	<form onsubmit="return false;">
		<!-- same filter inputs, using airlines/takeoffTimes/landingTimes -->
	</form>

	<%
	if ("roundtrip".equals(tripType)) {
	%>
	<!-- ROUND-TRIP: two side-by-side tables -->
	<form method="post"
		action="<%=request.getContextPath()%>/addRTFlightToPlan">
		<h2>Outbound</h2>
		<table border="1">
			<tr>
				<th></th>
				<th>Route</th>
				<th>Departs</th>
				<th>Arrives</th>
				<th>Duration</th>
				<th>Price</th>
				<th>Airline</th>
			</tr>
			<%
			for (Map<String, Object> f : outbound) {
				String leg1 = String.valueOf(f.get("first_leg_id"));
				boolean lay = f.get("second_leg_id") != null;
				String orig = String.valueOf(f.get("origin"));
				String stop = lay ? String.valueOf(f.get("stopover")) : null;
				String dest = String.valueOf(f.get("destination"));
				String d1 = String.valueOf(f.get("depart_time"));
				String a1 = String.valueOf(f.get("arrival_time"));
				int dur = ((Number) f.get("total_duration")).intValue();
				double pr = ((Number) f.get("total_fare")).doubleValue();
				String air = String.valueOf(f.get("airlineName"));
			%>
			<tr class="flight-row" data-price="<%=pr%>"
				data-airline="<%=air.toLowerCase()%>"
				data-takeoff="<%=d1.substring(11, 16)%>"
				data-landing="<%=a1.substring(11, 16)%>">
				<td><input type="radio" name="outboundFlight" value="<%=leg1%>"
					required /></td>
				<td><%=orig%> <%
 if (lay) {
 %>→<%=stop%>(stop)<%
 }
 %> →<%=dest%></td>
				<td><%=d1.substring(11, 16)%></td>
				<td><%=a1.substring(11, 16)%></td>
				<td><%=dur%> min</td>
				<td>$<%=pr%></td>
				<td><%=air%></td>
			</tr>
			<%
			}
			%>
		</table>

		<h2>Return</h2>
		<table border="1">
			<tr>
				<th></th>
				<th>Route</th>
				<th>Departs</th>
				<th>Arrives</th>
				<th>Duration</th>
				<th>Price</th>
				<th>Airline</th>
			</tr>
			<%
			for (Map<String, Object> f : inbound) {
				String leg1 = String.valueOf(f.get("first_leg_id"));
				boolean lay = f.get("second_leg_id") != null;
				String orig = String.valueOf(f.get("origin"));
				String stop = lay ? String.valueOf(f.get("stopover")) : null;
				String dest = String.valueOf(f.get("destination"));
				String d1 = String.valueOf(f.get("depart_time"));
				String a1 = String.valueOf(f.get("arrival_time"));
				int dur = ((Number) f.get("total_duration")).intValue();
				double pr = ((Number) f.get("total_fare")).doubleValue();
				String air = String.valueOf(f.get("airlineName"));
			%>
			<tr class="flight-row" data-price="<%=pr%>"
				data-airline="<%=air.toLowerCase()%>"
				data-takeoff="<%=d1.substring(11, 16)%>"
				data-landing="<%=a1.substring(11, 16)%>">
				<td><input type="radio" name="inboundFlight" value="<%=leg1%>"
					required /></td>
				<td><%=orig%> <%
 if (lay) {
 %>→<%=stop%>(stop)<%
 }
 %> →<%=dest%></td>
				<td><%=d1.substring(11, 16)%></td>
				<td><%=a1.substring(11, 16)%></td>
				<td><%=dur%> min</td>
				<td>$<%=pr%></td>
				<td><%=air%></td>
			</tr>
			<%
			}
			%>
		</table>

		<button type="submit">Add Round-Trip to Plan</button>
	</form>

	<%
	} else {
	%>
	<!-- ONE-WAY (and one‐stop) -->
	<h2>Available Flights</h2>
	<table border="1">
		<tr>
			<th>Route</th>
			<th>Departs</th>
			<th>Arrives</th>
			<th>Duration</th>
			<th>Price</th>
			<th>Airline</th>
			<th>Action</th>
		</tr>
		<%
		for (Map<String, Object> f : outbound) {
			String leg1 = String.valueOf(f.get("first_leg_id"));
			String leg2 = f.get("second_leg_id") != null ? f.get("second_leg_id").toString() : null;
			boolean lay = leg2 != null;
			String orig = String.valueOf(f.get("origin"));
			String stop = lay ? String.valueOf(f.get("stopover")) : null;
			String dest = String.valueOf(f.get("destination"));
			String d1 = String.valueOf(f.get("depart_time"));
			String a1 = String.valueOf(f.get("arrival_time"));
			int dur = ((Number) f.get("total_duration")).intValue();
			double pr = ((Number) f.get("total_fare")).doubleValue();
			String air = String.valueOf(f.get("airlineName"));
			String action = lay ? "/addLayFlightToPlan" : "/addOWFlightToPlan";
		%>
		<tr class="flight-row" data-price="<%=pr%>"
			data-airline="<%=air.toLowerCase()%>"
			data-takeoff="<%=d1.substring(11, 16)%>"
			data-landing="<%=a1.substring(11, 16)%>">
			<td>
				<form method="post" action="<%=request.getContextPath() + action%>">
					<input type="hidden" name="leg1ID" value="<%=leg1%>" />
					<%
					if (lay) {
					%>
					<input type="hidden" name="leg2ID" value="<%=leg2%>" />
					<%
					}
					%>
					<button type="submit">Add to Plan</button>
				</form>
			</td>
			<td><%=orig%> <%
 if (lay) {
 %>→<%=stop%>(stop)<%
 }
 %> →<%=dest%></td>
			<td><%=d1.substring(11, 16)%></td>
			<td><%=a1.substring(11, 16)%></td>
			<td><%=dur%> min</td>
			<td>$<%=pr%></td>
			<td><%=air%></td>
		</tr>
		<%
		}
		%>
	</table>
	<%
	}
	%>
</body>
</html>
