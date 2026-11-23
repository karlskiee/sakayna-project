SakayNa: A Bus Ticket Reservation System with Seat Selection and Rule-Based Fare Computation
"SakayNa: A Bus Ticket Reservation System with Seat Selection and Rule-Based Fare Computation" is a Java-based console reservation and ticketing simulator built for bus terminal ticketing clerks and dispatchers at the Batangas Grand Terminal. Users can browse destinations, view real-time 3-state visual seat maps ([01] Available, [RS] Reserved, [XX] Occupied), hold or pay for seats immediately, apply statutory and age-based discounts (Student, Senior Citizen/PWD, Child), and monitor terminal revenue. The system's foundation revolves around the four main Object-Oriented Programming (OOP) principles to ensure clean data protection, modular bus tier scaling, and accurate rule-based fare computation.
Object-Oriented Programming Concepts Applied
The following is the detailed documentation of how the OOP principles were used throughout the program:
Encapsulation
private String routeId;
private String origin;
private String destination;
private double distanceKm;
private double baseFare;

public String getRouteId() { return routeId; }
public String getOrigin() { return origin; }
public String getDestination() { return destination; }
public double getDistanceKm() { return distanceKm; }
public double getBaseFare() { return baseFare; }

Route.java keeps terminal origins, destinations, distances, and base fares private, preventing unwanted external modification and exposing route details safely through read-only getters.

private String name;
private String contactNumber;
private String passengerType;

public String getName() { return name; }
public String getContactNumber() { return contactNumber; }
public String getPassengerType() { return passengerType; }
