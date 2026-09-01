package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordleDictionaryTest {

    private PrintWriter log;
    private WordleDictionary dictionary;
    private List<String> initialWords;

    @BeforeEach
    void init() {
        log = new PrintWriter(System.out);
        initialWords = Arrays.asList("герой", "гора", "город", "мама");
        dictionary = new WordleDictionary(initialWords, log);
    }

    @Test
    void runtimeExceptionWhenConstructorWithNullList() {
        assertThrows(RuntimeException.class, () -> new WordleDictionary(null, log));
    }

    @Test
    void runtimeExceptionWhenConstructorWithEmptyList() {
        assertThrows(RuntimeException.class, () -> new WordleDictionary(List.of(), log));
    }

    @Test
    void nullWhenArgumentIsNull() {
        assertNull(WordleDictionary.normalizeWord(null));
    }

    @Test
    void emptyWhenArgumentIsEmptyString() {
        assertEquals("", WordleDictionary.normalizeWord(""));
    }

    @Test
    void trimmedStringWhenSpaces() {
        assertEquals("малой", WordleDictionary.normalizeWord("  малой  "));
    }

    @Test
    void lowerCaseStringWhenUpperCase() {
        assertEquals("малой", WordleDictionary.normalizeWord("МАЛОЙ"));
    }

    @Test
    void eInsteadWhenYoInString() {
        assertEquals("ежик", WordleDictionary.normalizeWord("ёжик"));
    }

    @Test
    void trueWhenWordInDictionary() {
        assertTrue(dictionary.containsWord("герой"));
        assertTrue(dictionary.containsWord("ГЕРОЙ"));
    }

    @Test
    void falseWhenWordNotInDictionary() {
        assertFalse(dictionary.containsWord("несущ"));
    }

    @Test
    void randomWordFromDictionary() {
        String word = dictionary.getRandomWord();
        assertTrue(initialWords.contains(word));
    }

    @Test
    void allPlusesPatternWhenGuessEqualsAnswer() {
        String pattern = WordleDictionary.generateStringPatternFromWord("малой", "малой");
        assertEquals("+++++", pattern);
    }

    @Test
    void allMinusesPatternWhenGuessAbsolutelyNotEqualsAnswer() {
        String pattern = WordleDictionary.generateStringPatternFromWord("абвгд", "прсту");
        assertEquals("-----", pattern);
    }

    @Test
    void generateRightPatternFromGuess() {
        String pattern = WordleDictionary.generateStringPatternFromWord("гарда", "герой");
        assertEquals("+-+--", pattern);
    }

    @Test
    void generateRightPatternWhenDuplicates() {
        String pattern = WordleDictionary.generateStringPatternFromWord("мамма", "мамам");
        assertEquals("+++^^", pattern);
    }

    @Test
    void RuntimeExceptionWhenWordOrAnswerIsNull() {
        assertThrows(RuntimeException.class,
                () -> WordleDictionary.generateStringPatternFromWord(null, "герой"));
        assertThrows(RuntimeException.class,
                () -> WordleDictionary.generateStringPatternFromWord("герой", null));
    }

    @Test
    void RuntimeExceptionWhenWordsHaveDifferentLengths() {
        assertThrows(RuntimeException.class,
                () -> WordleDictionary.generateStringPatternFromWord("герой", "герцогиня"));
    }
}