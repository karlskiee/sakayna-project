public class DeluxeBus extends Bus {
    public DeluxeBus(String busId, String busLiner, Route route, String departureTime, int totalSeats, boolean hasAircon) {
        super(busId, busLiner, route, departureTime, totalSeats, hasAircon);
    }

    @Override
    public double calculateTierSurcharge() {
        return 150.00; 
    }
}