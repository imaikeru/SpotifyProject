package bg.sofia.uni.fmi.mjt.spotify.client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import bg.sofia.uni.fmi.mjt.spotify.client.constants.Constants;
import bg.sofia.uni.fmi.mjt.spotify.client.exceptions.InvalidAudioFormatException;
import bg.sofia.uni.fmi.mjt.spotify.client.utils.ResponseValidator;

public class SpotifyPlayer extends Thread {
    private final SocketChannel audioSocketChannel;
    private final ByteBuffer readBuffer;
    private final AtomicBoolean runClient;
    private final AtomicBoolean playSong;

    public SpotifyPlayer(final SocketChannel audioSocketChannel, final AtomicBoolean runClient,
            final AtomicBoolean playSong) {
        this.audioSocketChannel = audioSocketChannel;
        this.runClient = runClient;
        this.playSong = playSong;
        this.readBuffer = ByteBuffer.allocate(Constants.BUFFER_SIZE);
    }

    @Override
    public void run() {
        while (runClient.get()) {
            try {
                readBuffer.clear();
                if (audioSocketChannel.isConnected()) {
                    audioSocketChannel.read(readBuffer);
                } else {
                    break;
                }
                if (readBuffer.position() > 0) {
                    readBuffer.flip();
                    final String messageFromServer = new String(readBuffer.array(), 0, readBuffer.limit());
                    try {
                        final AudioFormat audioFormat = ResponseValidator.extractValidAudioFormat(messageFromServer);
                        playMusic(audioFormat);

                    } catch (final InvalidAudioFormatException e) {
//                        System.out.println(e.getMessage());
                    }
                } else {
                    Thread.sleep(500);
                }
            } catch (final IOException | InterruptedException e) {
                runClient.set(false);
                System.out.println(e.getMessage());
            } catch (final LineUnavailableException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void playMusic(final AudioFormat audioFormat) throws LineUnavailableException, IOException {
        final DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);

        final SourceDataLine dataLine = (SourceDataLine) AudioSystem.getLine(info);
        dataLine.open();
        dataLine.start();
        playSong.set(true);
        while (runClient.get() && playSong.get()) {
            readBuffer.clear();
            audioSocketChannel.read(readBuffer);
            if (readBuffer.position() > 0) {
                readBuffer.flip();
                dataLine.write(readBuffer.array(), 0, readBuffer.limit());
            } else {
                break;
            }
        }
        dataLine.flush();
        dataLine.close();
    }

}
