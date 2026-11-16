import java.util.ArrayList;

public class ReservationSystem {
    private ArrayList<Route> routes;
    private ArrayList<Bus> buses;
    private ArrayList<Ticket> tickets;

    public ReservationSystem() {
        this.routes = new ArrayList<>();
        this.buses = new ArrayList<>();
        this.tickets = new ArrayList<>();
        seedSampleData();
    }

    private void seedSampleData() {
        // Cubao routes split by highway corridor (Skyway vs. C-5)
        Route rCubaoSkywayCeres = new Route("RT-CUB-SKY-C", "Batangas Grand Terminal", "Cubao (via Skyway)", 115.0, 287.00);
        Route rCubaoSkywayStd = new Route("RT-CUB-SKY-S", "Batangas Grand Terminal", "Cubao (via Skyway)", 115.0, 286.00);
        Route rCubaoC5 = new Route("RT-CUB-C5", "Batangas Grand Terminal", "Cubao (via C-5)", 118.0, 286.00);

        // Other Batangas City Grand Terminal routes & fares
        Route rBuendiaCeres = new Route("RT-BND-C", "Batangas Grand Terminal", "LRT Buendia", 108.0, 287.00);
        Route rBuendiaStd = new Route("RT-BND-S", "Batangas Grand Terminal", "LRT Buendia", 108.0, 286.00);
        Route rPitxCeres = new Route("RT-PTX-C", "Batangas Grand Terminal", "PITX", 100.0, 287.00);
        Route rPitxStd = new Route("RT-PTX-S", "Batangas Grand Terminal", "PITX", 100.0, 286.00);
        Route rAlabangStd = new Route("RT-ALB-S", "Batangas Grand Terminal", "Alabang (Star Mall)", 88.0, 224.00);
        Route rAlabangNdela = new Route("RT-ALB-N", "Batangas Grand Terminal", "Alabang (Star Mall)", 88.0, 259.00);
        Route rLucenaOrd = new Route("RT-LUC-O", "Batangas Grand Terminal", "Lucena", 120.0, 181.00);
        Route rNasugbu = new Route("RT-NAS", "Batangas Grand Terminal", "Nasugbu", 70.0, 189.00);
        Route rCalamba = new Route("RT-CAL", "Batangas Grand Terminal", "Calamba / Turbina", 50.0, 161.00);

        routes.add(rCubaoSkywayCeres);
        routes.add(rCubaoSkywayStd);
        routes.add(rCubaoC5);
        routes.add(rBuendiaCeres);
        routes.add(rBuendiaStd);
        routes.add(rPitxCeres);
        routes.add(rPitxStd);
        routes.add(rAlabangStd);
        routes.add(rAlabangNdela);
        routes.add(rLucenaOrd);
        routes.add(rNasugbu);
        routes.add(rCalamba);

        // Cubao via Skyway: Ceres, Goldstar, DLTB Co.
        buses.add(new RegularBus("BUS-101", "Ceres Transport", rCubaoSkywayCeres, "05:00 AM", 40, false));
        buses.add(new RegularBus("BUS-102", "Goldstar", rCubaoSkywayCeres, "05:30 AM", 40, false));
        buses.add(new RegularBus("BUS-103", "DLTB Co.", rCubaoSkywayStd, "06:00 AM", 40, false));

        // Cubao via C-5: ALPS The Bus, JAM Liner
        buses.add(new RegularBus("BUS-104", "ALPS The Bus", rCubaoC5, "06:30 AM", 40, false));
        buses.add(new RegularBus("BUS-105", "JAM Liner", rCubaoC5, "07:00 AM", 40, false));

        // LRT Buendia
        buses.add(new RegularBus("BUS-201", "Ceres / Goldstar", rBuendiaCeres, "07:30 AM", 40, false));
        buses.add(new RegularBus("BUS-202", "DLTB Co.", rBuendiaStd, "08:00 AM", 40, false));
        buses.add(new RegularBus("BUS-203", "ALPS The Bus", rBuendiaStd, "08:30 AM", 40, false));
        buses.add(new RegularBus("BUS-204", "JAM Liner", rBuendiaStd, "09:00 AM", 40, false));

        // PITX
        buses.add(new RegularBus("BUS-301", "ALPS The Bus", rPitxStd, "09:30 AM", 40, false));
        buses.add(new RegularBus("BUS-302", "JAM Liner", rPitxStd, "10:00 AM", 40, false));
        buses.add(new RegularBus("BUS-303", "Ceres / Goldstar", rPitxCeres, "10:30 AM", 40, false));

        // Alabang (Star Mall)
        buses.add(new RegularBus("BUS-401", "Ceres / Goldstar", rAlabangStd, "11:00 AM", 40, false));
        buses.add(new RegularBus("BUS-402", "RRCG", rAlabangStd, "11:30 AM", 40, false));
        buses.add(new RegularBus("BUS-403", "N. Dela Rosa", rAlabangNdela, "12:00 PM", 40, false));

        // Provincial & Regional Routes
        buses.add(new RegularBus("BUS-501", "Supreme", rLucenaOrd, "01:00 PM", 40, false));
        buses.add(new DeluxeBus("BUS-601", "BSC Nasugbu", rNasugbu, "02:00 PM", 40, false));
        buses.add(new RegularBus("BUS-701", "JAPS Transport", rCalamba, "03:00 PM", 40, false));
    }

