public class Passenger {
    private String name;
    private String contactNumber;
    private String passengerType; // "REGULAR", "STUDENT", "SENIOR/PWD", "CHILD"

    public Passenger(String name, String contactNumber, String passengerType) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Passenger name cannot be blank.");
        }
        if (contactNumber == null || contactNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Contact number cannot be blank.");
        }
        this.name = name.trim();
        this.contactNumber = contactNumber.trim();
        this.passengerType = normalizeType(passengerType);
    }

    private String normalizeType(String type) {
        if (type == null) return "REGULAR";
        String upper = type.trim().toUpperCase();
        switch (upper) {
            case "STUDENT":
            case "SENIOR/PWD":
            case "CHILD":
                return upper;
            default:
                return "REGULAR";
        }
    }

    // Applicable fare discount rules per passenger classification
    public double getDiscountRate() {
        switch (passengerType) {
            case "STUDENT":
            case "SENIOR/PWD":
                return 0.20; // 20% statutory discount
            case "CHILD":
                return 0.50; // 50% half-fare rule for children
            default:
                return 0.0;  // Standard regular fare
        }
    }

    public String getName() {
        return name;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getPassengerType() {
        return passengerType;
    }
}