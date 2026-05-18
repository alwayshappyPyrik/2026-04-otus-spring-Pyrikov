package ru.otus.hw.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Question;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
public class TestServiceImplTest {

    @Mock
    private IOService ioService;
    @Mock
    private QuestionDao questionDao;

    @InjectMocks
    private TestServiceImpl testService;

    @Test
    @DisplayName("Тестирования вопросов")
    void testExecuteTest() {
        List<Question> mockQuestions = List.of(new Question("Test?", List.of()));
        when(questionDao.findAll()).thenReturn(mockQuestions);

        testService.executeTest();

        verify(questionDao, times(1)).findAll();

        verify(ioService, times(1))
                .printFormattedLine("Please answer the questions below%n");

        verify(ioService, times(1))
                .printFormattedLine("%d. Question: %s", 1, "Test?");

        verifyNoMoreInteractions(questionDao);
    }
}
