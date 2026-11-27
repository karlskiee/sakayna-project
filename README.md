# SakayNa: A Bus Ticket Reservation System with Seat Selection and Rule-Based Fare Computation

> A Java-based console reservation and ticketing simulator modeled after the Batangas City Grand Terminal. Users can browse destinations, view real-time 3-state seat maps (`[01]` Available, `[RS]` Reserved, `[XX]` Occupied), reserve or pay for seats immediately, apply passenger discounts (Student, Senior Citizen/PWD, Child), and monitor terminal revenue. The program's foundation revolves around the four main OOP principles to ensure clean data protection, modular bus tier scaling, and accurate rule-based fare computation.

* **Terminal Hub:** Batangas Grand Terminal (Cubao via Skyway/C-5, LRT Buendia, PITX, Alabang, Lucena, Nasugbu, Calamba)
* **Seat Legend:** `[01]` Available | `[RS]` Reserved | `[XX]` Occupied
* **Fare Discounts:** Regular (`0%`), Student (`20%`), Senior Citizen/PWD (`20%`), Child below 7 yrs (`50%`)[cite: 2]

---

## Object-Oriented Programming Concepts Applied

### 1. Encapsulation

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
In `Seat.java`, a seat's booking state (`AVAILABLE`, `RESERVED`, `OCCUPIED`) and assigned `Passenger` cannot be altered directly outside the class. State transitions are strictly controlled through `reserve()`, `occupy()`, `confirmPayment()`, and `release()` to prevent double-booking.[cite: 2, 3]

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

### 2. Inheritance

```java
public class RegularBus extends Bus {
    public RegularBus(String busId, String busLiner, Route route, String departureTime, int totalSeats, boolean hasAircon) {
        super(busId, busLiner, route, departureTime, totalSeats, hasAircon);
    }
```
`RegularBus.java` inherits from the `Bus` parent class (`Bus.java`) and gains all shared attributes (`busId`, `busLiner`, `route`, `departureTime`, `seats`, `totalSeats`, `hasAircon`) and methods (`bookSeat()`, `displaySeatMap()`, `countByStatus()`) without rewriting code.[cite: 3]

```java
public class DeluxeBus extends Bus {
    public DeluxeBus(String busId, String busLiner, Route route, String departureTime, int totalSeats, boolean hasAircon) {
        super(busId, busLiner, route, departureTime, totalSeats, hasAircon);
    }
```
`DeluxeBus.java` also extends `Bus`, meaning it automatically receives all attributes and seat-management behaviors of a `Bus` while representing a higher-tier coach.[cite: 3, 4]

```java
public class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}
```
`InvalidBookingException.java` inherits from Java's `Exception` class to create a custom checked exception for domain errors like invalid seats, double-bookings, and missing ticket IDs.[cite: 3, 5]

---

### 3. Abstraction

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
`Bus.java` is an `abstract` superclass modeling the general blueprint of a terminal bus. It includes essential details like `busId`, `route`, and `departureTime` while hiding internal seat-tracking logic and forcing subclasses to define `calculateTierSurcharge()`.[cite: 3]

```java
public interface Billable {
    double calculateTotalFare();
    String generateReceipt();
}
```
The `Billable` interface from `Billable.java` declares what billing operations must exist (`calculateTotalFare()` and `generateReceipt()`) without showing how fares, surcharges, and discounts are calculated.[cite: 3]

---

### 4. Polymorphism

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
Because `ReservationSystem` stores both `RegularBus` and `DeluxeBus` inside `ArrayList<Bus>`, calling `bus.calculateTierSurcharge()` dynamically runs the overridden method matching the actual bus subclass at runtime.[cite: 3]

---

## Program Structure

```text
Main Menu (1-7) ─► Select Destination & Bus ─► View 2x2 Seat Map ─► Reserve [RS] or Pay [XX] ─► Print Receipt
```

* **`Main` Class**
  * **ROLE:** Starts the system and controls the console menu.
  * **RESPONSIBILITIES:** Creates `ReservationSystem`, displays the 7-option main menu loop, validates user inputs (`readIntInRange()`, `readNonEmptyString()`, `readPassengerType()`), and handles booking, seat map viewing, payment confirmation, and cancellation.[cite: 5]

