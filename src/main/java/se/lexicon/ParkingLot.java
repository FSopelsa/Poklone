package se.lexicon;

import java.util.ArrayList;
import java.util.List;

public class ParkingLot {
    private final List<ParkingSpot> spots = new ArrayList<>();

    public ParkingLot(int capacity) {
        // Step 1: Validate the lot size.
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than 0.");
        }

        // Step 2: Create the fixed parking spots.
        for (int i = 1; i <= capacity; i++) {
            spots.add(new ParkingSpot(i));
        }
    }

    public ParkingSpot findFreeSpot() {
        // Step 1: Search spots from first to last.
        for (ParkingSpot spot : spots) {
            if (spot.isFree()) {
                return spot;
            }
        }

        // Step 2: Return null when all spots are occupied.
        return null;
    }

    public ParkingSpot findSpotByNumber(int spotNumber) {
        // Step 1: Search for the matching spot number.
        for (ParkingSpot spot : spots) {
            if (spot.getSpotNumber() == spotNumber) {
                return spot;
            }
        }

        // Step 2: Fail clearly if the ticket has an invalid spot.
        throw new IllegalArgumentException("Spot does not exist: " + spotNumber);
    }

    public void printStatus() {
        // Step 1: Print every spot state.
        System.out.println();
        System.out.println("Parking lot status:");
        for (ParkingSpot spot : spots) {
            spot.printStatus();
        }
    }
}