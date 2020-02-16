package bg.sofia.uni.fmi.mjt.spotify.server.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import bg.sofia.uni.fmi.mjt.spotify.server.constants.Commands;
import bg.sofia.uni.fmi.mjt.spotify.server.constants.CommonConstants;

public class RequestParser {
    private RequestParser() {
        // Utility class
    }

    public static List<String> parseRequest(String request) {
        final List<String> parsedRequest = new ArrayList<>();
        request = request.trim();
        if (!isEmpty(request)) {
            final int inTwo = 2;
            final int remainderIndex = 1;

            final String[] splitIntoTwoRequest = request.split(CommonConstants.WHITESPACE_DELIMITER, inTwo);
            final String extractedCommand = splitIntoTwoRequest[CommonConstants.COMMAND_INDEX].toLowerCase();

            parsedRequest.add(extractedCommand);

            final int commandAndRemainderLength = 2;
            if (splitIntoTwoRequest.length == commandAndRemainderLength) {
                String[] secondSplit;

                switch (extractedCommand) {
                    case Commands.DISCONNECT:
                    case Commands.STOP_SONG:
                    case Commands.SEARCH_SONGS:
                    case Commands.TOP_LISTENED_SONGS:
                    case Commands.LOGIN:
                    case Commands.AUDIO_LOGIN:
                    case Commands.REGISTER:
                        secondSplit = splitIntoTwoRequest[remainderIndex].split(CommonConstants.WHITESPACE_DELIMITER);
                        for (final String str : secondSplit) {
                            parsedRequest.add(str.trim());
                        }
                        break;
                    case Commands.CREATE_PLAYLIST:
                    case Commands.ADD_SONG_TO_PLAYLIST:
                    case Commands.SHOW_PLAYLIST:
                    case Commands.PLAY_SONG:
                        secondSplit = splitIntoTwoRequest[remainderIndex].split(CommonConstants.QUOTATION_MARK);
                        final List<String> tokensFilteredFromWhiteSpaces = Arrays.stream(secondSplit)
                                .map(token -> token.trim()).filter(token -> !token.equals(CommonConstants.EMPTY_STRING))
                                .collect(Collectors.toList());
                        parsedRequest.addAll(tokensFilteredFromWhiteSpaces);
                        break;
                    default:
                }
            }
        }
        return parsedRequest;
    }

    private static boolean isEmpty(final String string) {
        return string.equals(CommonConstants.EMPTY_STRING);
    }
}