* **`ReservationSystem` Class**
  * **ROLE:** Core terminal controller handling schedules, bookings, payments, and cancellations.
  * **DATA:** `routes` (`ArrayList<Route>`), `buses` (`ArrayList<Bus>`), `tickets` (`ArrayList<Ticket>`)
  * **RESPONSIBILITIES:** `seedSampleData()`, `getAvailableDestinations()`, `getBusesByDestination()`, `createBooking()`, `confirmReservedTicket()`, `cancelTicket()`, `displayBuses()`, `displayAllTickets()`
  * **RELATIONSHIPS:** Has-many `Route`, `Bus`, and `Ticket` objects; uses `RegularBus` and `DeluxeBus` through polymorphism.[cite: 3, 4]

* **`Bus` Class *(Abstract)***
  * **ROLE:** Base bus class (parent class for all buses).
  * **DATA:** `busId`, `busLiner`, `route`, `departureTime`, `seats` (`ArrayList<Seat>`), `totalSeats`, `hasAircon`
  * **RESPONSIBILITIES:** `getSeat()`, `bookSeat()`, `countByStatus()`, `displaySeatMap()`, and abstract `calculateTierSurcharge()`
  * **RELATIONSHIPS:** Has-a `Route` and has-many `Seat` objects; parent of `RegularBus` and `DeluxeBus`.[cite: 3]

* **`RegularBus` & `DeluxeBus` Classes**
  * **ROLE:** Concrete bus tier subclasses.
  * **SPECIAL FEATURE (Polymorphism):** Overrides `calculateTierSurcharge()` (`₱25.00` aircon / `₱0.00` ordinary for `RegularBus`; `₱150.00` for `DeluxeBus`).
  * **INHERITANCE:** Both extend `Bus` and inherit all seat-management methods.[cite: 3]

* **`Billable` Interface**
  * **ROLE:** Defines billing behaviors for payable tickets.
  * **METHODS:** `calculateTotalFare()`, `generateReceipt()`[cite: 3]

* **`Ticket` Class**
  * **ROLE:** Represents an issued reservation or paid bus ticket.
  * **DATA:** `ticketId`, `passenger`, `bus`, `seatNumber`, `isPaid`
  * **RESPONSIBILITIES:** `calculateTotalFare()`, `generateReceipt()`, `markAsPaid()`, `getPaymentStatus()`
  * **RELATIONSHIPS:** Has-a `Passenger` and has-a `Bus`.[cite: 4]

* **`Seat` Class**
  * **ROLE:** Represents a single bus seat and its availability state.
  * **DATA:** `seatNumber`, `status` (`AVAILABLE`, `RESERVED`, `OCCUPIED`), `passenger`
  * **RESPONSIBILITIES:** `reserve()`, `occupy()`, `confirmPayment()`, `release()`, `isAvailable()`, `isReserved()`, `isOccupied()`, `getMapSymbol()`[cite: 4]

* **`Route` Class**
  * **ROLE:** Stores origin, destination, distance, and base fare details.
  * **DATA:** `routeId`, `origin`, `destination`, `distanceKm`, `baseFare`
  * **RESPONSIBILITIES:** Provides read-only getters and `getFormattedRoute()`.[cite: 4]

* **`Passenger` Class**
  * **ROLE:** Stores commuter details and fare discount rules.
  * **DATA:** `name`, `contactNumber`, `passengerType` (`REGULAR`, `STUDENT`, `SENIOR/PWD`, `CHILD`)
  * **RESPONSIBILITIES:** Validates passenger inputs and provides `getDiscountRate()`.[cite: 4]

* **`InvalidBookingException` Class**
  * **ROLE:** Custom checked exception for invalid booking actions.
  * **RESPONSIBILITIES:** Signals out-of-bounds seat numbers, double-booked seats, or missing ticket IDs.[cite: 5]

---

 ## ***How to Run the Program***

### PREREQUISITES

1. Before running the program, make sure that Java JDK is installed on your computer. Open your terminal and check the installation by typing:

```bash
java -version
javac -version
```

2. Go to the GitHub repository:

   https://github.com/karlskiee/sakayna-project

