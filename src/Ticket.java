import java.util.UUID;

public class Ticket {
    private String ticketId;
    private Passenger passenger;
    private Bus bus;
    private int seatNumber;
    private boolean isPaid;

    public Ticket(Passenger passenger, Bus bus, int seatNumber, boolean isPaid) {
        this.ticketId = "TKT-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        this.passenger = passenger;
        this.bus = bus;
        this.seatNumber = seatNumber;
        this.isPaid = isPaid;
    }

    public String getTicketId() { return ticketId; }
    public Passenger getPassenger() { return passenger; }
    public Bus getBus() { return bus; }
    public int getSeatNumber() { return seatNumber; }
    public String getPaymentStatus() { return isPaid ? "PAID" : "PENDING (RESERVED)"; }
    
    public void markAsPaid() { this.isPaid = true; }

    public double calculateTotalFare() {
        double baseFare = bus.getRoute().getBaseFare();
        double surcharge = bus.calculateTierSurcharge();
        double total = baseFare + surcharge;

        switch (passenger.getPassengerType()) {
            case "STUDENT":
            case "SENIOR/PWD":
                return total * 0.80; 
            case "CHILD":
                return total * 0.50; 
            default:
                return total; 
        }
    }

    public String generateReceipt() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n========================================\n");
        sb.append("           OFFICIAL TICKET              \n");
        sb.append("========================================\n");
        sb.append("Ticket ID   : ").append(ticketId).append("\n");
        sb.append("Passenger   : ").append(passenger.getName()).append(" (").append(passenger.getPassengerType()).append(")\n");
        sb.append("Bus Liner   : ").append(bus.getBusLiner()).append("\n");
        sb.append("Route       : ").append(bus.getRoute().getOrigin()).append(" to ").append(bus.getRoute().getDestination()).append("\n");
        sb.append("Seat Number : ").append(seatNumber).append("\n");
        sb.append("Total Fare  : PHP ").append(String.format("%.2f", calculateTotalFare())).append("\n");
        sb.append("Status      : ").append(getPaymentStatus()).append("\n");
        sb.append("========================================");
        return sb.toString();
    }
}