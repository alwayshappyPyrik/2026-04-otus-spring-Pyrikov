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
import ru.otus.hw.service.LocalizedIOService;
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
    private LocalizedIOService localizedIOService;
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

        when(localizedIOService.readIntForRangeWithPromptLocalized(anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(2);

        testService.executeTestFor(student);

        InOrder inOrder = inOrder(localizedIOService);

        inOrder.verify(localizedIOService).printLine("");
        inOrder.verify(localizedIOService).printLineLocalized("TestService.answer.the.questions");
        inOrder.verify(localizedIOService).printLine("");
        inOrder.verify(localizedIOService).printFormattedLineLocalized("TestService.question", 1, "Test?");
        inOrder.verify(localizedIOService).printFormattedLine("   %d. %s", 1, "no");
        inOrder.verify(localizedIOService).printFormattedLine("   %d. %s", 2, "yes");
        inOrder.verify(localizedIOService).printFormattedLine("   %d. %s", 3, "unclear");
        inOrder.verify(localizedIOService).readIntForRangeWithPromptLocalized(1, 3,
                "TestService.your.choice.prompt", "TestService.your.choice.error");
    }
}