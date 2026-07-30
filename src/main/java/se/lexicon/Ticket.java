package se.lexicon;

import java.time.Duration;
import java.time.LocalDateTime;

public class Ticket {
    private final int spotNumber;
    private final String carRegistration;
    private final LocalDateTime arrivalTime;
    private LocalDateTime departureTime;

    public Ticket(int spotNumber, String carRegistration) {
        // Step 1: Store which spot and car this ticket belongs to.
        this.spotNumber = spotNumber;
        this.carRegistration = carRegistration;

        // Step 2: Record the arrival time when the ticket is created.
        this.arrivalTime = LocalDateTime.now();
    }

    public void close() {
        // Step 1: Record the leaving time.
        departureTime = LocalDateTime.now();
    }

    public Duration getDuration() {
        // Step 1: Compare arrival with departure.
        return Duration.between(arrivalTime, departureTime);
    }

    public void printDuration() {
        // Step 1: Convert duration to seconds for a simple demo.
        long seconds = getDuration().toSeconds();

        // Step 2: Print the parked time.
        System.out.println("Parked for " + seconds + " second(s).");
    }

    public int getSpotNumber() {
        return spotNumber;
    }

    public String getCarRegistration() {
        return carRegistration;
    }
}