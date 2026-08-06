package se.poklone.ui.swing;

import org.junit.jupiter.api.Test;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AudioAssetsTest {

    @Test
    void everyAudioCueReferencesReadableWaveData() throws Exception {
        Stream.concat(
                Stream.of(SoundEffect.values()).map(SoundEffect::resourcePath),
                Stream.of(MusicTrack.values()).map(MusicTrack::resourcePath)
        ).forEach(AudioAssetsTest::assertReadableAudio);
    }

    private static void assertReadableAudio(String resourcePath) {
        try (InputStream resource = AudioAssetsTest.class.getResourceAsStream(resourcePath)) {
            assertNotNull(resource, "Missing audio resource " + resourcePath);
            try (BufferedInputStream buffered = new BufferedInputStream(resource);
                 AudioInputStream audio = AudioSystem.getAudioInputStream(buffered)) {
                assertTrue(audio.getFrameLength() > 0, "Empty audio resource " + resourcePath);
            }
        } catch (Exception exception) {
            throw new AssertionError("Unreadable audio resource " + resourcePath, exception);
        }
    }
}
