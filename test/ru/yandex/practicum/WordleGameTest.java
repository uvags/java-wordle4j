package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordleGameTest {
    private static final List<String> DICTIONARY = List.of("аббат", "абвер", "абзац", "абрис");
    private static final String ANSWER = "абзац";

    private WordleGame game;
    private PrintWriter log;

    @BeforeEach
    void init() {
        // Перенаправление консольного в текущей реализации вывода в массив байтов
        log = new PrintWriter(new OutputStreamWriter(new ByteArrayOutputStream()));
        WordleDictionary dictionary = new FixedAnswerWordleDictionary(DICTIONARY, ANSWER, log);
        game = new WordleGame(dictionary, log);
    }

    @Test
    void validInitByConstructorOfWordleGameInstance() {
        assertEquals(ANSWER, game.getAnswer());
        assertEquals(WordleGameConfig.MAX_STEPS, game.getSteps());
        assertTrue(game.getWordsHistory().isEmpty());
        assertTrue(game.getPatternsHistory().isEmpty());
        assertEquals(DICTIONARY.size(), game.getWordleDictionary().getWords().size());
        assertFalse(game.isWon());
        assertFalse(game.isGameFinished());
    }

    @Test
    void winAndStepsDecreaseBy1WhenInputAnswerAndFirstTurn() throws Exception {
        WordleGameStateInfo result = game.guessWord(ANSWER);
        assertEquals(WordleGameConfig.WIN_PATTERN, result.getPattern());
        assertEquals(GameState.WIN, result.getState());
        assertTrue(game.isWon());
        assertTrue(game.isGameFinished());
        assertEquals(WordleGameConfig.MAX_STEPS - 1, game.getSteps());
        assertEquals(1, game.getWordsHistory().size());
        assertEquals(ANSWER, game.getWordsHistory().getFirst());
        assertEquals(WordleGameConfig.WIN_PATTERN, game.getPatternsHistory().getFirst());
    }

    @Test
    void loseAndStepsIsZeroAndGameStateIsLoseAndWonIsFalseWhenAllAttemptsUsed() throws Exception {
        String guess = "аббат";
        for (int i = 0; i < WordleGameConfig.MAX_STEPS - 1; i++) {
            WordleGameStateInfo result = game.guessWord(guess);
            assertEquals(GameState.PLAYING, result.getState());
        }
        WordleGameStateInfo lastResult = game.guessWord(guess);
        assertEquals(GameState.LOSE, lastResult.getState());
        assertEquals(0, game.getSteps());
        assertFalse(game.isWon());
        assertTrue(game.isGameFinished());
    }

    @Test
    void stepsDecreaseBy1AndContinueGameWhenStepsMoreThan1AndWrongInputWordInDictionary() throws Exception {
        String guess = "абрис";
        WordleGameStateInfo result = game.guessWord(guess);
        String expectedPattern = WordleDictionary.generateStringPatternFromWord(guess, ANSWER);
        assertEquals(expectedPattern, result.getPattern());
        assertEquals(GameState.PLAYING, result.getState());
        assertFalse(game.isWon());
        assertFalse(game.isGameFinished());
        assertEquals(WordleGameConfig.MAX_STEPS - 1, game.getSteps());
        assertEquals(1, game.getWordsHistory().size());
        assertEquals(guess, game.getWordsHistory().getFirst());
    }

    // Имитация ситуации, когда пользователь запрашивает подсказки, пока не закончатся
    @Test
    void nullHintWhenAllHintsUsed() {
        int numOfWords = DICTIONARY.size();
        for (int i = 0; i < numOfWords; i++) {
            assertNotNull(game.getHint());
        }
        assertNull(game.getHint());

        List<String> hints = game.getHintsGiven();
        assertEquals(numOfWords, hints.size());
        assertEquals(numOfWords, new HashSet<>(hints).size());
    }

    @Test
    void validHintsFromPossibleWords() {
        String hint1 = game.getHint();
        assertNotNull(hint1);
        assertTrue(DICTIONARY.contains(hint1));
        List<String> hints = game.getHintsGiven();
        assertEquals(1, hints.size());
        assertEquals(hint1, hints.getFirst());
        String hint2 = game.getHint();
        assertNotNull(hint2);
        assertNotEquals(hint1, hint2);
        hints = game.getHintsGiven();
        assertEquals(2, hints.size());
        assertTrue(hints.contains(hint1));
        assertTrue(hints.contains(hint2));
    }

    @Test
    void wordNotFoundInDictionaryExceptionAndStatesSameWhenWordNotInDictionary() {
        String guess = "абвгд";
        assertThrows(WordNotFoundInDictionaryException.class, () -> game.guessWord(guess));
        assertEquals(WordleGameConfig.MAX_STEPS, game.getSteps());
        assertTrue(game.getWordsHistory().isEmpty());
        assertTrue(game.getPatternsHistory().isEmpty());
    }

    @Test
    void invalidWordLengthExceptionAndStatesSameWhenWordHasBadLength() {
        String guess1 = "абвг";
        assertThrows(InvalidWordLengthException.class, () -> game.guessWord(guess1));
        assertEquals(WordleGameConfig.MAX_STEPS, game.getSteps());
        String guess2 = "абвгдеж";
        assertThrows(InvalidWordLengthException.class, () -> game.guessWord(guess2));
        assertEquals(WordleGameConfig.MAX_STEPS, game.getSteps());
    }

    @Test
    void invalidWordExceptionAndStatesSameWhenNotValidLettersInWord() {
        String guess = "абв35";
        assertThrows(InvalidWordException.class, () -> game.guessWord(guess));
        assertEquals(WordleGameConfig.MAX_STEPS, game.getSteps());
    }

    @Test
    void invalidWordExceptionAndStatesSameWhenNotRussianLettersInWord() {
        String guess = "abcde";
        assertThrows(InvalidWordException.class, () -> game.guessWord(guess));
        assertEquals(WordleGameConfig.MAX_STEPS, game.getSteps());
    }

    @Test
    void invalidWordExceptionAndStatesSameWhenWordIsNullOrEmpty() {
        assertThrows(InvalidWordException.class, () -> game.guessWord(""));
        assertEquals(WordleGameConfig.MAX_STEPS, game.getSteps());
        assertThrows(InvalidWordException.class, () -> game.guessWord(null));
        assertEquals(WordleGameConfig.MAX_STEPS, game.getSteps());
    }
}