3. Click the green **Code** button and select **Download ZIP**.

4. Extract the downloaded ZIP file into your preferred folder.

### AFTERWARDS, COMPILE THE PROGRAM:

5. Open Visual Studio Code or another Java-supported IDE.

6. Select **File**, then choose **Open Folder** and open the extracted `sakayna-project` folder.

7. Open the `src` folder containing the Java source files.

8. Open a new terminal inside the `src` folder.

9. Compile all Java files by entering:

```bash
javac *.java
```

10. After successful compilation, run the program by typing:

```bash
java Main
```

11. The Batangas Grand Terminal - Bus Ticket Reservation System will appear in the terminal.

---

## *TO NAVIGATE THE PROGRAM:*

### 1. Main Menu

- Displays the available features of the bus ticket reservation system.
- Users can choose from seven options:
  - 1 - Select Destination to View Bus Liners & Book Ticket
  - 2 - View All Terminal Schedules & Fares
  - 3 - View Bus Seat Map
  - 4 - Confirm Payment for a Reserved Seat
  - 5 - Cancel Reservation / Release Seat
  - 6 - View All Tickets & Revenue Summary
  - 7 - Exit

### 2. Destination Selection

- Choose a destination from Batangas Grand Terminal.
- Available destinations include:
  - Cubao (via Skyway)
  - Cubao (via C-5)
  - LRT Buendia
  - PITX
  - Alabang (Star Mall)
  - Lucena
  - Nasugbu
  - Calamba / Turbina
- The system displays the available bus liners traveling to the selected destination.

### 3. Bus Selection

- Enter the Bus ID from the displayed list.
- The system checks whether the selected bus belongs to the chosen destination.
- Displays the bus liner, destination, departure time, fare, and seat availability.

### 4. Seat Selection

- Displays the seat map of the selected bus.
- Users can choose an available seat.
- The system checks whether the selected seat can be booked.

Seat indicators:

- `[01]` - Available Seat
- `[RS]` - Reserved Seat
- `[XX]` - Occupied Seat

### 5. Passenger Information

- Enter the passenger's full name.
- Enter the passenger's contact number.
- Select the passenger classification:
  - Regular
  - Student
  - Senior Citizen / PWD
  - Child Below 7 Years

### 6. Booking and Payment Selection

Users can choose between two booking options:

- **Option 1: Reserve Seat Only**
  - Holds the selected seat as RESERVED `[RS]`.
  - Allows the passenger to pay later.

- **Option 2: Book & Pay Now**
  - Immediately marks the selected seat as OCCUPIED `[OC]`.
  - Generates a ticket with paid status.

### 7. Official Ticket Generation

- After a successful booking, the system generates an official ticket.
- Displays the following information:
  - Ticket ID
  - Passenger Name
  - Bus Liner
  - Route
  - Seat Number
  - Total Fare
  - Payment Status

### 8. Confirm Payment

- Enter the Reserved Ticket ID.
- The system checks whether the ticket exists and is eligible for payment confirmation.
- Once payment is confirmed, the seat status changes from RESERVED `[RS]` to OCCUPIED `[OC]`.
- Displays the updated official ticket.

### 9. Cancel Reservation

- Enter the Ticket ID to be cancelled.
- The system checks whether the ticket exists and can be cancelled.
- Once successfully cancelled, the seat becomes AVAILABLE again.

### 10. View All Tickets & Revenue Summary

- Displays all active tickets and reservations.
- Shows passenger information, bus details, routes, seat numbers, fares, and payment statuses.
- Displays the total number of active tickets, collected revenue, and pending revenue.

### 11. Exit Program

- Select option 7 to exit the program.
- Displays an exit message.

---

# *Sample Output*

### 1. Main Menu

Displays the system title and the seven available menu options.

```text
============================================================
 BATANGAS GRAND TERMINAL - BUS TICKET RESERVATION SYSTEM
============================================================

--- MAIN MENU ---
1. Select Destination to View Bus Liners & Book Ticket
2. View All Terminal Schedules & Fares
3. View Bus Seat Map (Available / Reserved / Occupied)
4. Confirm Payment for a Reserved Seat ([RS] -> [OC])
5. Cancel Reservation / Release Seat
6. View All Tickets & Revenue Summary
7. Exit

Select an option (1-7):
```

