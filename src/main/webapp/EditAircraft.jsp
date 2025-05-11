<%@ page contentType="text/html; charset=UTF-8"
         import="java.util.Map, java.util.List" %>
<%
  @SuppressWarnings("unchecked")
  Map<String,Object> aircraft    = (Map<String,Object>) request.getAttribute("aircraft");
  List<Map<String,Object>> airlines = (List<Map<String,Object>>) request.getAttribute("airlines");

  // parse config string "E:xx,B:yy,F:zz"
  String cfg = String.valueOf(aircraft.get("Config"));
  int eco = 0, biz = 0, first = 0;
  if (cfg != null) {
    for (String part : cfg.split(",")) {
      String[] kv = part.split(":");
      switch (kv[0]) {
        case "E": eco   = Integer.parseInt(kv[1]); break;
        case "B": biz   = Integer.parseInt(kv[1]); break;
        case "F": first = Integer.parseInt(kv[1]); break;
      }
    }
  }
  int total = ((Number)aircraft.get("TotalSeats")).intValue();
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Edit Aircraft</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/css/styles.css">
</head>
<body>
  <jsp:include page="header.jsp" />
  <h1>Edit Aircraft</h1>
  <form method="post"
        action="<%= request.getContextPath() %>/editAircraft"
        onsubmit="return validateAircraftSeats()">

    <!-- keep PK hidden -->
    <input type="hidden"
           name="aircraftID"
           value="<%= aircraft.get("AircraftID") %>" />

    <label for="airline">Airline</label>
    <select name="AirlineID" id="airline" required>
      <% for (Map<String,Object> al : airlines) {
           String aid = String.valueOf(al.get("AirlineID"));
           boolean sel = aid.equals(String.valueOf(aircraft.get("AirlineID")));
         %>
        <option value="<%= aid %>" <%= sel ? "selected" : "" %>>
          <%= al.get("Name") %>
        </option>
      <% } %>
    </select>

    <label for="model">Model</label>
    <input type="text"
           id="model"
           name="Model"
           maxlength="20"
           required
           value="<%= aircraft.get("Model") %>" />

    <label for="totalSeats">Total Seats</label>
    <input type="number"
           id="totalSeats"
           name="TotalSeats"
           min="1"
           required
           value="<%= total %>" />

    <fieldset>
      <legend>Class Config</legend>
      <label for="eco">Economy</label>
      <input type="number"
             id="eco"
             name="EconomySeats"
             min="0"
             required
             value="<%= eco %>" />

      <label for="biz">Business</label>
      <input type="number"
             id="biz"
             name="BusinessSeats"
             min="0"
             required
             value="<%= biz %>" />

      <label for="first">First Class</label>
      <input type="number"
             id="first"
             name="FirstClassSeats"
             min="0"
             required
             value="<%= first %>" />
    </fieldset>

    <div class="form-actions">
      <button type="submit"
        style="
          background-color: #28a745;
          color: white;
          border: none;
          padding: .5em 1em;
          border-radius: 4px;
          cursor: pointer;
        ">
  Save Changes
</button>

      <a href="<%= request.getContextPath() %>/CustRep">
        <button type="button" class="btn-cancel">Cancel</button>
      </a>
    </div>
  </form>

  <script>
    // same validation you had in CustRepPortal.jsp
    function validateAircraftSeats() {
      const total = +document.getElementById('totalSeats').value || 0;
      const eco   = +document.getElementById('eco').value        || 0;
      const biz   = +document.getElementById('biz').value        || 0;
      const first = +document.getElementById('first').value      || 0;
      if (eco + biz + first !== total) {
        alert(`Class seats (${eco+biz+first}) must equal Total Seats (${total}).`);
        return false;
      }
      return true;
    }
  </script>
</body>
</html>
