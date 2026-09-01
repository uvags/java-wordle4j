package ru.yandex.practicum;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WordleDictionary {
    private final List<String> words;
    private final PrintWriter log;

    public WordleDictionary(List<String> words, PrintWriter log) {
        if (words == null || words.isEmpty()) {
            throw new RuntimeException("Словарь пуст.");
        }
        this.words = new ArrayList<>(words);
        this.log = log;
        log.println("Создан словарь. Количество слов: " + this.words.size());
        log.flush();
    }

    public List<String> getWords() {
        return new ArrayList<>(words);
    }

    public static String normalizeWord(String word) {
        if (word == null) {
            return null;
        }
        return word.trim().toLowerCase().replace('ё', 'е');
    }

    public boolean containsWord(String word) {
        String normalizedWord = normalizeWord(word);
        return normalizedWord != null && words.contains(normalizedWord);
    }

    public String getRandomWord() {
        Random random = new Random();
        return words.get(random.nextInt(words.size()));
    }

    public static String generateStringPatternFromWord(String word, String answer) {
        String normalizedWord = normalizeWord(word);
        String normalizedAnswer = normalizeWord(answer);
        if (normalizedWord == null || normalizedAnswer == null) {
            throw new RuntimeException("Слово и ответ не могут быть null.");
        }
        if (normalizedWord.length() != normalizedAnswer.length()) {
            throw new RuntimeException("Слова должны иметь одинаковую длину.");
        }
        int length = normalizedWord.length();
        boolean[] considered = new boolean[length];
        StringBuilder pattern = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            if (normalizedWord.charAt(i) == normalizedAnswer.charAt(i)) {
                pattern.append('+');
                considered[i] = true;
            } else {
                pattern.append('-');
            }
        }
        for (int i = 0; i < length; i++) {
            if (pattern.charAt(i) == '+') {
                continue;
            }
            char currentChar = normalizedWord.charAt(i);
            for (int j = 0; j < length; j++) {
                if (normalizedAnswer.charAt(j) == currentChar && !considered[j]) {
                    considered[j] = true;
                    pattern.setCharAt(i, '^');
                    break;
                }
            }
        }
        return pattern.toString();
    }
}