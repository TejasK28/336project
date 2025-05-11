<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Flight Plan Details</title>
<link rel="stylesheet" href="<%= request.getContextPath() %>/css/styles.css">
<style>
    .status-waitlisted {
        color: #ff6b6b;
        font-weight: bold;
    }
    .status-confirmed {
        color: #51cf66;
        font-weight: bold;
    }
    .status-available {
        color: #868e96;
        font-style: italic;
    }
    .action-button {
        padding: 8px 16px;
        border: none;
        border-radius: 4px;
        cursor: pointer;
        font-weight: bold;
        transition: background-color 0.2s;
    }
    .purchase-button {
        background-color: #51cf66;
        color: white;
    }
    .purchase-button:hover {
        background-color: #40c057;
    }
    .waitlist-button {
        background-color: #ff6b6b;
        color: white;
    }
    .waitlist-button:hover {
        background-color: #fa5252;
    }
    .cancel-button {
        background-color: #ff6b6b;
        color: white;
    }
    .cancel-button:hover {
        background-color: #fa5252;
    }
    .button-disabled {
        background-color: #868e96;
        cursor: not-allowed;
    }
    .message {
        padding: 15px;
        margin: 15px 0;
        border-radius: 6px;
        font-weight: 500;
        display: flex;
        align-items: center;
        gap: 10px;
    }
    .success-message {
        background-color: #d3f9d8;
        color: #2b8a3e;
        border: 1px solid #51cf66;
    }
    .error-message {
        background-color: #ffe3e3;
        color: #c92a2a;
        border: 1px solid #ff6b6b;
    }
    .waitlist-message {
        background-color: #fff3bf;
        color: #e67700;
        border: 1px solid #ffd43b;
    }
    .info-message {
        background-color: #e7f5ff;
        color: #1971c2;
        border: 1px solid #74c0fc;
    }
    .dialog {
        display: none;
        position: fixed;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        background: white;
        padding: 20px;
        border-radius: 8px;
        box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        z-index: 1000;
    }
    .dialog-backdrop {
        display: none;
        position: fixed;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: rgba(0,0,0,0.5);
        z-index: 999;
    }
    .dialog-buttons {
        margin-top: 20px;
        text-align: right;
    }
    .dialog-buttons button {
        margin-left: 10px;
    }
