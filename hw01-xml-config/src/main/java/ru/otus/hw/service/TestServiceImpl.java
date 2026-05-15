package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.List;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public void executeTest() {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
        List<Question> questions = questionDao.findAll();
        int numberQuestion = 1;
        for (Question question : questions) {
            ioService.printFormattedLine("%d. Question: %s", numberQuestion, question.text());
            numberQuestion++;
            for (Answer answer : question.answers()) {
                ioService.printFormattedLine("  - %s [%s]", answer.text(), answer.isCorrect() ? "✓" : "✗");
            }
            ioService.printLine("");
        }
    }
}
