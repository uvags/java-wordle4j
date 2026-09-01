package ru.yandex.practicum;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

// Пример с tempDir https://www.baeldung.com/junit-5-temporary-directory
// И здесь https://habr.com/ru/companies/otus/articles/920200/

class WordleDictionaryLoaderTest {
    @TempDir
    Path tempDir;
    private final PrintWriter log = new PrintWriter(System.out);

    @AfterEach
    void flushLog() {
        log.flush();
    }

    @Test
    void validWordsWhenLoadFromFile() throws IOException {
        Path dictionaryFile = tempDir.resolve("wordle-words.txt");
        Files.write(dictionaryFile, List.of("тачка", "лето", "дом", "ёжик", "СЛОВо", "  тесты  "),
                StandardCharsets.UTF_8);
        WordleDictionaryLoader loader = new WordleDictionaryLoader(log);
        WordleDictionary dictionary = loader.load(dictionaryFile.toString());
        assertNotNull(dictionary);
        assertEquals(List.of("тачка", "слово", "тесты"), dictionary.getWords());
    }

    @Test
    void normalizedWordsWhenLoadedFromFile() throws IOException {
        Path dictionaryFile = tempDir.resolve("wordle-words.txt");
        Files.write(dictionaryFile, List.of(" СЛОВО ", "ЁжиКИ", "тЕстЫ"), StandardCharsets.UTF_8);
        WordleDictionaryLoader loader = new WordleDictionaryLoader(log);
        WordleDictionary dictionary = loader.load(dictionaryFile.toString());
        assertEquals(List.of("слово", "ежики", "тесты"), dictionary.getWords());
    }

    @Test
    void ignoreWordsWithBadLength() throws IOException {
        Path dictionaryFile = tempDir.resolve("wordle-words.txt");
        Files.write(dictionaryFile, List.of("кот", "собака", "крот", "малой", "тесты"), StandardCharsets.UTF_8);
        WordleDictionaryLoader loader = new WordleDictionaryLoader(log);
        WordleDictionary dictionary = loader.load(dictionaryFile.toString());
        assertEquals(List.of("малой", "тесты"), dictionary.getWords());
    }

    @Test
    void ignoreWordsWithNotRussianLetters() throws IOException {
        Path dictionaryFile = tempDir.resolve("wordle-words.txt");
        Files.write(dictionaryFile, List.of("слово", "hello", "12345", "сло8о", "тесты"), StandardCharsets.UTF_8);
        WordleDictionaryLoader loader = new WordleDictionaryLoader(log);
        WordleDictionary dictionary = loader.load(dictionaryFile.toString());
        assertEquals(List.of("слово", "тесты"), dictionary.getWords());
    }

    @Test
    void runtimeExceptionWhenDictionaryIsEmpty() throws IOException {
        Path dictionaryFile = tempDir.resolve("wordle-empty.txt");
        Files.write(dictionaryFile, List.of(), StandardCharsets.UTF_8);
        WordleDictionaryLoader loader = new WordleDictionaryLoader(log);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> loader
                .load(dictionaryFile.toString()));
        assertTrue(exception.getMessage().contains("Словарь пуст"));
    }

    @Test
    void runtimeExceptionWhenDictionaryHasNotValidWords() throws IOException {
        Path dictionaryFile = tempDir.resolve("wordle-invalid.txt");
        Files.write(dictionaryFile, List.of("дом", "машина", "hello", "12345"), StandardCharsets.UTF_8);
        WordleDictionaryLoader loader = new WordleDictionaryLoader(log);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> loader
                .load(dictionaryFile.toString()));
        assertTrue(exception.getMessage().contains("Словарь пуст или не содержит слов длиной " + WordleGameConfig
                .WORD_LENGTH + " букв"));
    }

    @Test
    void iOExceptionWhenFileWithWordsDoesNotExist() {
        Path dictionaryFile = tempDir.resolve("wordle-not-exists.txt");
        WordleDictionaryLoader loader = new WordleDictionaryLoader(log);
        assertThrows(IOException.class, () -> loader.load(dictionaryFile.toString()));
    }
}