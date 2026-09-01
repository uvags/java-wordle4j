package ru.yandex.practicum;

import java.io.PrintWriter;
import java.util.List;

public class FixedAnswerWordleDictionary extends WordleDictionary {
    private final String fixedAnswer;

    public FixedAnswerWordleDictionary(List<String> words, String fixedAnswer, PrintWriter log) {
        super(words, log);
        this.fixedAnswer = fixedAnswer;
    }

    // Переопределение, чтобы случайное выбранное слово из словаря в качестве ответа
    // всегда возвращало наше фиксированное для тестов

    @Override
    public String getRandomWord() {
        return fixedAnswer;
    }
}