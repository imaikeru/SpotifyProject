package bg.sofia.uni.fmi.mjt.spotify.server;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import bg.sofia.uni.fmi.mjt.spotify.server.constants.CommonConstants;
import bg.sofia.uni.fmi.mjt.spotify.server.constants.Messages;

public class MusicStreamer extends Thread {

    private final User user;
    private final String songName;
    private final ByteBuffer buffer;

    public MusicStreamer(final User user, final String songName) {
        this.user = user;
        this.songName = songName;
        this.buffer = ByteBuffer.allocate(CommonConstants.BUFFER_SIZE);
    }

    @Override
    public void run() {
        try {
            final AudioInputStream stream = AudioSystem.getAudioInputStream(
                    new File(CommonConstants.SONG_DIRECTORY + songName + CommonConstants.SONG_FORMAT));
            final AudioFormat audioFormat = stream.getFormat();
            final String audioFormatString = audioFormatToString(audioFormat);
            user.setSongPlayed(songName);

            buffer.clear();
            buffer.put(audioFormatString.getBytes());
            buffer.flip();
            user.getAudioSocketChannel().write(buffer);

            user.setListeningToMusic(true);
            final byte[] streamBuffer = new byte[CommonConstants.BUFFER_SIZE];
            int bytesReadFromStream = 0;
            int counter = 0;
            while (user.isListeningToMusic()
                    && (bytesReadFromStream = stream.read(streamBuffer, 0, CommonConstants.BUFFER_SIZE)) != -1) {
                counter++;
                buffer.clear();
                buffer.put(streamBuffer);
                buffer.flip();

                /*
                 * There appears to be some kind of problem with java.nio, if the thread is not
                 * put to sleep, it overflows the client with information and blocks the
                 * opposite listening socket. Checking if counter is divisible by 200 and then
                 * Putting the thread to sleep appears to be a better working alternative than
                 * putting it to sleep every 3-4 ms, since there is no audio stuttering or
                 * skipped audio fragments. MAGIC NUMBERS - 200 and 1050 were chosen by
                 * try-failure and appear to be working just fine.
                 */
                if (counter % 200 == 0) {
                    try {
                        Thread.sleep(1050);
                    } catch (final InterruptedException e) {
                        System.out.println(e.getMessage());
                    }
                }
                final int bytesSent = user.getAudioSocketChannel().write(buffer);
            }
            if (user.isListeningToMusic()) {
                final String stoppedSongMessage = Messages.STOPPED_SONG + user.getSongPlayed();
                user.setListeningToMusic(false);
                buffer.clear();
                buffer.put(stoppedSongMessage.getBytes());
                buffer.flip();
                user.getMessageSocketChannel().write(buffer);
                user.setSongPlayed(null);
            }
        } catch (UnsupportedAudioFileException | IOException e1) {
            System.out.println(e1.getMessage());
        }
    }

    private static String audioFormatToString(final AudioFormat audioFormat) {
        final String encoding = audioFormat.getEncoding().toString();
        final float sampleRate = audioFormat.getSampleRate();
        final int sampleSizeInBits = audioFormat.getSampleSizeInBits();
        final int channels = audioFormat.getChannels();
        final int frameSize = audioFormat.getFrameSize();
        final float frameRate = audioFormat.getFrameRate();
        final boolean bigEndian = audioFormat.isBigEndian();

        return new StringBuilder().append(encoding).append(CommonConstants.SPACE).append(sampleRate)
                .append(CommonConstants.SPACE).append(sampleSizeInBits).append(CommonConstants.SPACE).append(channels)
                .append(CommonConstants.SPACE).append(frameSize).append(CommonConstants.SPACE).append(frameRate)
                .append(CommonConstants.SPACE).append(bigEndian).toString();
    }
}
