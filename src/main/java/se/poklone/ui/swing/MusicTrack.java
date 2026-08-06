package se.poklone.ui.swing;

enum MusicTrack {
    WORLD("/audio/music/safe-space.wav");

    private final String resourcePath;

    MusicTrack(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    String resourcePath() {
        return resourcePath;
    }
}
