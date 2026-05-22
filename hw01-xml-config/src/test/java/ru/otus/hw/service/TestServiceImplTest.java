package ru.otus.hw.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

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

        ArgumentCaptor<String> questionFormatCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> questionArgsCaptor = ArgumentCaptor.forClass(Object.class);

        ArgumentCaptor<String> answerFormatCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> answerTextCaptor = ArgumentCaptor.forClass(String.class);

        testService.executeTest();

        verify(ioService, times(1))
                .printFormattedLine(questionFormatCaptor.capture(),
                        questionArgsCaptor.capture(),
                        questionArgsCaptor.capture());

        assertThat(questionFormatCaptor.getValue()).isEqualTo("%d. Question: %s");
        assertThat(questionArgsCaptor.getAllValues()).containsExactly(1, "Test?");

        verify(ioService, times(3))
                .printFormattedLine(answerFormatCaptor.capture(), answerTextCaptor.capture());

        assertThat(answerFormatCaptor.getAllValues())
                .allMatch(format -> format.equals("  - %s "));

        assertThat(answerTextCaptor.getAllValues())
                .containsExactly("no", "yes", "unclear");
    }
}
