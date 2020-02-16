package bg.sofia.uni.fmi.mjt.spotify.server;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicBoolean;

public class User {
    private final SocketChannel messageSocketChannel;
    private SocketChannel audioSocketChannel;
    private final AtomicBoolean listeningToMusic;
    private String songPlayed;

    public User(final SocketChannel messageSocketChannel) {
        this.messageSocketChannel = messageSocketChannel;
        this.audioSocketChannel = null;
        this.listeningToMusic = new AtomicBoolean(false);
    }

    public SocketChannel getMessageSocketChannel() {
        return messageSocketChannel;
    }

    public SocketChannel getAudioSocketChannel() {
        return audioSocketChannel;
    }

    public void setAudioSocketChannel(final SocketChannel audioSocketChannel) {
        this.audioSocketChannel = audioSocketChannel;
    }

    public boolean isListeningToMusic() {
        return listeningToMusic.get();
    }

    public boolean hasOpenAudioConnection() {
        return audioSocketChannel != null;
    }

    public void setListeningToMusic(final boolean value) {
        this.listeningToMusic.set(value);
    }

    public void setSongPlayed(final String songPlayed) {
        this.songPlayed = songPlayed;
    }

    public String getSongPlayed() {
        return songPlayed;
    }

    public void close() {
        try {
            if (messageSocketChannel.isOpen()) {
                messageSocketChannel.close();
            }
            try {
                if (audioSocketChannel.isOpen()) {
                    audioSocketChannel.close();
                }
            } catch (final IOException e) {
                // TODO
            }
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
