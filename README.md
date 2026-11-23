# SakayNa: A Bus Ticket Reservation System with Seat Selection and Rule-Based Fare Computation

**Description:** *"SakayNa: A Bus Ticket Reservation System with Seat Selection and Rule-Based Fare Computation"* is a console-based reservation and ticketing simulator modeled after the Batangas City Grand Terminal. Users can browse destinations, view real-time 3-state seat maps (`[01]` Available, `[RS]` Reserved, `[XX]` Occupied), reserve or pay for seats immediately, apply passenger discounts (Student, Senior Citizen/PWD, Child), and monitor terminal revenue. The program's foundation revolves around the four main OOP principles to ensure clean data protection, modular bus tier scaling, and accurate rule-based fare computation.

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
`Route.java` stays consistent and prevents unwanted modification of terminal origins, destinations, distances, and base fares, keeping route data safe and controlled.

```java
private String name;
private String contactNumber;
private String passengerType;

public String getName() { return name; }
public String getContactNumber() { return contactNumber; }
public String getPassengerType() { return passengerType; }
```
In `Passenger.java`, encapsulation is applied by keeping personal details and passenger classifications `private`, validating inputs inside the constructor, and exposing them only through public getter methods.

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
In `Seat.java`, a seat's booking state (`AVAILABLE`, `RESERVED`, `OCCUPIED`) and assigned `Passenger` cannot be altered directly outside the class. State transitions are strictly controlled through `reserve()`, `occupy()`, `confirmPayment()`, and `release()` to prevent double-booking.

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
`Ticket.java` protects the generated `ticketId`, passenger-bus-seat associations, and payment state, allowing controlled updates only through methods like `markAsPaid()`.

```java
private ArrayList<Route> routes;
private ArrayList<Bus> buses;
private ArrayList<Ticket> tickets;
```
Private fields from `ReservationSystem.java` are fully encapsulated so routes, buses, and issued tickets cannot be modified directly outside the class.

---

### Inheritance

```java
public class RegularBus extends Bus {
    public RegularBus(String busId, String busLiner, Route route, String departureTime, int totalSeats, boolean hasAircon) {
        super(busId, busLiner, route, departureTime, totalSeats, hasAircon);
    }
```
`RegularBus.java` inherits from the `Bus` parent class (`Bus.java`) and gains all shared attributes (`busId`, `busLiner`, `route`, `departureTime`, `seats`, `totalSeats`, `hasAircon`) and methods (`bookSeat()`, `displaySeatMap()`, `countByStatus()`) without rewriting code.

```java
public class DeluxeBus extends Bus {
    public DeluxeBus(String busId, String busLiner, Route route, String departureTime, int totalSeats, boolean hasAircon) {
        super(busId, busLiner, route, departureTime, totalSeats, hasAircon);
    }
```
`DeluxeBus.java` also extends `Bus`, meaning it automatically receives all attributes and seat-management behaviors of a `Bus` while representing a higher-tier coach.

```java
public class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}
```
`InvalidBookingException.java` inherits from Java's `Exception` class to create a custom checked exception for domain errors like invalid seats, double-bookings, and missing ticket IDs.

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
`Bus.java` is an `abstract` superclass modeling the general blueprint of a terminal bus. It includes essential details like `busId`, `route`, and `departureTime` while hiding internal seat-tracking logic and forcing subclasses to define `calculateTierSurcharge()`.

```java
public interface Billable {
    double calculateTotalFare();
    String generateReceipt();
}
```
The `Billable` interface from `Billable.java` declares what billing operations must exist (`calculateTotalFare()` and `generateReceipt()`) without showing how fares, surcharges, and discounts are calculated.

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
Both `RegularBus` and `DeluxeBus` override `calculateTierSurcharge()` to apply different pricing rules—`RegularBus` adds a ₱25.00 surcharge when air-conditioned (or ₱0.00 for ordinary), whereas `DeluxeBus` applies a flat ₱150.00 surcharge.

```java
// In ReservationSystem.java & Ticket.java
double regularFare = bus.getRoute().getBaseFare() + bus.calculateTierSurcharge();
```
Because `ReservationSystem` stores both `RegularBus` and `DeluxeBus` inside `ArrayList<Bus>`, calling `bus.calculateTierSurcharge()` dynamically runs the overridden method matching the actual bus subclass at runtime.

