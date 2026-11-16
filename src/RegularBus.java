public class RegularBus extends Bus {
    public RegularBus(String busId, String busLiner, Route route, String departureTime, int totalSeats, boolean hasAircon) {
        super(busId, busLiner, route, departureTime, totalSeats, hasAircon);
    }

    @Override
    public double calculateTierSurcharge() {
        return hasAircon ? 25.00 : 0.00;
    }
}