package se.lexicon;

public class ParkingSpot {
    private final int spotNumber;
    private String carRegistration;

    public ParkingSpot(int spotNumber) {
        // Step 1: Validate the spot number.
        if (spotNumber <= 0) {
            throw new IllegalArgumentException("Spot number must be greater than 0.");
        }

        // Step 2: Store the permanent spot number.
        this.spotNumber = spotNumber;
    }

    public int getSpotNumber() {
        return spotNumber;
    }

    public boolean isFree() {
        // Step 1: A null registration means no car is parked here.
        return carRegistration == null;
    }

    public void park(String carRegistration) {
        // Step 1: Do not park a car in an occupied spot.
        if (!isFree()) {
            throw new IllegalStateException("Spot " + spotNumber + " is already occupied.");
        }

        // Step 2: Store the car registration.
        this.carRegistration = carRegistration;
    }

    public void free() {
        // Step 1: Remove the car from the spot.
        carRegistration = null;
    }

    public void printStatus() {
        // Step 1: Print free or occupied status.
        if (isFree()) {
            System.out.println("Spot " + spotNumber + ": free");
        } else {
            System.out.println("Spot " + spotNumber + ": occupied by " + carRegistration);
        }
    }
}