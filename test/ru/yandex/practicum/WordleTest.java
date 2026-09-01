package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordleTest {
    private PrintWriter log;

    @BeforeEach
    void init() {
        log = new PrintWriter(new ByteArrayOutputStream());
    }

    private WordleGame generateTestGameWithFixedAnswer(List<String> words, String answer) {
        WordleDictionary dict = new FixedAnswerWordleDictionary(words, answer, log);

        return new WordleGame(dict, log);
    }

    private String makeTurnAndCaptureOutput(WordleGame game, String input) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(byteArrayOutputStream, true, StandardCharsets.UTF_8);
        Wordle.makeTurn(game, input, log, out);
        out.flush();

        return byteArrayOutputStream.toString(StandardCharsets.UTF_8);
    }

    @Test
    void hintAndStepsSameWhenEmptyInput() {
        List<String> words = Arrays.asList("абвгд");
        WordleGame game = generateTestGameWithFixedAnswer(words, "абвгд");
        String output = makeTurnAndCaptureOutput(game, "");

        assertTrue(output.contains("Подсказка: абвгд"));
        assertFalse(game.isGameFinished());
        assertEquals(6, game.getSteps());
    }

    @Test
    void WinAndStepsDecreaseBy1WhenInputAnswer() {
        List<String> words = Arrays.asList("абвгд");
        WordleGame game = generateTestGameWithFixedAnswer(words, "абвгд");
        String output = makeTurnAndCaptureOutput(game, "абвгд");

        assertTrue(output.contains("+++++"));
        assertTrue(output.contains("Вы угадали слово!"));
        assertTrue(game.isWon());
        assertTrue(game.isGameFinished());
        assertEquals(5, game.getSteps());
    }

    @Test
    void stepsDecreaseBy1WhenWrongWordInputAndWrongWordInDictionary() {
        List<String> words = Arrays.asList("абвгд", "мнопр");
        WordleGame game = generateTestGameWithFixedAnswer(words, "абвгд");
        String output = makeTurnAndCaptureOutput(game, "мнопр");

        assertTrue(output.contains("-----"));
        assertTrue(output.contains("Осталось попыток: 5"));
        assertFalse(game.isGameFinished());
        assertEquals(5, game.getSteps());
    }

    @Test
    void stepsSameWhenWordNotInDictionary() {
        List<String> words = Arrays.asList("абвгд");
        WordleGame game = generateTestGameWithFixedAnswer(words, "абвгд");
        String output = makeTurnAndCaptureOutput(game, "вгдеж");

        assertTrue(output.contains("Слово не найдено в словаре."));
        assertEquals(6, game.getSteps());
        assertFalse(game.isGameFinished());
    }

    @Test
    void stepsSameWhenBadLengthOfInputWord() {
        List<String> words = Arrays.asList("абвгд");
        WordleGame game = generateTestGameWithFixedAnswer(words, "абвгд");
        String output = makeTurnAndCaptureOutput(game, "абв");

        assertTrue(output.contains("Слово должно содержать ровно 5 букв."));
        assertEquals(6, game.getSteps());
        assertFalse(game.isGameFinished());
    }

    @Test
    void stepsSameWhenNotOnlyRussianLettersInInputWord() {
        List<String> words = Arrays.asList("абвгд");
        WordleGame game = generateTestGameWithFixedAnswer(words, "абвгд");
        String output = makeTurnAndCaptureOutput(game, "abcde");

        assertTrue(output.contains("Допустимы для ввода слов только русские буквы."));
        assertEquals(6, game.getSteps());
        assertFalse(game.isGameFinished());
    }

    @Test
    void loseGameWhenSpendingAllSteps() {
        List<String> words = Arrays.asList(
                "абвгд", "мбвгд", "нбвгд", "обвгд", "пбвгд", "рбвгд", "сбвгд"
        );
        WordleGame game = generateTestGameWithFixedAnswer(words, "абвгд");

        String[] wrongGuesses = {"мбвгд", "нбвгд", "обвгд", "пбвгд", "рбвгд", "сбвгд"};
        for (int i = 0; i < wrongGuesses.length; i++) {
            String output = makeTurnAndCaptureOutput(game, wrongGuesses[i]);
            if (i < wrongGuesses.length - 1) {
                assertEquals(6 - (i + 1), game.getSteps());
                assertFalse(game.isWon());
                assertFalse(game.isGameFinished());
            } else {
                assertTrue(output.contains("Попытки закончились!"));
                assertTrue(output.contains("Загаданное слово: абвгд"));
                assertEquals(0, game.getSteps());
                assertTrue(game.isGameFinished());
                assertFalse(game.isWon());
            }
        }
    }

    @Test
    void hintIsNullAndStepsSameWhenAllHintsSpentOrAvailable() {
        List<String> words = Arrays.asList("абвгд");
        WordleGame game = generateTestGameWithFixedAnswer(words, "абвгд");
        String output1 = makeTurnAndCaptureOutput(game, "");

        assertEquals(6, game.getSteps());
        assertTrue(output1.contains("Подсказка: абвгд"));

        String output2 = makeTurnAndCaptureOutput(game, "");

        assertTrue(output2.contains("Нет подходящих слов. Попробуйте ввести слово."));
        assertEquals(6, game.getSteps());
        assertFalse(game.isGameFinished());
    }
}