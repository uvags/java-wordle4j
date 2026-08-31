package ru.yandex.practicum;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WordleGame {
    private final String answer;
    private int steps;
    private final WordleDictionary dictionary;
    private final PrintWriter log;
    private final List<String> wordsHistory;
    private final List<String> patternsHistory;
    private final List<String> hintsGiven;
    private final List<String> possibleWords;
    private boolean won;
    private final Random random;

    public WordleGame(WordleDictionary dictionary, PrintWriter log) {
        this.dictionary = dictionary;
        this.log = log;
        this.answer = dictionary.getRandomWord();
        this.steps = WordleGameConfig.MAX_STEPS;
        this.wordsHistory = new ArrayList<>();
        this.patternsHistory = new ArrayList<>();
        this.hintsGiven = new ArrayList<>();
        this.possibleWords = new ArrayList<>(dictionary.getWords());
        this.won = false;
        this.random = new Random();
    }

    public WordleGameStateInfo guessWord(String word)
            throws InvalidWordException, InvalidWordLengthException, WordNotFoundInDictionaryException {
        String normalizedWord = WordleDictionary.normalizeWord(word);
        if (normalizedWord == null) {
            throw new InvalidWordException("Введите слово.");
        }

        if (normalizedWord.isEmpty()) {
            throw new InvalidWordException("Слово не может быть пустым.");
        }

        if (!normalizedWord.matches("[а-я]+")) {
            throw new InvalidWordException("Допустимы для ввода слов только русские буквы.");
        }

        if (normalizedWord.length() != WordleGameConfig.WORD_LENGTH) {
            throw new InvalidWordLengthException(
                    "Слово должно содержать ровно " + WordleGameConfig.WORD_LENGTH + " букв."
            );
        }

        if (normalizedWord.equals(answer)) {
            String pattern = WordleGameConfig.WIN_PATTERN;
            wordsHistory.add(normalizedWord);
            patternsHistory.add(pattern);
            steps--;
            won = true;
            log.println("Игрок угадал слово: " + normalizedWord);
            log.flush();
            return new WordleGameStateInfo(pattern, GameState.WIN);
        }

        if (!dictionary.containsWord(normalizedWord)) {
            log.println("Слово отсутствует в словаре: " + normalizedWord);
            log.flush();
            throw new WordNotFoundInDictionaryException("Слово не найдено в словаре.");
        }

        String pattern = WordleDictionary.generateStringPatternFromWord(normalizedWord, answer);

        wordsHistory.add(normalizedWord);
        patternsHistory.add(pattern);
        possibleWords.removeIf(candidate -> !matchesPattern(normalizedWord, candidate, pattern));
        possibleWords.remove(normalizedWord);
        steps--;

        log.println("Попытка: " + normalizedWord + ", результат: " + pattern);
        log.flush();

        if (steps == 0) {
            log.println("Игрок проиграл. Ответ: " + answer);
            log.flush();
            return new WordleGameStateInfo(pattern, GameState.LOSE);
        }

        return new WordleGameStateInfo(pattern, GameState.PLAYING);
    }

    public String getHint() {
        if (possibleWords.isEmpty()) {
            log.println("Подходящих слов для подсказки не найдено.");
            log.flush();
            return null;
        }

        String hint = possibleWords.get(random.nextInt(possibleWords.size()));

        hintsGiven.add(hint);
        possibleWords.remove(hint);
        log.println("Получена подсказка: " + hint);
        log.flush();

        return hint;
    }

    private boolean matchesPattern(String guessed, String candidate, String pattern) {
        String generated = WordleDictionary.generateStringPatternFromWord(guessed, candidate);
        return generated.equals(pattern);
    }

    public boolean isGameFinished() {
        return won || steps == 0;
    }

    public String getAnswer() {
        return answer;
    }

    public int getSteps() {
        return steps;
    }

    public WordleDictionary getWordleDictionary() {
        return dictionary;
    }

    public List<String> getWordsHistory() {
        return new ArrayList<>(wordsHistory);
    }

    public List<String> getPatternsHistory() {
        return new ArrayList<>(patternsHistory);
    }

    public boolean isWon() {
        return won;
    }
}