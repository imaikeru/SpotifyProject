/**
 *
 */
package bg.sofia.uni.fmi.mjt.spotify.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

import bg.sofia.uni.fmi.mjt.spotify.client.constants.Constants;

public class SpotifyClient {
    private final ByteBuffer writeBuffer;
    private final AtomicBoolean runClient;
    private final AtomicBoolean isAudioLoggedIn;

    public SpotifyClient() {
        this.writeBuffer = ByteBuffer.allocate(Constants.BUFFER_SIZE);
        this.runClient = new AtomicBoolean(true);
        this.isAudioLoggedIn = new AtomicBoolean(false);
    }

    public static void main(final String[] args) {
        final SpotifyClient client = new SpotifyClient();
        client.start();
    }

    // TODO
    // Vtora konekciq (socketChannel) kym survura, momenta v koito se login-na,
    // trqbva da izpratq suobshtenie do survura, v koeto da kynektna sus sushtiq
    // email, da asociiram vtoriq kanal s purviq v map i taka survura shte moje
    // da prashta tekst kym pyrviq socket i muzika kym vtoriq
    //

    public void start() {
        try (SocketChannel messageSocketChannel = SocketChannel.open();
                SocketChannel audioSocketChannel = SocketChannel.open();
                Scanner scanner = new Scanner(System.in)) {

            messageSocketChannel.connect(new InetSocketAddress(Constants.SERVER_HOST, Constants.SERVER_PORT));
            messageSocketChannel.configureBlocking(true);
            audioSocketChannel.connect(new InetSocketAddress(Constants.SERVER_HOST, Constants.SERVER_PORT));
            audioSocketChannel.configureBlocking(true);

            final ResponseReaderThread responseReader = new ResponseReaderThread(messageSocketChannel,
                    audioSocketChannel, runClient, isAudioLoggedIn);
            responseReader.setDaemon(true);
            responseReader.start();

            while (this.runClient.get() && messageSocketChannel.isConnected()) {
                final String messageToServer = scanner.nextLine();
                final boolean hasSentRequest = sendToServer(messageSocketChannel, messageToServer);
                final String[] messageSplitByWhitespaces = messageToServer.split(Constants.WHITESPACE_DELIMITER);
                final int commandIndex = 0;
                if (messageSplitByWhitespaces[commandIndex].equalsIgnoreCase(Constants.LOGIN_COMMAND)
                        && !isAudioLoggedIn.get()) {
                    messageSplitByWhitespaces[commandIndex] = Constants.AUDIO_LOGIN_COMMAND;
                    final String audioLoginMessageToServer = String.join(Constants.SPACE, messageSplitByWhitespaces);
                    sendToServer(audioSocketChannel, audioLoginMessageToServer);
                }

                if (messageToServer.equalsIgnoreCase(Constants.DISCONNECT_COMMAND)) {
                    this.stop();
                }
            }
        } catch (final IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private boolean sendToServer(final SocketChannel socketChannel, final String messageToServer) {
        writeBuffer.clear();
        writeBuffer.put(messageToServer.getBytes());
        writeBuffer.flip();
        try {
            socketChannel.write(writeBuffer);
            return true;
        } catch (final IOException e) {
            return false;
        }
    }

    private void stop() {
        runClient.set(false);
    }
}
