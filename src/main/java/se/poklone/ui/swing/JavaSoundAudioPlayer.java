package se.poklone.ui.swing;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

final class JavaSoundAudioPlayer implements AudioPlayer {

    private static final float MUSIC_GAIN_DECIBELS = -15.0f;
    private static final float EFFECT_GAIN_DECIBELS = -7.0f;

    private final ExecutorService audioExecutor = Executors.newSingleThreadExecutor(task -> {
        Thread thread = new Thread(task, "poklone-audio");
        thread.setDaemon(true);
        return thread;
    });
    private final AtomicBoolean warningShown = new AtomicBoolean();

    private volatile boolean muted;
    private volatile boolean closed;
    private Clip musicClip;
    private Clip effectClip;
    private MusicTrack currentTrack;

    @Override
    public void play(SoundEffect effect) {
        Objects.requireNonNull(effect, "Sound effect must not be null");
        if (muted || closed) {
            return;
        }
        submit(() -> {
            closeClip(effectClip);
            effectClip = openClip(effect.resourcePath(), EFFECT_GAIN_DECIBELS);
            if (effectClip != null && !muted) {
                effectClip.start();
            }
        });
    }

    @Override
    public void loop(MusicTrack track) {
        Objects.requireNonNull(track, "Music track must not be null");
        if (muted || closed) {
            return;
        }
        submit(() -> {
            if (track == currentTrack && musicClip != null && musicClip.isRunning()) {
                return;
            }
            closeMusic();
            musicClip = openClip(track.resourcePath(), MUSIC_GAIN_DECIBELS);
            if (musicClip != null && !muted) {
                currentTrack = track;
                musicClip.loop(Clip.LOOP_CONTINUOUSLY);
            }
        });
    }

    @Override
    public void stopMusic() {
        submit(this::closeMusic);
    }

    @Override
    public boolean muted() {
        return muted;
    }

    @Override
    public void setMuted(boolean muted) {
        this.muted = muted;
        if (muted) {
            submit(() -> {
                closeMusic();
                closeClip(effectClip);
                effectClip = null;
            });
        }
    }

    @Override
    public void close() {
        if (closed) {
            return;
        }
        closed = true;
        submit(() -> {
            closeMusic();
            closeClip(effectClip);
            effectClip = null;
        });
        audioExecutor.shutdown();
    }

    private Clip openClip(String resourcePath, float gainDecibels) {
        try (InputStream resource = JavaSoundAudioPlayer.class.getResourceAsStream(resourcePath)) {
            if (resource == null) {
                throw new IOException("Missing audio resource: " + resourcePath);
            }
            try (BufferedInputStream buffered = new BufferedInputStream(resource);
                 AudioInputStream audio = AudioSystem.getAudioInputStream(buffered)) {
                Clip clip = AudioSystem.getClip();
                clip.open(audio);
                applyGain(clip, gainDecibels);
                return clip;
            }
        } catch (IOException | LineUnavailableException | UnsupportedAudioFileException exception) {
            if (warningShown.compareAndSet(false, true)) {
                System.err.println("Poklone audio unavailable: " + exception.getMessage());
            }
            return null;
        }
    }

    private void submit(Runnable task) {
        try {
            audioExecutor.execute(task);
        } catch (RejectedExecutionException ignored) {
            // Closing the game can race with a final UI event.
        }
    }

    private void closeMusic() {
        closeClip(musicClip);
        musicClip = null;
        currentTrack = null;
    }

    private static void applyGain(Clip clip, float gainDecibels) {
        if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            gain.setValue(Math.max(gain.getMinimum(), Math.min(gain.getMaximum(), gainDecibels)));
        }
    }

    private static void closeClip(Clip clip) {
        if (clip != null) {
            clip.stop();
            clip.close();
        }
    }
}
