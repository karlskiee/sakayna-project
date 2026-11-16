import java.util.ArrayList;

public abstract class Bus {
    protected String busId;
    protected String busLiner;
    protected Route route;
    protected String departureTime;
    protected ArrayList<Seat> seats;
    protected int totalSeats;
    protected boolean hasAircon;

    public Bus(String busId, String busLiner, Route route, String departureTime, int totalSeats, boolean hasAircon) {
        this.busId = busId;
        this.busLiner = busLiner;
        this.route = route;
        this.departureTime = departureTime;
        this.totalSeats = totalSeats;
        this.hasAircon = hasAircon;
        this.seats = new ArrayList<>();
        for (int i = 1; i <= totalSeats; i++) {
            seats.add(new Seat(i));
        }
    }

    public String getBusId() { return busId; }
    public String getBusLiner() { return busLiner; }
    public Route getRoute() { return route; }
    public String getDepartureTime() { return departureTime; }
    public int getTotalSeats() { return totalSeats; }

    public Seat getSeat(int seatNumber) throws InvalidBookingException {
        if (seatNumber < 1 || seatNumber > totalSeats) {
            throw new InvalidBookingException("Seat number out of bounds.");
        }
        return seats.get(seatNumber - 1);
    }

    public int countByStatus(String status) {
        int count = 0;
        for (Seat seat : seats) {
            if (seat.getStatus().equals(status)) count++;
        }
        return count;
    }

    public void bookSeat(int seatNumber, Passenger passenger, boolean payImmediately) throws InvalidBookingException {
        Seat seat = getSeat(seatNumber);
        if (!seat.isAvailable()) {
            throw new InvalidBookingException("Seat #" + seatNumber + " is already taken.");
        }
        if (payImmediately) seat.occupy(passenger);
        else seat.reserve(passenger);
    }

    public void displaySeatMap() {
        System.out.println("\n--- SEAT MAP FOR " + busId + " (" + busLiner + ") ---");
        System.out.println("[XX] = Occupied | [RS] = Reserved | [01] = Available");
        for (int i = 0; i < seats.size(); i++) {
            Seat s = seats.get(i);
            String display = String.format("[%02d]", s.getSeatNumber());
            if (s.isOccupied()) display = "[XX]";
            else if (s.isReserved()) display = "[RS]";
            
            System.out.print(display + " ");
            if ((i + 1) % 4 == 0) System.out.println(); 
        }
        System.out.println();
    }

    public abstract double calculateTierSurcharge();
}