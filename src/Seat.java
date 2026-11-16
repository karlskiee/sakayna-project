public class Seat {
    public static final String AVAILABLE = "AVAILABLE";
    public static final String RESERVED = "RESERVED";
    public static final String OCCUPIED = "OCCUPIED";

    private int seatNumber;
    private String status;
    private Passenger passenger;

    public Seat(int seatNumber) {
        this.seatNumber = seatNumber;
        this.status = AVAILABLE;
        this.passenger = null;
    }

    public boolean isAvailable() {
        return AVAILABLE.equals(status);
    }

    public boolean isReserved() {
        return RESERVED.equals(status);
    }

    public boolean isOccupied() {
        return OCCUPIED.equals(status);
    }

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

    public int getSeatNumber() {
        return seatNumber;
    }

    public String getStatus() {
        return status;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public String getMapSymbol() {
        if (isReserved()) {
            return "[RS]";
        } else if (isOccupied()) {
            return "[OC]";
        }
        return String.format("[%02d]", seatNumber);
    }
}