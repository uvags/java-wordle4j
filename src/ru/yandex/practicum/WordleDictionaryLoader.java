package ru.yandex.practicum;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class WordleDictionaryLoader {
    private final PrintWriter log;

    public WordleDictionaryLoader(PrintWriter log) {
        this.log = log;
    }

    public WordleDictionary load(String filename) throws IOException {
        log.println("Начинаем загрузку словаря: " + filename);
        log.flush();
        List<String> words = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String normalizedWord = WordleDictionary.normalizeWord(line);
                if (normalizedWord != null && normalizedWord.length() == WordleGameConfig.WORD_LENGTH
                        && normalizedWord.matches("[а-я]+")) {
                    words.add(normalizedWord);
                }
            }
        }
        if (words.isEmpty()) {
            throw new RuntimeException("Словарь пуст или не содержит слов длиной " + WordleGameConfig
                    .WORD_LENGTH + " букв.");
        }
        log.println("Словарь загружен. Количество слов: " + words.size());
        log.flush();
        return new WordleDictionary(words, log);
    }
}