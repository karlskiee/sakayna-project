public class Route {
    private String routeId;
    private String origin;
    private String destination;
    private double distanceKm;
    private double baseFare;

    public Route(String routeId, String origin, String destination, double distanceKm, double baseFare) {
        this.routeId = routeId;
        this.origin = origin;
        this.destination = destination;
        this.distanceKm = distanceKm;
        this.baseFare = baseFare;
    }

    public String getRouteId() {
        return routeId;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public double getBaseFare() {
        return baseFare;
    }

    public String getFormattedRoute() {
        return origin + " -> " + destination + " (" + distanceKm + " km)";
    }
}