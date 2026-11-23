# SakayNa: A Bus Ticket Reservation System with Seat Selection and Rule-Based Fare Computation

**Description:** *"SakayNa: A Bus Ticket Reservation System with Seat Selection and Rule-Based Fare Computation"* is a Java-based console reservation and ticketing simulator built for bus terminal ticketing clerks and dispatchers at the Batangas Grand Terminal. Users can browse destinations, view real-time 3-state visual seat maps (`[01]` Available, `[RS]` Reserved, `[XX]` Occupied), hold or pay for seats immediately, apply statutory and age-based discounts (Student, Senior Citizen/PWD, Child), and monitor terminal revenue. The system's foundation revolves around the four main Object-Oriented Programming (OOP) principles to ensure clean data protection, modular bus tier scaling, and accurate rule-based fare computation.

---

## Object-Oriented Programming Concepts Applied

The following is the detailed documentation of how the OOP principles were used throughout the program:

### Encapsulation

```java
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
```
`Route.java` keeps terminal origins, destinations, distances, and base fares `private`, preventing unwanted external modification and exposing route details safely through read-only getters.

```java
private String name;
private String contactNumber;
private String passengerType;

public String getName() { return name; }
public String getContactNumber() { return contactNumber; }
public String getPassengerType() { return passengerType; }
```
In `Passenger.java`, encapsulation protects commuter names, contact numbers, and passenger classifications (`REGULAR`, `STUDENT`, `SENIOR/PWD`, `CHILD`), validating inputs inside the constructor and exposing them strictly through public getters.

```java
private int seatNumber;
private String status;
private Passenger passenger;

public void reserve(Passenger passenger) {
    this.passenger = passenger;
    this.status = RESERVED;
}

public void occupy(Passenger passenger) {
    this.passenger = passenger;
    this.status = OCCUPIED;
}

public void confirmPayment() {
    if (isReserved()) {
        this.status = OCCUPIED;
    }
}

public void release() {
    this.passenger = null;
    this.status = AVAILABLE;
}
```
In `Seat.java`, a seat's booking state (`AVAILABLE`, `RESERVED`, `OCCUPIED`) and assigned `Passenger` cannot be altered directly outside the class. State changes are strictly controlled through `reserve()`, `occupy()`, `confirmPayment()`, and `release()` to prevent double-booking.

```java
private String ticketId;
private Passenger passenger;
private Bus bus;
private int seatNumber;
private boolean isPaid;

public String getTicketId() { return ticketId; }
public Passenger getPassenger() { return passenger; }
public Bus getBus() { return bus; }
public int getSeatNumber() { return seatNumber; }
public void markAsPaid() { this.isPaid = true; }
```
`Ticket.java` protects the generated `ticketId`, passenger-bus-seat associations, and payment state, allowing updates only through controlled methods such as `markAsPaid()`.

```java
private ArrayList<Route> routes;
private ArrayList<Bus> buses;
private ArrayList<Ticket> tickets;
```
In `ReservationSystem.java`, the master collections of routes, buses, and issued tickets are fully encapsulated as `private`, ensuring bookings, payment confirmations, and cancellations happen exclusively through validated controller methods.

---

### Inheritance

```java
public class RegularBus extends Bus {
    public RegularBus(String busId, String busLiner, Route route, String departureTime, int totalSeats, boolean hasAircon) {
        super(busId, busLiner, route, departureTime, totalSeats, hasAircon);
    }
```
`RegularBus.java` extends the `Bus` parent class (`Bus.java`) and automatically inherits all shared fields (`busId`, `busLiner`, `route`, `departureTime`, `seats`, `totalSeats`, `hasAircon`) and seat-management methods (`bookSeat()`, `displaySeatMap()`, `countByStatus()`, and getters) without rewriting code.

```java
public class DeluxeBus extends Bus {
    public DeluxeBus(String busId, String busLiner, Route route, String departureTime, int totalSeats, boolean hasAircon) {
        super(busId, busLiner, route, departureTime, totalSeats, hasAircon);
    }
```
`DeluxeBus.java` also inherits from `Bus`, reusing the exact same seat tracking and schedule logic while representing a higher-tier coach service.

```java
public class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}
```
`InvalidBookingException.java` extends Java's `Exception` class to provide a custom checked exception for domain-specific errors such as out-of-bounds seat numbers, double-booking unavailable seats, or invalid ticket IDs.

---

### Abstraction

```java
public abstract class Bus {
    protected String busId;
    protected String busLiner;
    protected Route route;
    protected String departureTime;
    protected ArrayList<Seat> seats;
    protected int totalSeats;
    protected boolean hasAircon;

    // ... shared seat management and display methods ...

    public abstract double calculateTierSurcharge();
}
```
`Bus.java` is an `abstract` superclass modeling the general blueprint of a terminal bus. It handles internal seat-tracking details while declaring the abstract method `calculateTierSurcharge()`, requiring every concrete bus subclass to define its own surcharge rule.

```java
public interface Billable {
    double calculateTotalFare();
    String generateReceipt();
}
```
`Billable.java` defines a billing contract declaring *what* payable operations must be provided (`calculateTotalFare()` and `generateReceipt()`) while hiding *how* fares, surcharges, and discounts are calculated.

---

### Polymorphism

```java
// In RegularBus.java
@Override
public double calculateTierSurcharge() {
    return hasAircon ? 25.00 : 0.00;
}

// In DeluxeBus.java
@Override
public double calculateTierSurcharge() {
    return 150.00; 
}
```
Both `RegularBus` and `DeluxeBus` override `calculateTierSurcharge()` to apply tier-specific pricing—`RegularBus` adds a ₱25.00 surcharge when air-conditioned (or ₱0.00 for ordinary), while `DeluxeBus` applies a flat ₱150.00 tier surcharge.

