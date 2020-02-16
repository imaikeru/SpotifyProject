package bg.sofia.uni.fmi.mjt.spotify.client.utils;

import javax.sound.sampled.AudioFormat;

import bg.sofia.uni.fmi.mjt.spotify.client.constants.Constants;
import bg.sofia.uni.fmi.mjt.spotify.client.exceptions.InvalidAudioFormatException;

public class ResponseValidator {
    public static AudioFormat extractValidAudioFormat(final String audioFormat) {
        final String[] splitAudioFormat = audioFormat.split(Constants.WHITESPACE_DELIMITER);
        if (splitAudioFormat.length != Constants.AUDIO_FORMAT_ARGUMENTS) {
            throw new InvalidAudioFormatException(Constants.INVALID_AUDIO_FORMAT_MESSAGE);
        }
        try {
            final AudioFormat.Encoding encoding = new AudioFormat.Encoding(splitAudioFormat[Constants.ENCODING_INDEX]);
            final float sampleRate = Float.parseFloat(splitAudioFormat[Constants.SAMPLE_RATE_INDEX]);
            final int sampleSizeInBits = Integer.parseInt(splitAudioFormat[Constants.SAMPLE_SIZE_IN_BITS_INDEX]);
            final int channels = Integer.parseInt(splitAudioFormat[Constants.CHANNELS_INDEX]);
            final int frameSize = Integer.parseInt(splitAudioFormat[Constants.FRAME_SIZE_INDEX]);
            final float frameRate = Float.parseFloat(splitAudioFormat[Constants.FRAME_RATE_INDEX]);
            final boolean bigEndian = Boolean.parseBoolean(splitAudioFormat[Constants.BIG_ENDIAN_INDEX]);
            return new AudioFormat(encoding, sampleRate, sampleSizeInBits, channels, frameSize, frameRate, bigEndian);
        } catch (final NumberFormatException e) {
            throw new InvalidAudioFormatException(Constants.INVALID_AUDIO_FORMAT_MESSAGE);
        }
    }
}