### 2. Destination Selection

Displays the available destinations from Batangas Grand Terminal.

```text
--- STEP 1: SELECT DESTINATION FROM BATANGAS GRAND TERMINAL ---

[1] Cubao (via Skyway)
[2] Cubao (via C-5)
[3] LRT Buendia
[4] PITX
[5] Alabang (Star Mall)
[6] Lucena
[7] Nasugbu
[8] Calamba / Turbina

Choose Destination:
```

### 3. Bus Selection

Displays the bus liners traveling to the selected destination, including their schedules and fares.

```text
--- STEP 2: BUS LINERS TRAVELING TO SELECTED DESTINATION ---

BUS ID   | BUS LINER       | DESTINATION | DEPARTURE | FARE
-------------------------------------------------------------
BUS-101  | Ceres Transport | Cubao       | 05:00 AM  | PHP 287.00
BUS-102  | Goldstar        | Cubao       | 05:30 AM  | PHP 287.00
BUS-103  | DLTB Co.        | Cubao       | 06:00 AM  | PHP 286.00

Enter Bus ID from the list above to proceed:
```

### 4. Seat Selection

Displays the seat map of the selected bus.

```text
--- SEAT MAP ---

[01] [02] [03] [04]
[05] [06] [07] [08]
[09] [10] [11] [12]
[13] [14] [15] [16]

[17] [18] [19] [20]
[21] [22] [23] [24]
[25] [26] [27] [28]

[29] [30] [31] [32]
[33] [34] [35] [36]
[37] [38] [39] [40]

Select an Available Seat Number:
```

### 5. Passenger Information

Requests the passenger's personal information and classification.

```text
Enter Passenger Full Name: Juan Dela Cruz
Enter Contact Number: 09123456789

Select Passenger Type:
[1] Regular
[2] Student
[3] Senior Citizen / PWD
[4] Child Below 7 Years

Enter passenger type:
```

### 6. Booking Action

Allows the passenger to choose whether to reserve the seat or pay immediately.

```text
Select Booking Action:

[1] Reserve Seat Only (Hold as RESERVED [RS] - Pay Later)
[2] Book & Pay Now (Mark as OCCUPIED [OC])

Enter choice (1-2):
```

### 7. Successful Booking and Official Ticket

Displays the generated ticket after a successful booking.

```text
Booking recorded successfully!

========================================
              OFFICIAL TICKET
========================================
Ticket ID   : TKT-XXXX
Passenger   : Juan Dela Cruz
Bus Liner   : Ceres Transport
Route       : Batangas Grand Terminal to Cubao
Seat Number : 1
Total Fare  : PHP 287.00
Status      : PAID
========================================
```

### 8. Confirm Payment

Displays the result after successfully confirming payment for a reserved seat.

```text
Enter Reserved Ticket ID to confirm payment:
TKT-XXXX

Payment confirmed! Seat #1 is now OCCUPIED [OC].

========================================
              OFFICIAL TICKET
========================================
Ticket ID   : TKT-XXXX
Passenger   : Juan Dela Cruz
Status      : PAID
========================================
```

### 9. Cancel Reservation

Displays the confirmation after successfully cancelling a reservation.

```text
Enter Ticket ID to cancel:
TKT-XXXX

Ticket TKT-XXXX cancelled.
The seat is now AVAILABLE again.
```

### 10. View All Tickets & Revenue Summary

Displays the active tickets and revenue summary.

```text
--- ISSUED TICKETS & RESERVATIONS ---

Ticket ID | Passenger | Bus Liner | Seat | Fare | Status

Total Active Tickets: 1
Collected Revenue: PHP 287.00
Pending Revenue: PHP 0.00
```

### 11. Exit Program

Displays the final message when the user chooses to exit.

```text
Exiting system. Thank you!
```

## 👥 Authors & Acknowledgements