</style>
</head>
<body>
    <jsp:include page="header.jsp" />
    
    <div class="main-content">
        <h1 class="page-title">Flight Plan #<%= request.getAttribute("flightPlanID") %></h1>
        
        <% if (request.getAttribute("success") != null) { %>
            <div class="message success-message">
                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
                    <polyline points="22 4 12 14.01 9 11.01"></polyline>
                </svg>
                <%= request.getAttribute("success") %>
            </div>
        <% } %>
        
        <% if (request.getAttribute("error") != null) { %>
            <div class="message error-message">
                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <circle cx="12" cy="12" r="10"></circle>
                    <line x1="12" y1="8" x2="12" y2="12"></line>
                    <line x1="12" y1="16" x2="12.01" y2="16"></line>
                </svg>
                <%= request.getAttribute("error") %>
            </div>
        <% } %>
        
        <% if (request.getAttribute("waitlist") != null) { %>
            <div class="message waitlist-message">
                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <circle cx="12" cy="12" r="10"></circle>
                    <line x1="12" y1="16" x2="12" y2="12"></line>
                    <line x1="12" y1="8" x2="12.01" y2="8"></line>
                </svg>
                <%= request.getAttribute("message") %>
                <form action="<%= request.getContextPath() %>/JoinWaitlist" method="post" style="display: inline;">
                    <input type="hidden" name="flightId" value="<%= request.getAttribute("flightId") %>">
                    <input type="hidden" name="className" value="<%= request.getAttribute("className") %>">
                    <input type="hidden" name="flightPlanID" value="<%= request.getAttribute("flightPlanID") %>">
                    <button type="submit" class="action-button waitlist-button">Join Waitlist</button>
                </form>
            </div>
        <% } %>

        <% if (request.getAttribute("info") != null) { %>
            <div class="message info-message">
                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <circle cx="12" cy="12" r="10"></circle>
                    <line x1="12" y1="16" x2="12" y2="12"></line>
                    <line x1="12" y1="8" x2="12.01" y2="8"></line>
                </svg>
                <%= request.getAttribute("info") %>
            </div>
        <% } %>
        
        <div class="report-section">
            <table class="report-table">
                <thead>
                    <tr>
                        <th>Flight Number</th>
                        <th>Airline</th>
                        <th>From</th>
                        <th>To</th>
                        <th>Departure</th>
                        <th>Arrival</th>
                        <th>Duration</th>
                        <th>Class</th>
                        <th>Price</th>
                        <th>Status</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                    List<Map<String, Object>> flights = (List<Map<String, Object>>) request.getAttribute("flights");
                    if (flights != null && !flights.isEmpty()) {
                        for (Map<String, Object> flight : flights) {
                            String status = (String) flight.get("status");
                            String statusClass = "status-" + status.toLowerCase();
                            String className = (String) flight.get("Class");
                            boolean isBusinessOrFirst = "B".equals(className) || "F".equals(className);
                    %>
                        <tr>
                            <td><%= flight.get("FlightNumber") %></td>
                            <td><%= flight.get("airline_name") %></td>
                            <td><%= flight.get("departure_airport") %> (<%= flight.get("departure_city") %>, <%= flight.get("departure_country") %>)</td>
                            <td><%= flight.get("arrival_airport") %> (<%= flight.get("arrival_city") %>, <%= flight.get("arrival_country") %>)</td>
                            <td><%= flight.get("DepartTime") %></td>
                            <td><%= flight.get("ArrivalTime") %></td>
                            <td><%= flight.get("Duration") %> minutes</td>
                            <td><%= flight.get("Class") %></td>
                            <td>$<%= flight.get("StandardFare") %></td>
                            <td class="<%= statusClass %>">
                                <%= status %>
                                <% if ("Waitlisted".equals(status) && flight.get("waitlist_date") != null) { %>
                                    <br><small>Since: <%= flight.get("waitlist_date") %></small>
                                <% } %>
                            </td>
                            <td>
                                <% if ("Available".equals(status)) { %>
                                    <form action="<%= request.getContextPath() %>/PurchaseTicket" method="post" class="purchase-form">
                                        <input type="hidden" name="flightID" value="<%= flight.get("FlightID") %>">
                                        <input type="hidden" name="className" value="<%= flight.get("Class") %>">
                                        <input type="hidden" name="ticketFare" value="<%= flight.get("StandardFare") %>">
                                        <input type="hidden" name="flightPlanID" value="<%= request.getAttribute("flightPlanID") %>">
                                        <button type="submit" class="action-button purchase-button">Purchase</button>
                                    </form>
                                <% } else if ("Waitlisted".equals(status)) { 
                                    boolean hasAvailableSeats = (Boolean)flight.get("hasAvailableSeats");
                                    if (hasAvailableSeats) { %>
                                        <form action="<%= request.getContextPath() %>/PurchaseTicket" method="post" class="purchase-form">
                                            <input type="hidden" name="flightID" value="<%= flight.get("FlightID") %>">
                                            <input type="hidden" name="className" value="<%= flight.get("Class") %>">
                                            <input type="hidden" name="ticketFare" value="<%= flight.get("StandardFare") %>">
                                            <input type="hidden" name="flightPlanID" value="<%= request.getAttribute("flightPlanID") %>">
                                            <button type="submit" class="action-button purchase-button">Purchase Available Seat</button>
                                        </form>
                                    <% } else { %>
                                        <button class="action-button waitlist-button button-disabled" disabled>
                                            Waitlisted
                                        </button>
                                    <% } %>
                                <% } else if ("Confirmed".equals(status)) { %>
                                    <form action="<%= request.getContextPath() %>/CancelTicket" method="post" class="cancel-form">
                                        <input type="hidden" name="flightId" value="<%= flight.get("FlightID") %>">
                                        <input type="hidden" name="className" value="<%= flight.get("Class") %>">
                                        <input type="hidden" name="flightPlanID" value="<%= request.getAttribute("flightPlanID") %>">
                                        <button type="submit" class="action-button cancel-button" <%= "E".equals(flight.get("Class")) ? "disabled" : "" %>>Cancel Ticket</button>
                                    </form>
                                <% } %>
                            </td>
                        </tr>
                    <%
                        }
                    } else {
                    %>
                        <tr>
                            <td colspan="11" style="text-align: center;">No flights found in this flight plan.</td>
                        </tr>
                    <%
                    }
                    %>
                </tbody>
            </table>
        </div>
    </div>

    <!-- Success Dialog -->
    <div id="successDialog" class="dialog">
        <h3>Success</h3>
        <p id="successMessage"></p>
        <div class="dialog-buttons">
            <button onclick="closeDialog('successDialog')">OK</button>
        </div>
    </div>

    <!-- Error Dialog -->
    <div id="errorDialog" class="dialog">
        <h3>Error</h3>
        <p id="errorMessage"></p>
        <div class="dialog-buttons">
            <button onclick="closeDialog('errorDialog')">OK</button>
        </div>
    </div>

    <!-- Dialog Backdrop -->
    <div id="dialogBackdrop" class="dialog-backdrop"></div>

    <script>
        // Show dialog with message
        function showDialog(dialogId, message) {
            document.getElementById(dialogId).querySelector('p').textContent = message;
            document.getElementById(dialogId).style.display = 'block';
            document.getElementById('dialogBackdrop').style.display = 'block';
        }

        // Close dialog
        function closeDialog(dialogId) {
            document.getElementById(dialogId).style.display = 'none';
            document.getElementById('dialogBackdrop').style.display = 'none';
        }

        // Handle form submissions
        document.querySelectorAll('.purchase-form').forEach(form => {
            form.addEventListener('submit', function(e) {
                e.preventDefault();
                if (confirm('Are you sure you want to purchase this ticket?')) {
                    this.submit();
                }
            });
        });

        document.querySelectorAll('.cancel-form').forEach(form => {
            form.addEventListener('submit', function(e) {
                e.preventDefault();
                if (confirm('Are you sure you want to cancel this ticket?')) {
                    this.submit();
                }
            });
        });

        // Show success/error messages from server response
        <% if (request.getAttribute("success") != null) { %>
            showDialog('successDialog', '<%= request.getAttribute("success") %>');
        <% } %>
        
        <% if (request.getAttribute("error") != null) { %>
            showDialog('errorDialog', '<%= request.getAttribute("error") %>');
        <% } %>
    </script>
</body>
</html>