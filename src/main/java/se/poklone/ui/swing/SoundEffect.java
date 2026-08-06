package se.poklone.ui.swing;

enum SoundEffect {
    STEP("/audio/sfx/step.wav"),
    BLOCKED("/audio/sfx/blocked.wav"),
    ENCOUNTER("/audio/sfx/encounter.wav"),
    ATTACK("/audio/sfx/attack.wav"),
    SWITCH("/audio/sfx/switch.wav"),
    VICTORY("/audio/sfx/victory.wav"),
    DEFEAT("/audio/sfx/defeat.wav");

    private final String resourcePath;

    SoundEffect(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    String resourcePath() {
        return resourcePath;
    }
}
