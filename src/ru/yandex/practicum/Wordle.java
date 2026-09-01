package ru.yandex.practicum;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Wordle {

    public static void main(String[] args) {
        try (PrintWriter log = new PrintWriter(new FileWriter("game.log", StandardCharsets.UTF_8, true));
             Scanner scanner = new Scanner(System.in)) {

            log.println("Игра запущена");
            log.flush();

            try {
                WordleDictionaryLoader loader = new WordleDictionaryLoader(log);
                WordleDictionary dictionary = loader.load("words_ru.txt");
                WordleGame game = new WordleGame(dictionary, log);

                startGameLoop(game, scanner, log, System.out);
            } catch (Exception e) {
                log.println("Непредвиденная ошибка: " + e.getMessage());
                e.printStackTrace(log);
                log.flush();
                System.err.println("Произошла ошибка программы. Детали в game.log.");
            }

        } catch (IOException e) {
            System.err.println("Не удалось создать лог-файл: " + e.getMessage());
        }
    }

    private static void startGameLoop(WordleGame game, Scanner scanner,
                                      PrintWriter log, PrintStream out) {
        out.println("Игра Wordle!");
        out.println("У вас " + WordleGameConfig.MAX_STEPS + " попыток угадать слово из "
                + WordleGameConfig.WORD_LENGTH + " букв.");
        out.println("Введите слово или нажмите Enter для подсказки.");

        while (!game.isGameFinished()) {
            out.print(": ");
            String input = scanner.nextLine();
            makeTurn(game, input, log, out);
        }

        out.println("Конец игры.");
        log.println("Игра завершена.");
        log.flush();
    }

    public static void makeTurn(WordleGame game, String input, PrintWriter log, PrintStream out) {
        String normalizedInput = WordleDictionary.normalizeWord(input);

        if (normalizedInput.isEmpty()) {
            String hint = game.getHint();
            if (hint == null) {
                out.println("Нет подходящих слов. Попробуйте ввести слово.");
            } else {
                out.println("Подсказка: " + hint);
            }
            return;
        }

        try {
            WordleGameStateInfo result = game.guessWord(input);
            out.println(result.getPattern());

            switch (result.getState()) {
                case WIN:
                    out.println("Вы угадали слово!");
                    break;
                case LOSE:
                    out.println("Попытки закончились!");
                    out.println("Загаданное слово: " + game.getAnswer());
                    break;
                case PLAYING:
                    out.println("Осталось попыток: " + game.getSteps());
                    break;
            }

        } catch (InvalidWordException e) {
            out.println(e.getMessage());
            log.println("Некорректный ввод: " + e.getMessage());
            log.flush();

        } catch (InvalidWordLengthException e) {
            out.println(e.getMessage());
            log.println("Некорректная длина слова: " + e.getMessage());
            log.flush();

        } catch (WordNotFoundInDictionaryException e) {
            out.println(e.getMessage());
            log.println("Слово отсутствует в словаре: " + input);
            log.flush();
        }
    }
}