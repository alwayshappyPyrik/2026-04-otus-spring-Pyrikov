package ru.otus.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.service.IOService;
import ru.otus.hw.service.TestServiceImpl;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("TestServiceImpl должен ")
public class TestServiceImplTest {

    @Mock
    private IOService ioService;
    @Mock
    private QuestionDao questionDao;

    @InjectMocks
    private TestServiceImpl testService;

    @Test
    @DisplayName("выводить в консоль вопросы и ответы в заданном формате")
    void shouldDisplayQuestionAndAnswerInTheSpecifiedFormatInTheConsole() {
        List<Question> mockQuestionAndAnswer = List.of(new Question("Test?", List.of(
                new Answer("no", false),
                new Answer("yes", true),
                new Answer("unclear", false)
        )));
        when(questionDao.findAll()).thenReturn(mockQuestionAndAnswer);

        Student student = new Student("Test", "Student");

        when(ioService.readIntForRangeWithPrompt(anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(2);

        testService.executeTestFor(student);

        InOrder inOrder = inOrder(ioService);

        inOrder.verify(ioService).printLine("");
        inOrder.verify(ioService).printFormattedLine("Please answer the questions below%n");
        inOrder.verify(ioService).printFormattedLine("%d. Question: %s", 1, "Test?");
        inOrder.verify(ioService).printFormattedLine("   %d. %s", 1, "no");
        inOrder.verify(ioService).printFormattedLine("   %d. %s", 2, "yes");
        inOrder.verify(ioService).printFormattedLine("   %d. %s", 3, "unclear");
        inOrder.verify(ioService).readIntForRangeWithPrompt(1, 3, "Your choice (1-3): ", "Please enter a number between 1 and 3");
    }
}
