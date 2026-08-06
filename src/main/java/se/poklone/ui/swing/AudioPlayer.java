package se.poklone.ui.swing;

interface AudioPlayer extends AutoCloseable {

    void play(SoundEffect effect);

    void loop(MusicTrack track);

    void stopMusic();

    boolean muted();

    void setMuted(boolean muted);

    @Override
    default void close() {
    }

    static AudioPlayer systemDefault() {
        return new JavaSoundAudioPlayer();
    }

    static AudioPlayer silent() {
        return new AudioPlayer() {
            private boolean muted;

            @Override
            public void play(SoundEffect effect) {
            }

            @Override
            public void loop(MusicTrack track) {
            }

            @Override
            public void stopMusic() {
            }

            @Override
            public boolean muted() {
                return muted;
            }

            @Override
            public void setMuted(boolean muted) {
                this.muted = muted;
            }
        };
    }
}