---

## Program Structure

### `Main` Class
* **ROLE:** Starts the system and controls the console menu.
* **RESPONSIBILITIES:**
  * Creates `ReservationSystem`
  * Displays the 7-option main menu loop
  * Validates user inputs (`readIntInRange()`, `readNonEmptyString()`, `readPassengerType()`)
  * Handles destination booking, seat map viewing, payment confirmation, and cancellation

### `ReservationSystem` Class
* **ROLE:** Core terminal controller. Handles schedules, bookings, payments, and cancellations.
* **DATA:**
  * `routes` (`ArrayList<Route>`)
  * `buses` (`ArrayList<Bus>`)
  * `tickets` (`ArrayList<Ticket>`)
* **RESPONSIBILITIES:**
  * `seedSampleData()`
  * `getAvailableDestinations()` / `getBusesByDestination(destination)`
  * `createBooking()` / `confirmReservedTicket()` / `cancelTicket()`
  * `displayBuses()` / `displayAllTickets()`
* **RELATIONSHIPS:**
  * Has-many `Route`, `Bus`, and `Ticket` objects
  * Uses `RegularBus` and `DeluxeBus` through polymorphism

### `Bus` Class *(Abstract)*
* **ROLE:** Base bus class (parent class for all buses).
* **DATA:**
  * `busId`, `busLiner`, `route`, `departureTime`
  * `seats` (`ArrayList<Seat>`), `totalSeats`, `hasAircon`
* **RESPONSIBILITIES:**
  * `getSeat()` / `bookSeat()` / `countByStatus()`
  * `displaySeatMap()`
  * Declares abstract `calculateTierSurcharge()`
* **RELATIONSHIPS:**
  * Has-a `Route` and has-many `Seat` objects
  * Parent of `RegularBus` and `DeluxeBus`

### `RegularBus` & `DeluxeBus` Classes
* **ROLE:** Concrete bus tier subclasses.
* **SPECIAL FEATURE (Polymorphism):**
  * Overrides `calculateTierSurcharge()` (`₱25.00` aircon / `₱0.00` ordinary for `RegularBus`; `₱150.00` for `DeluxeBus`)
* **INHERITANCE:**
  * Both extend `Bus` and inherit all seat-management methods

### `Billable` Interface
* **ROLE:** Defines billing behaviors for payable tickets.
* **METHODS:**
  * `calculateTotalFare()`
  * `generateReceipt()`

### `Ticket` Class
* **ROLE:** Represents an issued reservation or paid bus ticket.
* **DATA:**
  * `ticketId`, `passenger`, `bus`, `seatNumber`, `isPaid`
* **RESPONSIBILITIES:**
  * `calculateTotalFare()` (combines base fare, tier surcharge, and passenger discount)
  * `generateReceipt()`
  * `markAsPaid()` / `getPaymentStatus()`
* **RELATIONSHIPS:**
  * Has-a `Passenger` and has-a `Bus`

### `Seat` Class
* **ROLE:** Represents a single bus seat and its availability state.
* **DATA:**
  * `seatNumber`, `status` (`AVAILABLE`, `RESERVED`, `OCCUPIED`), `passenger`
* **RESPONSIBILITIES:**
  * `reserve()` / `occupy()` / `confirmPayment()` / `release()`
  * `isAvailable()` / `isReserved()` / `isOccupied()` / `getMapSymbol()`

### `Route` Class
* **ROLE:** Stores origin, destination, distance, and base fare details.
* **DATA:**
  * `routeId`, `origin`, `destination`, `distanceKm`, `baseFare`
* **RESPONSIBILITIES:**
  * Provides read-only getters and `getFormattedRoute()`

### `Passenger` Class
* **ROLE:** Stores commuter details and fare discount rules.
* **DATA:**
  * `name`, `contactNumber`, `passengerType` (`REGULAR`, `STUDENT`, `SENIOR/PWD`, `CHILD`)
* **RESPONSIBILITIES:**
  * Validates passenger inputs and provides `getDiscountRate()`

### `InvalidBookingException` Class
* **ROLE:** Custom checked exception for invalid booking actions.
* **RESPONSIBILITIES:**
  * Signals out-of-bounds seat numbers, double-booked seats, or missing ticket IDs
