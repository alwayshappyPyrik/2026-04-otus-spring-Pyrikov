package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final LocalizedIOService ioService;

    private final QuestionDao questionDao;

    @Override
    public TestResult executeTestFor(Student student) {
        printTestHeader();

        List<Question> questions = questionDao.findAll();
        TestResult testResult = new TestResult(student, ioService);

        for (int i = 0; i < questions.size(); i++) {
            processQuestion(questions.get(i), i + 1, testResult);
        }

        return testResult;
    }

    private void printTestHeader() {
        ioService.printLine("");
        ioService.printLineLocalized("TestService.answer.the.questions");
        ioService.printLine("");
    }

    private void processQuestion(Question question, int numberQuestion, TestResult testResult) {
        displayQuestion(question, numberQuestion);
        int userChoice = getUserChoice(question);
        ioService.printLine("");

        boolean isCorrect = isAnswerCorrect(question, userChoice);
        testResult.applyAnswer(question, isCorrect);
    }

    private void displayQuestion(Question question, int numberQuestion) {
        ioService.printFormattedLineLocalized("TestService.question", numberQuestion, question.text());

        List<Answer> answers = question.answers();
        for (int i = 0; i < answers.size(); i++) {
            ioService.printFormattedLine("   %d. %s", i + 1, answers.get(i).text());
        }
    }

    private int getUserChoice(Question question) {
        int maxChoice = question.answers().size();
        return ioService.readIntForRangeWithPromptLocalized(
                1, maxChoice,
                "TestService.your.choice.prompt",
                "TestService.your.choice.error"
        );
    }

    private boolean isAnswerCorrect(Question question, int userChoice) {
        return question.answers().get(userChoice - 1).isCorrect();
    }
}