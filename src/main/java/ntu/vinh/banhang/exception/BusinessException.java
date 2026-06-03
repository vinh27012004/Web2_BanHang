package ntu.vinh.banhang.exception;

/**
 * Ném ra khi vi phạm quy tắc nghiệp vụ (hết hàng, thanh toán không đủ...).
 * Được {@link GlobalExceptionHandler} ánh xạ sang HTTP 400.
 */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
