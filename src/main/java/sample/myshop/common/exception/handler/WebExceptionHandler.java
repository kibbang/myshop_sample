package sample.myshop.common.exception.handler;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import sample.myshop.common.exception.BadRequestException;
import sample.myshop.common.exception.ForbiddenException;
import sample.myshop.common.exception.NotFoundException;

@ControllerAdvice
@Slf4j
public class WebExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public String handleBadRequestException(BadRequestException e, HttpServletResponse response, HttpStatus status, Model model) {
        log.warn("BadRequestException", e);
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        model.addAttribute("message", e.getMessage());
        return "error/404";
    }

    @ExceptionHandler(NotFoundException.class)
    public String handleNotFoundException(NotFoundException e, HttpServletResponse response, HttpStatus status, Model model) {
        log.warn("NotFoundException", e);
        response.setStatus(HttpStatus.NOT_FOUND.value());
        model.addAttribute("message", e.getMessage());
        return "error/400";
    }

    @ExceptionHandler(ForbiddenException.class)
    public String handleForbiddenException(ForbiddenException e, HttpServletResponse response, HttpStatus status, Model model) {
        log.warn("ForbiddenException", e);
        response.setStatus(HttpStatus.FORBIDDEN.value());
        model.addAttribute("message", e.getMessage());
        return "error/403";
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, HttpServletResponse response, Model model) {
        log.error("Unhandled Exception", e);
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        model.addAttribute("message", "서버 오류가 발생했습니다.");
        return "error/500";
    }
}
