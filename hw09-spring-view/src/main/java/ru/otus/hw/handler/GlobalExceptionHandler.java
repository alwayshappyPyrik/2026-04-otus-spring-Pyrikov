package ru.otus.hw.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import ru.otus.hw.exceptions.NotFoundException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler implements ErrorController {

    @ExceptionHandler(NotFoundException.class)
    public ModelAndView handleNotFoundException(NotFoundException e) {
        log.warn("Resource not found: {}", e.getMessage());
        return createErrorView(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleGenericException(Exception e) {
        log.error("Unexpected error occurred. Exception type: {}, Message: {}",
                e.getClass().getSimpleName(), e.getMessage(), e);

        return createErrorView("An error has occurred on the server",
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ModelAndView createErrorView(String message, HttpStatus status) {
        ModelAndView mav = new ModelAndView("error/error");
        mav.addObject("message", message);
        mav.addObject("status", status.value());
        mav.setStatus(status);
        return mav;
    }
}

