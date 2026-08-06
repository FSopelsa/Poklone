package se.poklone.ui.swing;

import java.util.ArrayList;
import java.util.List;

final class RecordingAudioPlayer implements AudioPlayer {

    private final List<SoundEffect> effects = new ArrayList<>();
    private final List<MusicTrack> tracks = new ArrayList<>();
    private boolean muted;
    private int musicStops;

    @Override
    public void play(SoundEffect effect) {
        if (!muted) {
            effects.add(effect);
        }
    }

    @Override
    public void loop(MusicTrack track) {
        if (!muted) {
            tracks.add(track);
        }
    }

    @Override
    public void stopMusic() {
        musicStops++;
    }

    @Override
    public boolean muted() {
        return muted;
    }

    @Override
    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    List<SoundEffect> effects() {
        return List.copyOf(effects);
    }

    List<MusicTrack> tracks() {
        return List.copyOf(tracks);
    }

    int musicStops() {
        return musicStops;
    }
}
