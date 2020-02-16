package bg.sofia.uni.fmi.mjt.spotify.server.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bg.sofia.uni.fmi.mjt.spotify.server.constants.CommandArguments;
import bg.sofia.uni.fmi.mjt.spotify.server.constants.Commands;
import bg.sofia.uni.fmi.mjt.spotify.server.constants.CommonConstants;

public class RequestValidator {
    private static final Map<String, Integer> commandsMapper = initializeValidationMapping();

    private RequestValidator() {
        // Utility class
    }

    private static Map<String, Integer> initializeValidationMapping() {
        final Map<String, Integer> commandsMapper = new HashMap<>();
        commandsMapper.put(Commands.LOGIN, CommandArguments.LOGIN);
        commandsMapper.put(Commands.REGISTER, CommandArguments.REGISTER);
        commandsMapper.put(Commands.DISCONNECT, CommandArguments.DISCONNECT);
        commandsMapper.put(Commands.SEARCH_SONGS, CommandArguments.SEARCH_SONGS);
        commandsMapper.put(Commands.TOP_LISTENED_SONGS, CommandArguments.TOP_LISTENED_SONGS);
        commandsMapper.put(Commands.CREATE_PLAYLIST, CommandArguments.CREATE_PLAYLIST);
        commandsMapper.put(Commands.ADD_SONG_TO_PLAYLIST, CommandArguments.ADD_SONG_TO_PLAYLIST);
        commandsMapper.put(Commands.SHOW_PLAYLIST, CommandArguments.SHOW_PLAYLIST);
        commandsMapper.put(Commands.PLAY_SONG, CommandArguments.PLAY_SONG);
        commandsMapper.put(Commands.STOP_SONG, CommandArguments.STOP_SONG);
        commandsMapper.put(Commands.AUDIO_LOGIN, CommandArguments.AUDIO_LOGIN);
        return commandsMapper;
    }

    private static boolean isNonNegativeInteger(final String str) {
        try {
            final Integer number = Integer.parseInt(str);
            return number.intValue() >= 0;
        } catch (final NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidRequest(final List<String> parsedRequest) {
        boolean isValidRequest = true;
        if (parsedRequest.isEmpty()) {
            isValidRequest = false;
        } else {
            final String command = parsedRequest.get(CommonConstants.COMMAND_INDEX);
            if (commandsMapper.containsKey(command)) {
                if (command.equals(Commands.SEARCH_SONGS)) {
                    if (parsedRequest.size() < commandsMapper.get(command)) {
                        isValidRequest = false;
                    }
                } else {
                    if (parsedRequest.size() != commandsMapper.get(command)) {
                        isValidRequest = false;
                    }
                    if (command.equals(Commands.TOP_LISTENED_SONGS) && isValidRequest) {
                        final int numberOfSongsToReturnIndex = 1;
                        if (!isNonNegativeInteger(parsedRequest.get(numberOfSongsToReturnIndex))) {
                            isValidRequest = false;
                        }
                    }
                }
            } else {
                isValidRequest = false;
            }
        }
        return isValidRequest;
    }

}
