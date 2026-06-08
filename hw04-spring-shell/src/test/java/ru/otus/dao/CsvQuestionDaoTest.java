package ru.otus.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.CsvQuestionDao;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = CsvQuestionDao.class)
@DisplayName("CsvQuestionDao должен ")
public class CsvQuestionDaoTest {

    @MockitoBean
    private TestFileNameProvider fileNameProvider;

    @Autowired
    private CsvQuestionDao questionDao;

    @Test
    @DisplayName("успешно загружать вопросы из существующего файла")
    void shouldLoadQuestionsFromExistingFile() {
        when(fileNameProvider.getTestFileName()).thenReturn("questions.csv");

        List<Question> questions = questionDao.findAll();

        assertThat(questions).isNotNull();
        assertThat(questions).isNotEmpty();

        Question firstQuestion = questions.getFirst();
        assertThat(firstQuestion.text()).isNotBlank();
        assertThat(firstQuestion.answers()).isNotNull();
    }

    @Test
    @DisplayName("выбрасывать исключение при попытке загрузить несуществующий файл")
    void shouldThrowExceptionWhenFileNotFound() {
        when(fileNameProvider.getTestFileName()).thenReturn("non-existing-file.csv");

        assertThatThrownBy(() -> questionDao.findAll())
                .isInstanceOf(QuestionReadException.class)
                .hasMessageContaining("File not found in classpath: non-existing-file.csv");
    }
}