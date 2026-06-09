package ru.otus.hw.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.command.annotation.Command;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;
import ru.otus.hw.service.ResultService;
import ru.otus.hw.service.StudentService;
import ru.otus.hw.service.TestService;

@Command(group = "Application test student commands")
@RequiredArgsConstructor
public class TestStudentCommands {

    private final TestService testService;

    private final StudentService studentService;

    private final ResultService resultService;

    @Command(description = "Registration student for test", command = "rsft")
    public String registrationStudent() {
        Student student = getCurrentStudent();
        return student.getFullName();
    }

    @Command(description = "Execute test for student", command = "etfs")
    public void executeTestForStudent() {
        TestResult result = testService.executeTestFor(getCurrentStudent());
        result.printResult();
    }

    @Command(description = "Show result for student", command = "srfs")
    public void showResultForStudent() {
        TestResult result = testService.executeTestFor(getCurrentStudent());
        resultService.showResult(result);
    }

    private Student getCurrentStudent() {
        return studentService.determineCurrentStudent();
    }
}
