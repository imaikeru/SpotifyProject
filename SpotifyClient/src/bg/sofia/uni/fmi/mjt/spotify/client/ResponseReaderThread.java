package bg.sofia.uni.fmi.mjt.spotify.client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicBoolean;

import bg.sofia.uni.fmi.mjt.spotify.client.constants.Constants;

public class ResponseReaderThread extends Thread {

    private final SocketChannel messageSocketChannel;
    private final SocketChannel audioSocketChannel;
    private final ByteBuffer readBuffer;
    private final AtomicBoolean runClient;
    private final AtomicBoolean isAudioLoggedIn;
    private final AtomicBoolean playSong;

    public ResponseReaderThread(final SocketChannel messageSocketChannel, final SocketChannel audioSocketChannel,
            final AtomicBoolean runClient, final AtomicBoolean isAudioLoggedIn) {
        this.readBuffer = ByteBuffer.allocate(Constants.BUFFER_SIZE);
        this.messageSocketChannel = messageSocketChannel;
        this.audioSocketChannel = audioSocketChannel;
        this.runClient = runClient;
        this.isAudioLoggedIn = isAudioLoggedIn;
        this.playSong = new AtomicBoolean(false);
    }

    @Override
    public void run() {
        while (runClient.get()) {
            try {
                readBuffer.clear();
                if (messageSocketChannel.isConnected()) {
                    messageSocketChannel.read(readBuffer);
                } else {
                    break;
                }
                if (this.readBuffer.position() > 0) {
                    final String messageFromServer = printMessageFromServerToConsole();
                    if (messageFromServer.contains(Constants.SUCCESSFUL_AUDIO_LOGIN_MESSAGE)) {
                        isAudioLoggedIn.set(true);
                        final SpotifyPlayer spotifyPlayer = new SpotifyPlayer(audioSocketChannel, runClient, playSong);
                        spotifyPlayer.setDaemon(true);
                        spotifyPlayer.start();
                    }
                    if (messageFromServer.contains(Constants.STOPPED_SONG_MESSAGE)) {
                        playSong.set(false);
                    }
                } else {
                    Thread.sleep(500);
                }
            } catch (final IOException | InterruptedException e) {
                runClient.set(false);
                e.printStackTrace();
            }
        }
        final String finished = "Finised daemon.";
        System.out.println(finished);
    }

    public String printMessageFromServerToConsole() {
        readBuffer.flip();
        final String messageFromServer = new String(readBuffer.array(), 0, readBuffer.limit());
        System.out.println(messageFromServer);
        return messageFromServer;
    }
}
