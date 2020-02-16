package bg.sofia.uni.fmi.mjt.spotify.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.sound.sampled.UnsupportedAudioFileException;

import bg.sofia.uni.fmi.mjt.spotify.server.constants.Commands;
import bg.sofia.uni.fmi.mjt.spotify.server.constants.CommonConstants;
import bg.sofia.uni.fmi.mjt.spotify.server.constants.Messages;
import bg.sofia.uni.fmi.mjt.spotify.server.exceptions.IllegalOperationException;
import bg.sofia.uni.fmi.mjt.spotify.server.exceptions.PlaylistNotFoundException;
import bg.sofia.uni.fmi.mjt.spotify.server.exceptions.SongNotFoundException;
import bg.sofia.uni.fmi.mjt.spotify.server.utils.RequestParser;
import bg.sofia.uni.fmi.mjt.spotify.server.utils.RequestValidator;

public final class SpotifyServer implements AutoCloseable {

//    private static final String SONG_DIRECTORY = "resources\\songs\\";
//    private static final String LOGIN_PATH = "resources\\login.txt";
//    private static final String LOG_FILE_PATH = "resources\\logs.txt";
//    private static final String PLAYLIST_DIRECTORY = "resources\\playlists\\";
    private static final int SLEEP_MILIS = 250;

    private final Selector selector;
    private final ServerSocketChannel serverSocketChannel;
    private final ByteBuffer buffer;
    private final Map<String, User> loggedInUsers;
    private final Map<String, Integer> songs;
    private final Set<String> playlistsNames;

    public SpotifyServer(final String hostName, final int port) throws IOException {
        this.selector = Selector.open();
        this.buffer = ByteBuffer.allocate(CommonConstants.BUFFER_SIZE);
        this.serverSocketChannel = ServerSocketChannel.open();
        this.serverSocketChannel.bind(new InetSocketAddress(hostName, port));
        this.loggedInUsers = new HashMap<>();
        this.songs = initializeSongs();
        this.playlistsNames = initializePlaylistsNames();
    }

    private Set<String> initializePlaylistsNames() throws IOException {
        Set<String> playlistsNames = new HashSet<>();
        try (final Stream<Path> walk = Files.walk(Paths.get(CommonConstants.PLAYLIST_DIRECTORY))) {
            playlistsNames = walk.map(x -> x.toString())
                    .filter(playlist -> playlist.endsWith(CommonConstants.TEXT_FORMAT)).map(playlist -> {
                        return playlist.replace(CommonConstants.TEXT_FORMAT, CommonConstants.EMPTY_STRING)
                                .replace(CommonConstants.PLAYLIST_DIRECTORY, CommonConstants.EMPTY_STRING);
                    }).collect(Collectors.toSet());
        }
        return playlistsNames;
    }

    private Map<String, Integer> initializeSongs() throws IOException {
        Map<String, Integer> songs = new HashMap<>();
        try (Stream<Path> walk = Files.walk(Paths.get(CommonConstants.SONG_DIRECTORY))) {
            songs = walk.map(x -> x.toString()).filter(song -> song.endsWith(CommonConstants.SONG_FORMAT)).map(song -> {
                return song.replace(CommonConstants.SONG_FORMAT, CommonConstants.EMPTY_STRING)
                        .replace(CommonConstants.SONG_DIRECTORY, CommonConstants.EMPTY_STRING);
            }).collect(Collectors.toMap(song -> song, song -> 0));
        }
        return songs;
    }

    private static String quote(final String str) {
        return CommonConstants.QUOTATION_MARK + str + CommonConstants.QUOTATION_MARK;
    }

