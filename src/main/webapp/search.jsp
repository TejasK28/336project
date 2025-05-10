<%@ page language="java"
         contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"
         import="java.util.List,java.util.Map" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>Search Flights</title>
  <script>
    // on page load and whenever tripType changes, toggle the return-date row
    function toggleReturnDate() {
      const tripType = document.getElementById('tripType').value;
      const returnRow = document.getElementById('returnRow');
      if (tripType === 'roundtrip') {
        returnRow.style.display = 'block';
      } else {
        returnRow.style.display = 'none';
        document.getElementById('returnDate').value = '';
      }
    }
    window.addEventListener('DOMContentLoaded', () => {
      // hide/show on initial load
      toggleReturnDate();
      // attach listener
      document.getElementById('tripType')
              .addEventListener('change', toggleReturnDate);
    });
  </script>
</head>
<body>
  <h1>Search Flights</h1>
  <form action="<%= request.getContextPath() %>/SearchFlightsServlet" method="post">

    <!-- FROM -->
    <p>
      <label for="fromAirport">From:</label>
      <select id="fromAirport" name="fromAirport" required>
        <%
          @SuppressWarnings("unchecked")
          List<Map<String,Object>> airports =
            (List<Map<String,Object>>)request.getAttribute("airports");
          for (Map<String,Object> a : airports) {
        %>
          <option value="<%= a.get("AirportID") %>">
            <%= a.get("AirportID") %>
          </option>
        <%
          }
        %>
      </select>
    </p>

    <!-- TO -->
    <p>
      <label for="toAirport">To:</label>
      <select id="toAirport" name="toAirport" required>
        <% for (Map<String,Object> a : airports) { %>
          <option value="<%= a.get("AirportID") %>">
            <%= a.get("AirportID") %>
          </option>
        <% } %>
      </select>
    </p>

    <!-- DEPART DATE -->
    <p>
      <label for="departDate">Depart Date:</label>
      <select id="departDate" name="departDate" required>
        <%
          @SuppressWarnings("unchecked")
          List<Map<String,Object>> dates =
            (List<Map<String,Object>>)request.getAttribute("dates");
          for (Map<String,Object> d : dates) {
        %>
          <option value="<%= d.get("departDate") %>">
            <%= d.get("departDate") %>
          </option>
        <%
          }
        %>
      </select>
    </p>

    <!-- RETURN DATE (hidden by default) -->
    <p id="returnRow" style="display:none;">
      <label for="returnDate">Return Date:</label>
      <select id="returnDate" name="returnDate">
        <option value="">-- none --</option>
        <% for (Map<String,Object> d : dates) { %>
          <option value="<%= d.get("departDate") %>">
            <%= d.get("departDate") %>
          </option>
        <% } %>
      </select>
    </p>

    <!-- TRIP TYPE -->
    <p>
      <label for="tripType">Trip Type:</label>
      <select id="tripType" name="tripType">
        <option value="oneway">One-way</option>
        <option value="roundtrip">Round-trip</option>
      </select>
    </p>

    <!-- FLEXIBLE -->
    <p>
      <label for="flexible">Flexible ±3 days?</label>
      <select id="flexible" name="flexible">
        <option value="false">No</option>
        <option value="true">Yes</option>
      </select>
    </p>

    <!-- SORT BY -->
    <p>
      <label for="sortBy">Sort by:</label>
      <select id="sortBy" name="sortBy">
        <option value="price">Price</option>
        <option value="f.DepartTime">Departure Time</option>
        <option value="f.ArrivalTime">Arrival Time</option>
        <option value="duration">Duration</option>
      </select>
    </p>

    <p>
      <button type="submit">Search</button>
    </p>
  </form>
</body>
</html>
