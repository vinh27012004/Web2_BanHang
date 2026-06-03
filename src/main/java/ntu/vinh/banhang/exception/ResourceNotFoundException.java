package ntu.vinh.banhang.exception;

/**
 * Ném ra khi không tìm thấy một bản ghi (sản phẩm, khách hàng, hóa đơn...).
 * Được {@link GlobalExceptionHandler} ánh xạ sang HTTP 404.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
