import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        ReservationSystem system = new ReservationSystem();
        boolean running = true;

        System.out.println("============================================================");
        System.out.println(" BATANGAS GRAND TERMINAL - BUS TICKET RESERVATION SYSTEM    ");
        System.out.println("============================================================");

        while (running) {
            printMainMenu();
            int choice = readIntInRange("Select an option (1-7): ", 1, 7);

            switch (choice) {
                case 1:
                    handleSearchAndBookByDestination(system);
                    break;
                case 2:
                    system.displayBuses(system.getAllBuses());
                    break;
                case 3:
                    handleViewSeatMap(system);
                    break;
                case 4:
                    handleConfirmReservationPayment(system);
                    break;
                case 5:
                    handleCancelReservation(system);
                    break;
                case 6:
                    system.displayAllTickets();
                    break;
                case 7:
                    running = false;
                    System.out.println("\nExiting system. Thank you!");
                    break;
            }
        }
        scanner.close();
    }

    private static void printMainMenu() {
        System.out.println("\n--- MAIN MENU ---");
        System.out.println("1. Select Destination to View Bus Liners & Book Ticket");
        System.out.println("2. View All Terminal Schedules & Fares");
        System.out.println("3. View Bus Seat Map (Available / Reserved / Occupied)");
        System.out.println("4. Confirm Payment for a Reserved Seat ([RS] -> [OC])");
        System.out.println("5. Cancel Reservation / Release Seat");
        System.out.println("6. View All Tickets & Revenue Summary");
        System.out.println("7. Exit");
    }

    private static void handleSearchAndBookByDestination(ReservationSystem system) {
        ArrayList<String> destinations = system.getAvailableDestinations();
        System.out.println("\n--- STEP 1: SELECT DESTINATION FROM BATANGAS GRAND TERMINAL ---");
        for (int i = 0; i < destinations.size(); i++) {
            System.out.println("  [" + (i + 1) + "] " + destinations.get(i));
        }
        int destIdx = readIntInRange("Choose Destination (1-" + destinations.size() + "): ", 1, destinations.size()) - 1;
        String selectedDestination = destinations.get(destIdx);

        ArrayList<Bus> matchingBuses = system.getBusesByDestination(selectedDestination);
        System.out.println("\n--- STEP 2: BUS LINERS TRAVELING TO " + selectedDestination.toUpperCase() + " ---");
        system.displayBuses(matchingBuses);

        processBusBooking(system, matchingBuses);
    }

    private static void processBusBooking(ReservationSystem system, ArrayList<Bus> allowedBuses) {
        String busId = readNonEmptyString("Enter Bus ID from the list above to proceed (e.g., BUS-101): ");
        Bus selectedBus = system.findBusById(busId);
        if (selectedBus == null || !allowedBuses.contains(selectedBus)) {
            System.out.println("\n[Error] Invalid Bus ID for the selected destination.");
            return;
        }

        selectedBus.displaySeatMap();
        if (selectedBus.countByStatus(Seat.AVAILABLE) == 0) {
            System.out.println("\n[Notice] Sorry, this bus is completely full.");
            return;
        }

        int seatNumber = readIntInRange("Select an Available Seat Number (1-" + selectedBus.getTotalSeats() + "): ",
                1, selectedBus.getTotalSeats());

        String name = readNonEmptyString("Enter Passenger Full Name: ");
        String contact = readNonEmptyString("Enter Contact Number: ");
        String passengerType = readPassengerType();

        System.out.println("Select Booking Action:");
        System.out.println("  [1] Reserve Seat Only (Hold as RESERVED [RS] - Pay Later)");
        System.out.println("  [2] Book & Pay Now (Mark as OCCUPIED [OC])");
        int actionChoice = readIntInRange("Enter choice (1-2): ", 1, 2);
        boolean payImmediately = (actionChoice == 2);

        try {
            Passenger passenger = new Passenger(name, contact, passengerType);
            Ticket ticket = system.createBooking(selectedBus.getBusId(), seatNumber, passenger, payImmediately);
            System.out.println("\nBooking recorded successfully!");
            System.out.println(ticket.generateReceipt());
        } catch (InvalidBookingException | IllegalArgumentException e) {
            System.out.println("\n[Booking Failed] " + e.getMessage());
        }
    }

    private static void handleViewSeatMap(ReservationSystem system) {
        system.displayBuses(system.getAllBuses());
        String busId = readNonEmptyString("Enter Bus ID to view seat map: ");
        Bus bus = system.findBusById(busId);
        if (bus == null) {
            System.out.println("\n[Error] Bus ID not found.");
            return;
        }
        bus.displaySeatMap();
    }

    private static void handleConfirmReservationPayment(ReservationSystem system) {
        String ticketId = readNonEmptyString("Enter Reserved Ticket ID to confirm payment (e.g., TKT-1001): ");
        try {
            Ticket updatedTicket = system.confirmReservedTicket(ticketId);
            System.out.println("\nPayment confirmed! Seat #" + updatedTicket.getSeatNumber() + " is now OCCUPIED [OC].");
            System.out.println(updatedTicket.generateReceipt());
        } catch (InvalidBookingException e) {
            System.out.println("\n[Confirmation Failed] " + e.getMessage());
        }
    }

    private static void handleCancelReservation(ReservationSystem system) {
        String ticketId = readNonEmptyString("Enter Ticket ID to cancel (e.g., TKT-1001): ");
        try {
            system.cancelTicket(ticketId);
            System.out.println("\nTicket " + ticketId.toUpperCase() + " cancelled. The seat is now AVAILABLE again.");
        } catch (InvalidBookingException e) {
            System.out.println("\n[Cancellation Failed] " + e.getMessage());
        }
    }

    private static String readPassengerType() {
        System.out.println("Select Passenger Type (Fare Rules):");
        System.out.println("  [1] Regular (Standard Fare)");
        System.out.println("  [2] Student (20% Discount)");
        System.out.println("  [3] Senior Citizen / PWD (20% Discount)");
        System.out.println("  [4] Child Below 7 yrs (50% Discount)");
        int choice = readIntInRange("Enter passenger type (1-4): ", 1, 4);
        switch (choice) {
            case 2: return "STUDENT";
            case 3: return "SENIOR/PWD";
            case 4: return "CHILD";
            default: return "REGULAR";
        }
    }

    private static int readIntInRange(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) return value;
                System.out.println("[Error] Please enter a number between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("[Error] Invalid numeric input. Enter a whole number.");
            }
        }
    }

    private static String readNonEmptyString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) return input;
            System.out.println("[Error] Input cannot be blank.");
        }
    }
}