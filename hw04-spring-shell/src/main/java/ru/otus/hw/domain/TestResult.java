package ru.otus.hw.domain;

import lombok.Data;
import ru.otus.hw.service.LocalizedIOService;

import java.util.ArrayList;
import java.util.List;

@Data
public class TestResult {
    private final Student student;

    private final LocalizedIOService ioService;

    private final List<Question> answeredQuestions;

    private int rightAnswersCount;

    public TestResult(Student student, LocalizedIOService ioService) {
        this.student = student;
        this.ioService = ioService;
        this.answeredQuestions = new ArrayList<>();
    }

    public void applyAnswer(Question question, boolean isRightAnswer) {

        answeredQuestions.add(question);
        if (isRightAnswer) {
            rightAnswersCount++;
        }
    }

    public void printResult() {
        ioService.printLineLocalized("TestResult");
        ioService.printFormattedLineLocalized("TestResult.student", student.getFullName());
        ioService.printFormattedLineLocalized("TestResult.answeredQuestions",  answeredQuestions.size());
        ioService.printFormattedLineLocalized("TestResult.rightAnswersCount", rightAnswersCount);
    }
}