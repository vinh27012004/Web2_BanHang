package ntu.vinh.banhang.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

/**
 * Xử lý lỗi tập trung cho toàn bộ ứng dụng.
 *
 * <p>Với request AJAX (header {@code X-Requested-With: XMLHttpRequest}) trả về
 * {@link ResponseEntity} dạng text kèm mã HTTP phù hợp để JS bắt vào callback
 * {@code error}. Với request điều hướng trang thông thường, render trang
 * {@code error.html} kèm thông báo.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public Object handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return build(request, HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler({BusinessException.class, ConstraintViolationException.class, IllegalArgumentException.class})
    public Object handleBadRequest(Exception ex, HttpServletRequest request) {
        return build(request, HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Object handleGeneric(Exception ex, HttpServletRequest request) {
        logger.error("Lỗi không mong muốn tại {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return build(request, HttpStatus.INTERNAL_SERVER_ERROR,
                "Đã có lỗi xảy ra, vui lòng thử lại sau.");
    }

    private Object build(HttpServletRequest request, HttpStatus status, String message) {
        if (isAjax(request)) {
            return ResponseEntity.status(status).body("Lỗi: " + message);
        }
        ModelAndView mav = new ModelAndView("error");
        mav.setStatus(status);
        mav.addObject("status", status.value());
        mav.addObject("error", status.getReasonPhrase());
        mav.addObject("message", message);
        return mav;
    }

    private boolean isAjax(HttpServletRequest request) {
        String requestedWith = request.getHeader("X-Requested-With");
        String accept = request.getHeader("Accept");
        return "XMLHttpRequest".equals(requestedWith)
                || (accept != null && accept.contains("application/json"));
    }
}
