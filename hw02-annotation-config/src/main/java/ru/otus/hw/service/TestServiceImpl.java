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

    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public TestResult executeTestFor(Student student) {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
        List<Question> questions = questionDao.findAll();
        TestResult testResult = new TestResult(student);
        int numberQuestion = 1;

        for (Question question : questions) {
            ioService.printFormattedLine("%d. Question: %s", numberQuestion, question.text());
            List<Answer> answers = question.answers();
            for (int i = 0; i < answers.size(); i++) {
                ioService.printFormattedLine("   %d. %s", i + 1, answers.get(i).text());
            }

            int userChoice = ioService.readIntForRangeWithPrompt(1, answers.size(),
                    "Your choice (1-" + answers.size() + "): ",
                    "Please enter a number between 1 and " + answers.size()
            );
            ioService.printLine("");
            boolean isAnswerValid = answers.get(userChoice - 1).isCorrect();
            testResult.applyAnswer(question, isAnswerValid);
            numberQuestion++;
        }
        return testResult;
    }
}