    public ArrayList<String> getAvailableDestinations() {
        ArrayList<String> destinations = new ArrayList<>();
        for (Route route : routes) {
            if (!destinations.contains(route.getDestination())) {
                destinations.add(route.getDestination());
            }
        }
        return destinations;
    }

    public ArrayList<Bus> getBusesByDestination(String destination) {
        ArrayList<Bus> matchingBuses = new ArrayList<>();
        for (Bus bus : buses) {
            if (bus.getRoute().getDestination().equalsIgnoreCase(destination)) {
                matchingBuses.add(bus);
            }
        }
        return matchingBuses;
    }

    public void displayBuses(ArrayList<Bus> busList) {
        System.out.println("\n============================================================================================================");
        System.out.printf("%-8s | %-18s | %-25s | %-9s | %-10s | %-13s%n",
                "BUS ID", "BUS LINER", "DESTINATION", "DEPARTURE", "FARE", "AVAIL/RES/OCC");
        System.out.println("============================================================================================================");
        for (Bus bus : busList) {
            double regularFare = bus.getRoute().getBaseFare() + bus.calculateTierSurcharge();
            String seatCounts = String.format("%d / %d / %d",
                    bus.countByStatus(Seat.AVAILABLE),
                    bus.countByStatus(Seat.RESERVED),
                    bus.countByStatus(Seat.OCCUPIED));

            System.out.printf("%-8s | %-18s | %-25s | %-9s | PHP %-6.2f | %-13s%n",
                    bus.getBusId(),
                    bus.getBusLiner(),
                    bus.getRoute().getDestination(),
                    bus.getDepartureTime(),
                    regularFare,
                    seatCounts);
        }
        System.out.println("============================================================================================================");
    }

    public ArrayList<Bus> getAllBuses() { return buses; }

    public Bus findBusById(String busId) {
        if (busId == null) return null;
        for (Bus bus : buses) {
            if (bus.getBusId().equalsIgnoreCase(busId.trim())) return bus;
        }
        return null;
    }

    public Ticket findTicketById(String ticketId) {
        if (ticketId == null) return null;
        for (Ticket ticket : tickets) {
            if (ticket.getTicketId().equalsIgnoreCase(ticketId.trim())) return ticket;
        }
        return null;
    }

    public Ticket createBooking(String busId, int seatNumber, Passenger passenger, boolean payImmediately) throws InvalidBookingException {
        Bus bus = findBusById(busId);
        if (bus == null) throw new InvalidBookingException("Bus ID '" + busId + "' does not exist.");
        bus.bookSeat(seatNumber, passenger, payImmediately);
        Ticket ticket = new Ticket(passenger, bus, seatNumber, payImmediately);
        tickets.add(ticket);
        return ticket;
    }

    public Ticket confirmReservedTicket(String ticketId) throws InvalidBookingException {
        Ticket ticket = findTicketById(ticketId);
        if (ticket == null) throw new InvalidBookingException("Ticket ID '" + ticketId + "' was not found.");
        Seat seat = ticket.getBus().getSeat(ticket.getSeatNumber());
        if (seat.isOccupied()) throw new InvalidBookingException("Ticket " + ticket.getTicketId() + " is already paid.");
        if (!seat.isReserved()) throw new InvalidBookingException("Seat #" + ticket.getSeatNumber() + " is not RESERVED.");
        seat.confirmPayment();
        ticket.markAsPaid();
        return ticket;
    }

    public void cancelTicket(String ticketId) throws InvalidBookingException {
        Ticket ticket = findTicketById(ticketId);
        if (ticket == null) throw new InvalidBookingException("Ticket ID '" + ticketId + "' was not found.");
        ticket.getBus().getSeat(ticket.getSeatNumber()).release();
        tickets.remove(ticket);
    }

    public void displayAllTickets() {
        if (tickets.isEmpty()) {
            System.out.println("\nNo reservations or tickets have been recorded yet.");
            return;
        }
        double collectedRevenue = 0.0, pendingRevenue = 0.0;
        System.out.println("\n--- ISSUED TICKETS & RESERVATIONS ---");
        for (Ticket t : tickets) {
            System.out.printf("%s | %-15s | %-14s | %-10s | %s -> %s | Seat #%02d | PHP %.2f | %s%n",
                    t.getTicketId(), t.getPassenger().getName(), t.getBus().getBusLiner(),
                    t.getPassenger().getPassengerType(), t.getBus().getRoute().getOrigin(),
                    t.getBus().getRoute().getDestination(), t.getSeatNumber(), t.calculateTotalFare(), t.getPaymentStatus());
            if (t.getPaymentStatus().startsWith("PAID")) collectedRevenue += t.calculateTotalFare();
            else pendingRevenue += t.calculateTotalFare();
        }
        System.out.printf("Total Active Tickets: %d | Collected: PHP %.2f | Pending: PHP %.2f%n", tickets.size(), collectedRevenue, pendingRevenue);
    }
}