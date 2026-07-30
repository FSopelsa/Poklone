package se.lexicon;

public class Attendant {
    private final String name;

    public Attendant(String name) {
        // Step 1: Validate the attendant name.
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be blank.");
        }

        // Step 2: Store the cleaned name.
        this.name = name.trim();
    }

    public Ticket parkCar(ParkingLot parkingLot, String carRegistration) {
        // Step 1: Ask the parking lot for a free spot.
        ParkingSpot freeSpot = parkingLot.findFreeSpot();

        // Step 2: Stop if the lot is full.
        if (freeSpot == null) {
            System.out.println("No free spots available for car " + carRegistration + ".");
            return null;
        }

        // Step 3: Mark the spot as occupied.
        freeSpot.park(carRegistration);

        // Step 4: Create the ticket for this parking session.
        Ticket ticket = new Ticket(freeSpot.getSpotNumber(), carRegistration);

        // Step 5: Print the assignment.
        System.out.println(name + " assigned car " + carRegistration
                + " to spot " + freeSpot.getSpotNumber() + ".");

        return ticket;
    }

    public void leaveCar(ParkingLot parkingLot, Ticket ticket) {
        // Step 1: Handle missing tickets safely.
        if (ticket == null) {
            System.out.println("No ticket to close.");
            return;
        }

        // Step 2: Close the ticket and calculate duration.
        ticket.close();

        // Step 3: Find and free the original spot.
        ParkingSpot spot = parkingLot.findSpotByNumber(ticket.getSpotNumber());
        spot.free();

        // Step 4: Print the result.
        System.out.print(name + " closed ticket for car "
                + ticket.getCarRegistration() + ". ");
        ticket.printDuration();
        System.out.println("Spot " + ticket.getSpotNumber() + " is free again.");
    }
}