    private static void logExceptionMessageToFile(final String exceptionMessage) {
        try (final var fileWriter = new FileWriter(CommonConstants.LOG_FILE_PATH, true);
                final var printWriter = new PrintWriter(fileWriter)) {
            printWriter.println(exceptionMessage);
        } catch (final IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Method that checks if any news songs have been added to the song directory
     * and adds them.
     *
     * @return The number of new songs added.
     * @throws IOException If I/O error occurs with the song files.
     */
    private long checkForNewSongs() throws IOException {
        long newSongsAdded = 0;
        try (final Stream<Path> walk = Files.walk(Paths.get(CommonConstants.SONG_DIRECTORY))) {
            newSongsAdded = walk.map(x -> x.toString()).filter(song -> song.endsWith(CommonConstants.SONG_FORMAT))
                    .map(song -> {
                        return song.replace(CommonConstants.SONG_FORMAT, CommonConstants.EMPTY_STRING)
                                .replace(CommonConstants.SONG_DIRECTORY, CommonConstants.EMPTY_STRING);
                    }).filter(song -> !songs.containsKey(song)).map(song -> {
                        songs.put(song, 0);
                        return song;
                    }).count();
        }
        return newSongsAdded;
    }

    /**
     * Method that returns the N most popular songs or the number of songs in the
     * directory, if N is larger.
     *
     * @param N The number of desired songs.
     * @return Returns the list containing the names of the most popular songs.
     */
    private List<String> findTopNListenedSongs(final int N) {
        return songs.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .map(song -> quote(song.getKey())).limit(N).collect(Collectors.toList());
    }

    /**
     * Method that returns all songs that contain all the searched words in them.
     *
     * @param searchedWords List containing the searched words. <strong>MUST NOT BE
     *                      NULL</strong>
     * @return Returns the list containing the filtered songs.
     */
    private List<String> filterSongs(final List<String> searchedWords) {
        final List<String> filteredSongs = new ArrayList<>();
        for (final String song : songs.keySet()) {
            final String songToLower = song.toLowerCase();
            if (searchedWords.stream().map(s -> s.toLowerCase()).allMatch(songToLower::contains)) {
                filteredSongs.add(quote(song));
            }
        }
        return filteredSongs;
    }

    /**
     * Method that creates a file representing a playlist with the given name.
     *
     * @param playlistName The name of the new playlist. <strong>MUST NOT BE
     *                     NULL</strong>
     * @return Returns true if the playlist file has successfully been created,
     *         false otherwise.
     * @throws IOException If I/O error occurs with the playlist file.
     */
    private boolean createPlaylist(final String playlistName) throws IOException {
        assert playlistName != null;
        final String playlistNameToLower = playlistName.toLowerCase();
        if (playlistsNames.contains(playlistNameToLower)) {
            return false;
        }
        final File newPlaylist = new File(
                CommonConstants.PLAYLIST_DIRECTORY + playlistNameToLower + CommonConstants.TEXT_FORMAT);
        newPlaylist.createNewFile();
        playlistsNames.add(playlistNameToLower);
        return true;
    }

    /**
     * Method that returns the songs in a playlist.
     *
     * @param playlistName The name of the playlist. <strong>MUST NOT BE
     *                     NULL</strong>
     * @return Returns a String containing the name of the playlist and all the
     *         songs in it.
     * @throws IOException               If I/O error occurs with the playlist file.
     * @throws PlaylistNotFoundException If playlist does not exist.
     *
     */
    private String showPlaylist(final String playlistName) throws IOException {
        assert playlistName != null;
        final String playlistNameToLower = playlistName.toLowerCase();
        if (playlistsNames.contains(playlistNameToLower)) {
            final List<String> songs = new ArrayList<>();

            songs.add(CommonConstants.PLAYLIST + quote(playlistName) + CommonConstants.COLON);
            try (var fileInputStream = new FileInputStream(
                    CommonConstants.PLAYLIST_DIRECTORY + playlistNameToLower + CommonConstants.TEXT_FORMAT);
                    var inputStreamReader = new InputStreamReader(fileInputStream);
                    var bufferedReader = new BufferedReader(inputStreamReader)) {
                String song;
                while ((song = bufferedReader.readLine()) != null) {
                    songs.add(quote(song));
                }
                return String.join(CommonConstants.NEW_LINE, songs);
            }
        }
        throw new PlaylistNotFoundException(Messages.PLAYLIST_NOT_FOUND + playlistName);
    }

    /**
     * Method that adds a song to a playlist.
     *
     * @param playlistName The name of the playlist in which a song is to be added.
     *                     <strong>MUST NOT BE NULL</strong>
     * @param songName     The name of the song which is to be added. <strong>MUST
     *                     NOT BE NULL</strong>
     * @return Returns true if the song has been added to the playlist.
     * @throws PlaylistNotFoundException If playlist does not exist.
     * @throws IllegalOperationException If playlist already contains song.
     * @throws SongNotFoundException     If song does not exist.
     * @throws IOException               If I/O error occurs with the playlist file.
     */
    private boolean addSongToPlaylist(final String playlistName, final String songName) throws IOException {
        assert playlistName != null;
        assert songName != null;
        final String playlistNameToLower = playlistName.toLowerCase();
        if (!playlistsNames.contains(playlistNameToLower)) {
            throw new PlaylistNotFoundException(Messages.PLAYLIST_NOT_FOUND + playlistName);
        }
        if (playlistContainsSong(playlistNameToLower, songName)) {
            throw new IllegalOperationException(Messages.PLAYLIST_ALREADY_CONTAINS_SONG + quote(songName));
        }
        if (!songExists(songName)) {
            throw new SongNotFoundException(Messages.SONG_NOT_FOUND + quote(songName));
        }
        try (var fileWriter = new FileWriter(
                CommonConstants.PLAYLIST_DIRECTORY + playlistNameToLower + CommonConstants.TEXT_FORMAT, true);
                var printWriter = new PrintWriter(fileWriter)) {
            printWriter.println(songName);
            return true;
        }
    }

    /**
     * Method that checks if a playlist contains a song.
     *
     * @param playlistName The name of the playlist in which it is
     *                     searched.<strong>MUST NOT BE NULL</strong>
     * @param songName     The name of the song.<strong>MUST NOT BE NULL</strong>
     * @return Returns true if the song exists in the playlist, false otherwise.
     * @throws IOException               If I/O error occurs with the playlist file.
     * @throws PlaylistNotFoundException If playlist does not exist.
     * @throws FileNotFoundException     If the playlist file is not found.
     */
    private boolean playlistContainsSong(final String playlistName, final String songName)
            throws FileNotFoundException, IOException {
        assert playlistName != null;
        assert songName != null;
        final String playlistNameToLower = playlistName.toLowerCase();
        if (!playlistsNames.contains(playlistNameToLower)) {
            throw new PlaylistNotFoundException(Messages.PLAYLIST_NOT_FOUND + playlistName);
        }
        try (var fileInputStream = new FileInputStream(
                CommonConstants.PLAYLIST_DIRECTORY + playlistNameToLower + CommonConstants.TEXT_FORMAT);
                var inputStreamReader = new InputStreamReader(fileInputStream);
                var bufferedReader = new BufferedReader(inputStreamReader)) {
            String song;
            while ((song = bufferedReader.readLine()) != null) {
                if (song.equalsIgnoreCase(songName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Method that registers a user.
     *
     * @param email    The user email. <strong>MUST NOT BE NULL</strong>
     * @param password The user password. <strong>MUST NOT BE NULL</strong>
     * @return Returns if the user has been successfully registered, false
     *         otherwise.
     * @throws IOException If I/O error occurs with the login file.
     */
    private boolean registerUser(final String email, final String password) throws IOException {
        assert email != null;
        assert password != null;
        boolean registeredNewUser = false;
        if (!isRegistered(email)) {
            try (var fileWriter = new FileWriter(CommonConstants.LOGIN_PATH, true);
                    var printWriter = new PrintWriter(fileWriter)) {
                printWriter.println(email + CommonConstants.SPACE + password);
                registeredNewUser = true;
            }
        }
        return registeredNewUser;
    }

    /**
     * Method that checks if an email has been registered.
     *
     * @param email The email by which it is searched. <strong>MUST NOT BE
     *              NULL</strong>
     * @return Returns true if the email has been registered, false otherwise.
     * @throws IOException If I/O error occurs with the login file.
     */
    private boolean isRegistered(final String email) throws IOException {
        assert email != null;
        try (var fileInputStream = new FileInputStream(CommonConstants.LOGIN_PATH);
                var inputStreamReader = new InputStreamReader(fileInputStream);
                var bufferedReader = new BufferedReader(inputStreamReader)) {
            String login = null;
            while ((login = bufferedReader.readLine()) != null) {
                final String[] loginArray = login.split(CommonConstants.WHITESPACE_DELIMITER);
                final int userIndex = 0;
                if (loginArray[userIndex].equalsIgnoreCase(email)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Method that checks if a user has logged in with a specific email.
     *
     * @param email The email which is checked.
     * @return Returns true if a user using the given email is logged in, false
     *         otherwise.
     */
    private boolean isLoggedIn(final String email) {
        return (email != null) && loggedInUsers.containsKey(email);
    }

    /**
     * Method that checks if a user can log in.
     *
     * @param email    The email of the user. <strong>MUST NOT BE NULL</strong>
     * @param password The password of the user. <strong>MUST NOT BE NULL</strong>
     * @return Returns true if the user can log in, false otherwise.
     * @throws IOException               If I/O error occurs with the login file.
     * @throws IllegalOperationException If the user attemps to login while already
     *                                   logged in.
     */
    private boolean canLogIn(final String email, final String password) throws IOException {
        assert email != null;
        assert password != null;
        if (isLoggedIn(email)) {
            throw new IllegalOperationException(Messages.CANNOT_LOGIN_WHILE_LOGGED_IN);
        }
        return checkIfUserExistsInRegister(email, password);
    }

    private boolean checkIfUserExistsInRegister(final String email, final String password) throws IOException {
        try (var fileInputStream = new FileInputStream(CommonConstants.LOGIN_PATH);
                var inputStreamReader = new InputStreamReader(fileInputStream);
                var bufferedReader = new BufferedReader(inputStreamReader)) {
            String login;
            while ((login = bufferedReader.readLine()) != null) {
                final String[] loginArray = login.split(CommonConstants.WHITESPACE_DELIMITER);
                final int userIndex = 0;
                final int passwordIndex = 1;
                if (loginArray[userIndex].equalsIgnoreCase(email) && loginArray[passwordIndex].equals(password)) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Method that checks if a song exists.
     *
     * @param songName The name of the song which is searched. <strong>MUST NOT BE
     *                 NULL</strong>
     * @return Returns true of there is a song with the given name, false otherwise.
     * @throws IOException
     */
    private boolean songExists(final String songName) throws IOException {
        assert songName != null;
        if (songs.containsKey(songName)) {
            return true;
        }
        checkForNewSongs();
        return songs.containsKey(songName);
    }

    public void runServer() throws UnsupportedAudioFileException, IOException, InterruptedException {
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            final int readyChannels = selector.select();
            if (readyChannels <= 0) {
                Thread.sleep(SLEEP_MILIS);
                continue;
            }
            final Set<SelectionKey> selectedKeys = this.selector.selectedKeys();
            for (final Iterator<SelectionKey> keyIterator = selectedKeys.iterator(); keyIterator.hasNext();) {
                final SelectionKey key = keyIterator.next();
                if (key.isAcceptable()) {
                    handleAccept(key);
                } else if (key.isReadable()) {
                    handleRead(key);
                }
                keyIterator.remove();
            }
        }
    }

    private void handleAccept(final SelectionKey key) throws IOException {
        final ServerSocketChannel sockChannel = (ServerSocketChannel) key.channel();
        final SocketChannel accept = sockChannel.accept();
        accept.configureBlocking(false);
        accept.register(this.selector, SelectionKey.OP_READ);
    }

    private void handleRead(final SelectionKey key) throws UnsupportedAudioFileException, IOException {
        buffer.clear();
        final SocketChannel userSocket = (SocketChannel) key.channel();
        final String attachedEmail = key.attachment() == null ? CommonConstants.NON_LOGGED_USER
                : (String) key.attachment();
        int readBytes = 0;
        try {
            readBytes = userSocket.read(buffer);
            if (readBytes > 0) {
                buffer.flip();
                final String userRequest = new String(buffer.array(), 0, buffer.limit());
                final List<String> parsedRequest = RequestParser.parseRequest(userRequest);

                if (RequestValidator.isValidRequest(parsedRequest)) {
                    processValidRequest(key, parsedRequest, userSocket);
                } else {
                    boolean isMessageSocket = true;
                    final int leastSize = 0;
                    if (parsedRequest.size() > leastSize
                            && parsedRequest.get(CommonConstants.COMMAND_INDEX).equals(Commands.AUDIO_LOGIN)) {
                        isMessageSocket = false;
                    }
                    if (isMessageSocket) {
                        sendMessageToNonLoggedInUser(userSocket, Messages.WRONG_COMMAND + Messages.AVAILABLE_COMMANDS);
                    }
                }

            } else {
                if (attachedEmail.equals(CommonConstants.NON_LOGGED_USER)) {
                    userSocket.close();
                } else {
                    loggedInUsers.get(attachedEmail).close();
                    loggedInUsers.remove(attachedEmail);
                }
            }
        } catch (final IOException e) {
            if (!attachedEmail.equals(CommonConstants.NON_LOGGED_USER)) {
                loggedInUsers.get(attachedEmail).close();
                loggedInUsers.remove(attachedEmail);
            }
            logExceptionMessageToFile(attachedEmail + CommonConstants.SPACE + CommonConstants.COLON
                    + CommonConstants.SPACE + e.getMessage());
        }
    }

    private void processValidRequest(final SelectionKey key, final List<String> parsedRequest,
            final SocketChannel userSocket) throws IOException, UnsupportedAudioFileException {
        final String attachedEmail = (String) key.attachment();
        final boolean isLoggedIn = isLoggedIn(attachedEmail);
        final String command = parsedRequest.get(CommonConstants.COMMAND_INDEX);
        try {
            switch (command) {
                case Commands.LOGIN: {
                    processLoginRequest(key, parsedRequest, isLoggedIn, userSocket);
                    break;
                }
                case Commands.AUDIO_LOGIN: {
                    processAudioLoginRequest(key, parsedRequest, isLoggedIn, userSocket);
                    break;
                }
                case Commands.REGISTER: {
                    processRegisterRequest(parsedRequest, isLoggedIn, userSocket);
                    break;
                }
                case Commands.DISCONNECT: {
                    processDisconnectRequest(isLoggedIn, userSocket, attachedEmail);
                    break;
                }
                case Commands.PLAY_SONG: {
                    processPlaySongRequest(parsedRequest, isLoggedIn, attachedEmail);
                    break;
                }
                case Commands.STOP_SONG: {
                    processStopSongRequest(isLoggedIn, attachedEmail);
                    break;
                }
                case Commands.CREATE_PLAYLIST: {
                    processCreatePlaylistRequest(parsedRequest, isLoggedIn, attachedEmail);
                    break;
                }
                case Commands.ADD_SONG_TO_PLAYLIST: {
                    processAddSongToPlaylistRequest(parsedRequest, isLoggedIn, attachedEmail);
                    break;
                }
                case Commands.SHOW_PLAYLIST: {
                    processShowPlaylistRequest(parsedRequest, isLoggedIn, attachedEmail);
                    break;
                }
                case Commands.SEARCH_SONGS: {
                    processSearchSongs(parsedRequest, isLoggedIn, attachedEmail);
                    break;
                }
                case Commands.TOP_LISTENED_SONGS: {
                    processTopListenedSongsRequest(parsedRequest, isLoggedIn, attachedEmail);
                    break;
                }
            }
        } catch (IllegalOperationException | PlaylistNotFoundException | SongNotFoundException e) {
            sendMessageToNonLoggedInUser(userSocket, e.getMessage());
            logExceptionMessageToFile(e.getMessage());
        }
    }

    private void processLoginRequest(final SelectionKey key, final List<String> parsedRequest, final boolean isLoggedIn,
            final SocketChannel userSocket) throws IOException {
        if (isLoggedIn) {
            // ERROR cannot login if already logged in, need to logout first
            throw new IllegalOperationException(Messages.CANNOT_LOGIN_WHILE_LOGGED_IN);
        }
        final String userEmail = parsedRequest.get(CommonConstants.EMAIL_INDEX);
        final String userPwd = parsedRequest.get(CommonConstants.PASSWORD_INDEX);
        final boolean canLogInSuccessfully = canLogIn(userEmail, userPwd);
        if (!canLogInSuccessfully) {
            throw new IllegalOperationException(Messages.WRONG_EMAIL_OR_PASSWORD);
        }
        loggedInUsers.put(userEmail, new User(userSocket));
        key.attach(userEmail);
        sendMessageToLoggedInUser(userEmail, Messages.SUCCESSFUL_LOGIN + userEmail);
    }

    private void processAudioLoginRequest(final SelectionKey key, final List<String> parsedRequest,
            final boolean isLoggedIn, final SocketChannel userSocket) throws IOException {
        if (isLoggedIn) {
            // ERROR, audio login can be attempted only once, when initiating audio
            // connection.
            // throw new IllegalOperationException(Messages.ILLEGAL_AUDIO_CONNECTION);
        } else {
            final String userEmail = parsedRequest.get(CommonConstants.EMAIL_INDEX);
            final String userPwd = parsedRequest.get(CommonConstants.PASSWORD_INDEX);
            final boolean hasMessageConnectionLoggedIn = isLoggedIn(userEmail);
            boolean hasAudioConnectionLoggedIn = false;
            final User user = loggedInUsers.get(userEmail);
            if (user != null) {
                hasAudioConnectionLoggedIn = user.hasOpenAudioConnection();
            }
            final boolean canAudioLogIn = checkIfUserExistsInRegister(userEmail, userPwd);
            if (hasMessageConnectionLoggedIn && canAudioLogIn && !hasAudioConnectionLoggedIn) {
                loggedInUsers.get(userEmail).setAudioSocketChannel(userSocket);
                key.attach(userEmail);
                sendMessageToLoggedInUser(userEmail, Messages.SUCCESSFUL_AUDIO_LOGIN + userEmail);
            } else {
                // ERROR, cant register audio connection of there is no message connection
                // throw new
                // IllegalOperationException(Messages.CANNOT_AUDIO_CONNECT_WITHOUT_MESSAGE_CONNECTION);
            }
        }
    }

    private void processRegisterRequest(final List<String> parsedRequest, final boolean isLoggedIn,
            final SocketChannel userSocket) throws IOException {
        if (isLoggedIn) {
            // ERROR, cant register if already logged in
            throw new IllegalOperationException(Messages.CANNOT_REGISTER_IF_LOGGED_IN);
        }
        final String userEmail = parsedRequest.get(CommonConstants.EMAIL_INDEX);
        final String userPwd = parsedRequest.get(CommonConstants.PASSWORD_INDEX);
        if (isRegistered(userEmail)) {
            // ERROR, cant register the same email twice.
            throw new IllegalOperationException(Messages.CANNOT_REGISTER_THE_SAME_EMAIL_TWICE);
        }
        registerUser(userEmail, userPwd);
        sendMessageToNonLoggedInUser(userSocket, Messages.SUCCESSFULLY_REGISTERED + userEmail);
    }

    private void processDisconnectRequest(final boolean isLoggedIn, final SocketChannel userSocket,
            final String attachedEmail) throws IOException {
        if (isLoggedIn) {
            final User user = loggedInUsers.get(attachedEmail);
            user.close();
            user.setListeningToMusic(false);
            loggedInUsers.remove(attachedEmail, user);
        } else {
            userSocket.close();
        }
    }

    private void processPlaySongRequest(final List<String> parsedRequest, final boolean isLoggedIn,
            final String attachedEmail) throws IOException, UnsupportedAudioFileException {
        if (!isLoggedIn) {
            // ERROR, cant play song if not logged in
            throw new IllegalOperationException(Messages.CANNOT_PLAY_SONG_IF_NOT_LOGGED_IN);
        }

        if (loggedInUsers.get(attachedEmail).isListeningToMusic()) {
            // ERROR, already listening to music..
            throw new IllegalOperationException(Messages.ALREADY_LISTENING_TO_MUSIC);
        }

        final String songName = parsedRequest.get(CommonConstants.SONG_INDEX);
        if (!songExists(songName)) {
            throw new SongNotFoundException(Messages.SONG_NOT_FOUND + quote(songName));
        }
        songs.put(songName, songs.get(songName) + 1);
        sendMessageToLoggedInUser(attachedEmail, Messages.PLAYING_SONG + quote(songName));
        final User user = loggedInUsers.get(attachedEmail);
        final MusicStreamer musicStreamer = new MusicStreamer(user, songName);
        musicStreamer.start();
    }

    private void processStopSongRequest(final boolean isLoggedIn, final String attachedEmail) {
        if (!isLoggedIn) {
            // ERROR, cant stop a song if not logged in
            throw new IllegalOperationException(Messages.CANNOT_STOP_SONG_IF_NOT_LOGGED_IN);
        }
        final User user = loggedInUsers.get(attachedEmail);
        if (!user.isListeningToMusic() && (user.getSongPlayed() == null)) {
            // ERROR, cant stop a song if not listening to one
            throw new IllegalOperationException(Messages.CANNOT_STOP_SONG_IF_NOT_LISTENING_TO_ONE);
        }
        sendMessageToLoggedInUser(attachedEmail, Messages.STOPPED_SONG + quote(user.getSongPlayed()));
        user.setListeningToMusic(false);
        user.setSongPlayed(null);
    }

    private void processCreatePlaylistRequest(final List<String> parsedRequest, final boolean isLoggedIn,
            final String attachedEmail) throws IOException {
        if (!isLoggedIn) {
            // ERROR, cant create a playlist if not logged in
            throw new IllegalOperationException(Messages.CANNOT_CREATE_PLAYLIST_IF_NOT_LOGGED_IN);
        }
        final String playlistName = parsedRequest.get(CommonConstants.PLAYLIST_NAME_INDEX);
        if (!createPlaylist(playlistName)) {
            throw new IllegalOperationException(Messages.CANNOT_CREATE_PLAYLIST_WITH_ALREADY_USED_NAME);
        }
        sendMessageToLoggedInUser(attachedEmail, Messages.SUCCESSFULLY_CREATED_PLAYLIST + playlistName);
    }

    private void processAddSongToPlaylistRequest(final List<String> parsedRequest, final boolean isLoggedIn,
            final String attachedEmail) throws IOException {
        if (!isLoggedIn) {
            // ERROR, cant add song to a playlist if not logged in
            throw new IllegalOperationException(Messages.CANNOT_ADD_SONG_TO_PLAYLIST_IF_NOT_LOGGED_IN);
        }
        final String playlistName = parsedRequest.get(CommonConstants.PLAYLIST_NAME_INDEX);
        final String songName = parsedRequest.get(CommonConstants.ADD_SONG_INDEX);
        if (addSongToPlaylist(playlistName, songName)) {
            sendMessageToLoggedInUser(attachedEmail, Messages.SUCCESSFULLY_ADDED_SONG + quote(songName)
                    + CommonConstants.SPACE + Messages.TO_PLAYLIST + quote(playlistName));
        } else {
            final String songMessage = "Song ";
            final String alreadyExistsInPlaylistMessage = " already exists in playlist ";
            throw new IllegalOperationException(
                    songMessage + quote(songName) + alreadyExistsInPlaylistMessage + quote(playlistName));
        }
    }

    private void processShowPlaylistRequest(final List<String> parsedRequest, final boolean isLoggedIn,
            final String attachedEmail) throws IOException {
        if (!isLoggedIn) {
            // Error, cant show playlist if not logged in.
            throw new IllegalOperationException(Messages.CANNOT_SHOW_PLAYLIST_IF_NOT_LOGGED_IN);
        }
        final String playlistName = parsedRequest.get(CommonConstants.PLAYLIST_NAME_INDEX);
        final String playListString = showPlaylist(playlistName);
        sendMessageToLoggedInUser(attachedEmail, playListString);
    }

    private void processTopListenedSongsRequest(final List<String> parsedRequest, final boolean isLoggedIn,
            final String attachedEmail) {
        if (!isLoggedIn) {
            // Error, cant show top listened songs if not logged in.
            throw new IllegalOperationException(Messages.CANNOT_SHOW_MOST_POPULAR_SONGS_IF_NOT_LOGGED_IN);
        }
        final int numberOfSongs = Integer.parseInt(parsedRequest.get(CommonConstants.NUMBER_OF_SONGS_INDEX));
        final List<String> topListenedSongs = findTopNListenedSongs(numberOfSongs);
        final String topListenedSongsString = String.join(CommonConstants.NEW_LINE, topListenedSongs);
        sendMessageToLoggedInUser(attachedEmail,
                Messages.MOST_POPULAR_SONGS + CommonConstants.NEW_LINE + topListenedSongsString);
    }

    private void processSearchSongs(final List<String> parsedRequest, final boolean isLoggedIn,
            final String attachedEmail) {
        if (!isLoggedIn) {
            // Error, cant show top listened songs if not logged in.
            throw new IllegalOperationException(Messages.CANNOT_SEARCH_FOR_SONGS_IF_NOT_LOGGED_IN);
        }
        final int startingIndex = 1;
        final List<String> filteredSongs = filterSongs(parsedRequest.subList(startingIndex, parsedRequest.size()));
        final String filteredSongsString = (filteredSongs.size() != 0)
                ? Messages.FOUND_SONGS + CommonConstants.NEW_LINE + String.join(CommonConstants.NEW_LINE, filteredSongs)
                : Messages.NO_SONGS_FOUND;
        sendMessageToLoggedInUser(attachedEmail, filteredSongsString);
    }

    /**
     * Method that sends a message to a logged in user.
     *
     * @param email   The email with which the user has logged in.
     * @param message The message that is to be sent.
     * @return Return true if the message has been sent successfully, false
     *         otherwise.
     */
    private boolean sendMessageToLoggedInUser(final String email, final String message) {
        final SocketChannel messageSocket = loggedInUsers.get(email).getMessageSocketChannel();
        try {
            buffer.clear();
            buffer.put(message.getBytes());
            buffer.flip();
            messageSocket.write(buffer);
            buffer.clear();
            return true;
        } catch (final IOException e) {
            logExceptionMessageToFile(e.getMessage());
            return false;
        }
    }

    /**
     * Method that sends a message to a non logged user.
     *
     * @param userSocket The user's socket.
     * @param message    The message that is to be sent.
     * @return Return true if the message has been sent successfully, false
     *         otherwise.
     */
    private boolean sendMessageToNonLoggedInUser(final SocketChannel userSocket, final String message) {
        try {
            buffer.clear();
            buffer.put(message.getBytes());
            buffer.flip();
            userSocket.write(buffer);
            buffer.clear();
            return true;
        } catch (final IOException e) {
            logExceptionMessageToFile(e.getMessage());
            return false;
        }
    }

    @Override
    public void close() throws Exception {
        this.serverSocketChannel.close();
        this.selector.close();
        for (final User user : loggedInUsers.values()) {
            user.close();
        }
    }

    public static void main(final String[] args) {
        try (final SpotifyServer spotifyServer = new SpotifyServer(CommonConstants.SERVER_HOST,
                CommonConstants.SERVER_PORT)) {
            spotifyServer.runServer();
        } catch (final Exception e) {
            logExceptionMessageToFile(e.getMessage());
        }
    }
}