<table>
  <tr>
    <th></th>
    <th>Name</th>
  </tr>
  <tr>
    <td>
      <!-- NOTE: Palitan ang link sa ibaba kung gagamit ng sariling picture ni Dolor, John Karl -->
      <img src="https://github.com/karlskiee.png" width="100" />
    </td>
    <td>
      <b>Dolor, John Karl</b><br>
      <a href="https://github.com/karlskiee">
        <img src="https://img.shields.io/badge/GitHub-100000?style=flat&logo=github&logoColor=white" alt="GitHub" />
      </a>
    </td>
  </tr>
  <tr>
    <td>
      <!-- NOTE: Palitan ang link sa ibaba kung gagamit ng sariling picture ni Macaraig, Brenlesly -->
      <img src="[![Uploading 929889170_1144196924686120_5057922159914180048_n.jpg…]()
](https://scontent.fmnl37-1.fna.fbcdn.net/v/t1.15752-9/929889170_1144196924686120_5057922159914180048_n.jpg?stp=dst-jpg_tt6&cstp=mx747x883&ctp=s747x883&_nc_cat=101&_nc_map=urlgen_bucketless&ccb=1-7&_nc_sid=9f807c&_nc_eui2=AeEjXpp80pWFofUnwagGBlt4m701glVzmF-bvTWCVXOYX06B7pGsSiC6xItseJ4SRbRoAp7-9zPdsPh9m_DC-OR1&_nc_ohc=zhRNunUAajYQ7kNvwF_kItS&_nc_oc=Adq41Teb7WsU66opGmVf7ZDY5ipNt3UwAAH0UEqD6lhXJ12RJFDjpGvyOUbgIUXwMxA&_nc_zt=23&_nc_ht=scontent.fmnl37-1.fna&_nc_ss=7b3a8&oh=03_Q7cD7AEsdm0A3Ctux2TW3NzRp0RMRFXS37lU09MNikl7EqE9IA&oe=6B30E37C)" width="100" />
    </td>
    <td>
      <b>Macaraig, Brenlesly</b><br>
      <a href="https://github.com/brenleslymacaraig">
        <img src="https://img.shields.io/badge/GitHub-100000?style=flat&logo=github&logoColor=white" alt="GitHub" />
      </a>
    </td>
  </tr>
  <tr>
    <td>
      <!-- NOTE: Palitan ang link sa ibaba kung gagamit ng sariling picture ni Mercado, Jove -->
      <img src="https://github.com/JM-HOBIE.png" width="100" />
    </td>
    <td>
      <b>Mercado, Jove</b><br>
      <a href="https://github.com/JM-HOBIE">
        <img src="https://img.shields.io/badge/GitHub-100000?style=flat&logo=github&logoColor=white" alt="GitHub" />
      </a>
    </td>
  </tr>
  <tr>
    <td>
      <!-- NOTE: Palitan ang link sa ibaba kung gagamit ng sariling picture ni Villanueva, Abegail -->
      <img src="[https://github.com/villanuevaabegail603-ux.png](https://scontent.fmnl37-1.fna.fbcdn.net/v/t1.15752-9/933979299_1153681417410595_4658419974784732106_n.jpg?stp=dst-jpg_tt6&cstp=mx360x360&ctp=s360x360&_nc_cat=105&_nc_map=urlgen_bucketless&ccb=1-7&_nc_sid=9f807c&_nc_eui2=AeFVyyGBzacu_IUjd0RS-ESG6Eau2oTIEnfoRq7ahMgSd3hspgscYiGnIacXzaBev9SHw3FWlfsuhp5wQoJTeImj&_nc_ohc=Rd3gzT-bfw8Q7kNvwGvSDVT&_nc_oc=Ado1IwszF84S8s0659fy-eM_euTGKyUr--YuvHjfI0KTk9l7AU8TLgMtcHtzvM8SkuA&_nc_zt=23&_nc_ht=scontent.fmnl37-1.fna&_nc_ss=7b3a8&oh=03_Q7cD7AEPeP6Z1fAzRU5NjBJi9928lY9s4LXizSDRvcjITM3T2A&oe=6B30ED5D)" width="100" />
    </td>
    <td>
      <b>Villanueva, Abegail</b><br>
      <a href="https://github.com/villanuevaabegail603-ux">
        <img src="https://img.shields.io/badge/GitHub-100000?style=flat&logo=github&logoColor=white" alt="GitHub" />
      </a>
    </td>
  </tr>
</table>
