<div align="center">

# 🚌 SakayNa: Bus Ticket Reservation System
**A Console-Based Seat Selection & Rule-Based Fare Computation Simulator**

![Java](https://img.shields.io/badge/Language-Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![OOP](https://img.shields.io/badge/Paradigm-Object--Oriented%20Programming-2EA44F?style=for-the-badge)
![Terminal](https://img.shields.io/badge/Hub-Batangas%20Grand%20Terminal-0969DA?style=for-the-badge)

*Built for OOP 101 — Batangas State University (The National Engineering University)*

</div>

---

## 📖 Overview

**SakayNa** is a Java-based bus ticket reservation and ticketing simulator modeled after the daily operations of the **Batangas City Grand Terminal**. Designed for terminal ticketing clerks and dispatchers, it replaces error-prone manual logs with real-time 3-state seat mapping, automated statutory discount calculations, and lifecycle tracking from reservation to payment or cancellation.

```text
============================================================
 BATANGAS GRAND TERMINAL - BUS TICKET RESERVATION SYSTEM
============================================================
--- SEAT MAP FOR BUS-101 (Ceres Transport) ---
[XX] = Occupied | [RS] = Reserved | [01] = Available

[XX] [02]   [RS] [04]
[05] [06]   [07] [08]
```

### ✨ Key Features
| Feature | Description |
| :--- | :--- |
| 🗺️ **Route & Liner Filtering** | Browse destinations from Batangas Grand Terminal (Cubao via Skyway/C-5, LRT Buendia, PITX, Alabang, Lucena, Nasugbu, Calamba) with live fares and departure schedules. |
| 💺 **3-State Seat Mapping** | Interactive 2×2 seating layout displaying **Available** (`[01]`), **Reserved** (`[RS]`), and **Occupied** (`[XX]`) seats to prevent double-booking. |
| 🧮 **Rule-Based Fare Engine** | Combines route base fares, bus tier surcharges, and passenger discounts (**20%** Student/Senior/PWD, **50%** Child below 7 yrs, **0%** Regular). |
| 🎟️ **Ticket & Revenue Tracking** | Supports reservation holds, payment confirmation (`[RS]` → `[XX]`), cancellations, itemized receipts, and terminal revenue summaries. |

---

## 🧱 Object-Oriented Programming Concepts Applied

The system's architecture revolves around the four core OOP principles:

### 1. 🔒 Encapsulation
Sensitive data fields across all domain models are declared `private` and exposed only through controlled getters and state-transition methods.

* **`Route.java`:** Protects terminal origins, destinations, distances, and base fares from external alteration.
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

* **`Passenger.java`:** Validates non-empty commuter inputs in the constructor and encapsulates passenger categories.
```java
private String name;
private String contactNumber;
private String passengerType;

public String getName() { return name; }
public String getContactNumber() { return contactNumber; }
public String getPassengerType() { return passengerType; }
```

* **`Seat.java`:** Restricts seat status changes (`AVAILABLE`, `RESERVED`, `OCCUPIED`) strictly to validated transition methods to prevent double-booking.
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

* **`Ticket.java` & `ReservationSystem.java`:** Protects generated ticket IDs, payment status (`isPaid`), and master `ArrayList` collections (`routes`, `buses`, `tickets`) so modifications only occur through controlled booking workflows.

---

### 2. 🧬 Inheritance
Subclasses reuse shared attributes and seat-management logic from parent classes without code duplication.

* **`RegularBus.java` & `DeluxeBus.java`:** Both inherit core fields (`busId`, `busLiner`, `route`, `departureTime`, `seats`, `totalSeats`, `hasAircon`) and methods (`bookSeat()`, `displaySeatMap()`, `countByStatus()`) from `Bus.java`.
```java
public class RegularBus extends Bus {
    public RegularBus(String busId, String busLiner, Route route, String departureTime, int totalSeats, boolean hasAircon) {
        super(busId, busLiner, route, departureTime, totalSeats, hasAircon);
    }
```

* **`InvalidBookingException.java`:** Extends Java's `Exception` class to create a domain-specific checked exception for invalid seats, double-bookings, and missing ticket IDs.
```java
public class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}
```

---

### 3. 🧩 Abstraction
Abstract classes and interfaces separate high-level system contracts from tier-specific implementation details.

* **`Bus.java` (Abstract Superclass):** Defines the general blueprint of a terminal bus and manages internal seat lists while requiring every subclass to implement `calculateTierSurcharge()`.
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

* **`Billable.java` (Interface):** Establishes a clean billing contract declaring *what* payable operations must exist (`calculateTotalFare()` and `generateReceipt()`) without exposing calculation internals.
```java
public interface Billable {
    double calculateTotalFare();
    String generateReceipt();
}
```

---

### 4. 🔄 Polymorphism
Subclasses override parent behaviors so the reservation engine can calculate fares dynamically at runtime.

* **Method Overriding (`RegularBus.java` vs. `DeluxeBus.java`):** Each bus tier implements its own surcharge rule.
```java
// RegularBus.java
@Override
public double calculateTierSurcharge() {
    return hasAircon ? 25.00 : 0.00;
}

// DeluxeBus.java
@Override
public double calculateTierSurcharge() {
    return 150.00;
}
```

* **Runtime Dispatch (`ReservationSystem.java` & `Ticket.java`):** Both `RegularBus` and `DeluxeBus` are stored together in `ArrayList<Bus>`. Calling `bus.calculateTierSurcharge()` dynamically executes the appropriate subclass formula at runtime without conditional `instanceof` checks.
```java
double regularFare = bus.getRoute().getBaseFare() + bus.calculateTierSurcharge();
```

---

## 🏗️ Program Structure

```text
Main (Console UI & Input Validation)
 └── ReservationSystem (Controller: Routes, Buses, Tickets, Revenue)
      ├── Route (Origin, Destination, Distance, Base Fare)
      ├── Bus [Abstract]
      │    ├── RegularBus (+₱25 Aircon / ₱0 Ordinary)
      │    ├── DeluxeBus  (+₱150 Deluxe Tier)
      │    └── Seat (AVAILABLE / RESERVED / OCCUPIED)
      ├── Ticket [Billable] (Fare Computation & Receipt Generation)
      │    └── Passenger (REGULAR, STUDENT, SENIOR/PWD, CHILD)
      └── InvalidBookingException (Custom Checked Exception)
```

| Class / Interface | Role & Key Responsibilities |
| :--- | :--- |
| **`Main`** | Entry point and console UI controller; handles the 7-option menu loop and input validation (`readIntInRange`, `readNonEmptyString`, `readPassengerType`). |
| **`ReservationSystem`** | Central controller managing `routes`, `buses`, and `tickets`; handles destination filtering, `createBooking()`, `confirmReservedTicket()`, `cancelTicket()`, and revenue reporting. |
| **`Bus` *(Abstract)*** | Base bus blueprint managing `ArrayList<Seat>`, `bookSeat()`, `displaySeatMap()`, `countByStatus()`, and abstract `calculateTierSurcharge()`. |
| **`RegularBus` / `DeluxeBus`** | Concrete bus subclasses overriding `calculateTierSurcharge()` for standard/aircon vs. deluxe coach tiers. |
| **`Billable` *(Interface)*** | Contract defining `calculateTotalFare()` and `generateReceipt()`. |
| **`Ticket`** | Links `Passenger`, `Bus`, and `seatNumber`; computes discounted fares, tracks payment status (`PAID` vs. `PENDING`), and generates itemized receipts. |
| **`Seat`** | Tracks individual seat numbers, availability states, and state transitions (`reserve()`, `occupy()`, `confirmPayment()`, `release()`). |
| **`Route`** | Encapsulates transit route details (`routeId`, `origin`, `destination`, `distanceKm`, `baseFare`). |
| **`Passenger`** | Validates commuter identity and provides statutory/age-based discount rates (`getDiscountRate()`). |
| **`InvalidBookingException`** | Custom checked exception thrown on invalid seat numbers, unavailable seats, or unrecognized ticket IDs. |

---

## 👥 Project Team (Group 7)

| Name | GitHub |
| :--- | :--- |
| **Dolor, John Karl** | [@karlskiee](https://github.com/karlskiee) |
| **Macaraig, Brenlesly** | [@brenleslymacaraig](https://github.com/brenleslymacaraig) |
| **Mercado, Jove** | [@JM-HOBIE](https://github.com/JM-HOBIE) |
| **Villanueva, Abegail** | [@villanuevaabegail603-ux](https://github.com/villanuevaabegail603-ux) |
