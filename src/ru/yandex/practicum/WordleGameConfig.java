package ru.yandex.practicum;

public final class WordleGameConfig {
    public static final int WORD_LENGTH = 5;
    public static final int MAX_STEPS = 6;
    public static final String WIN_PATTERN = "+".repeat(WORD_LENGTH);

    private WordleGameConfig() {
    }
}