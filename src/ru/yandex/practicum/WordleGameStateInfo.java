package ru.yandex.practicum;

public class WordleGameStateInfo {

    private final String pattern;
    private final GameState state;

    public WordleGameStateInfo(String pattern, GameState state) {
        this.pattern = pattern;
        this.state = state;
    }

    public String getPattern() {
        return pattern;
    }

    public GameState getState() {
        return state;
    }
}