```java
// In ReservationSystem.java & Ticket.java
double regularFare = bus.getRoute().getBaseFare() + bus.calculateTierSurcharge();
```
Through runtime polymorphism, `ReservationSystem` stores both `RegularBus` and `DeluxeBus` objects inside a single `ArrayList<Bus>`, and `Ticket` references a `Bus` object. When `bus.calculateTierSurcharge()` is invoked during schedule display or fare computation, Java dynamically executes the overridden method matching the actual bus subclass at runtime.

---

## Program Structure

### `Main` Class
* **ROLE:** Entry point and console user interface controller.
* **DATA:**
  * `scanner` (`Scanner`)
* **RESPONSIBILITIES:**
  * Initializes `ReservationSystem` and runs the 7-option main menu loop
  * Validates user inputs (`readIntInRange()`, `readNonEmptyString()`, `readPassengerType()`)
  * Routes operations (`handleSearchAndBookByDestination()`, `handleViewSeatMap()`, `handleConfirmReservationPayment()`, `handleCancelReservation()`)

### `ReservationSystem` Class
* **ROLE:** Central terminal controller managing schedules, seat bookings, payments, and cancellations.
* **DATA:**
  * `routes` (`ArrayList<Route>`)
  * `buses` (`ArrayList<Bus>`)
  * `tickets` (`ArrayList<Ticket>`)
* **RESPONSIBILITIES:**
  * `seedSampleData()` — initializes Batangas Grand Terminal routes and bus liners
  * `getAvailableDestinations()` / `getBusesByDestination(destination)`
  * `createBooking()` / `confirmReservedTicket()` / `cancelTicket()`
  * `displayBuses()` / `displayAllTickets()` (tracks active tickets, collected revenue, and pending revenue)
* **RELATIONSHIPS:**
  * Has-many `Route`, `Bus` (`RegularBus` & `DeluxeBus` via polymorphism), and `Ticket` objects

### `Bus` Class *(Abstract)*
* **ROLE:** Base blueprint for all physical terminal buses (parent class).
* **DATA:**
  * `busId`, `busLiner`, `route`, `departureTime`, `seats` (`ArrayList<Seat>`), `totalSeats`, `hasAircon`
* **RESPONSIBILITIES:**
  * Manages individual `Seat` objects (`getSeat()`, `bookSeat()`, `countByStatus()`)
  * Displays the 2×2 visual seat layout (`displaySeatMap()`)
  * Declares abstract `calculateTierSurcharge()`
* **RELATIONSHIPS:**
  * Has-a `Route` and has-many `Seat` objects
  * Parent of `RegularBus` and `DeluxeBus`

### `RegularBus` & `DeluxeBus` Classes
* **ROLE:** Concrete bus tier subclasses.
* **SPECIAL FEATURE (Polymorphism):**
  * `RegularBus` overrides `calculateTierSurcharge()` to return `25.00` (with aircon) or `0.00` (ordinary)
  * `DeluxeBus` overrides `calculateTierSurcharge()` to return `150.00`
* **INHERITANCE:**
  * Both extend `Bus` and inherit all seat-management and schedule capabilities

### `Billable` Interface
* **ROLE:** Defines the billing and receipt contract for payable tickets.
* **METHODS:**
  * `calculateTotalFare()`
  * `generateReceipt()`

### `Ticket` Class
* **ROLE:** Represents an issued reservation or paid ticket.
* **DATA:**
  * `ticketId`, `passenger`, `bus`, `seatNumber`, `isPaid`
* **RESPONSIBILITIES:**
  * Computes total fare (`calculateTotalFare()`) by combining route base fare, bus tier surcharge, and passenger discounts (`20%` Student/Senior/PWD, `50%` Child, `0%` Regular)
  * Generates itemized digital receipts (`generateReceipt()`)
  * Updates payment state (`markAsPaid()`, `getPaymentStatus()`)
* **RELATIONSHIPS:**
  * Has-a `Passenger` and has-a `Bus`

### `Seat` Class
* **ROLE:** Tracks an individual bus seat, its 3-state availability (`AVAILABLE`, `RESERVED`, `OCCUPIED`), and assigned passenger.
* **DATA:**
  * `seatNumber`, `status`, `passenger`
* **RESPONSIBILITIES:**
  * Manages seat state transitions (`reserve()`, `occupy()`, `confirmPayment()`, `release()`)
  * Checks status (`isAvailable()`, `isReserved()`, `isOccupied()`) and formats map symbols (`getMapSymbol()`)

### `Route` Class
* **ROLE:** Stores origin, destination, distance, and base fare data for a transit route.
* **DATA:**
  * `routeId`, `origin`, `destination`, `distanceKm`, `baseFare`
* **RESPONSIBILITIES:**
  * Provides read-only getters and formatted route summaries (`getFormattedRoute()`)

### `Passenger` Class
* **ROLE:** Stores commuter details and determines applicable discount rules.
* **DATA:**
  * `name`, `contactNumber`, `passengerType` (`REGULAR`, `STUDENT`, `SENIOR/PWD`, `CHILD`)
* **RESPONSIBILITIES:**
  * Validates non-empty commuter details and normalizes passenger categories
  * Provides statutory/age-based discount rates (`getDiscountRate()`)

### `InvalidBookingException` Class
* **ROLE:** Custom checked exception for invalid reservation operations.
* **RESPONSIBILITIES:**
  * Signals invalid seat numbers, double-booking attempts, unpaid/already-paid ticket errors, or missing ticket IDs
