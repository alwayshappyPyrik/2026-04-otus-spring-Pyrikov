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
        ioService.printLine("");
        ioService.printLineLocalized("TestService.answer.the.questions");
        ioService.printLine("");

        List<Question> questions = questionDao.findAll();
        TestResult testResult = new TestResult(student);
        int numberQuestion = 1;

        for (Question question : questions) {
            ioService.printFormattedLineLocalized("TestService.question", numberQuestion, question.text());
            List<Answer> answers = question.answers();
            for (int i = 0; i < answers.size(); i++) {
                ioService.printFormattedLine("   %d. %s", i + 1, answers.get(i).text());
            }

            int userChoice = ioService.readIntForRangeWithPromptLocalized(1, answers.size(),
                    "TestService.your.choice.prompt",
                    "TestService.your.choice.error"
            );
            ioService.printLine("");
            boolean isAnswerValid = answers.get(userChoice - 1).isCorrect();
            testResult.applyAnswer(question, isAnswerValid);
            numberQuestion++;
        }
        return testResult;
    }
}