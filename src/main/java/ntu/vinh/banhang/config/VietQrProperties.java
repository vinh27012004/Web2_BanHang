package ntu.vinh.banhang.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Thông tin tài khoản nhận tiền dùng để sinh mã VietQR.
 * Cấu hình qua tiền tố {@code vietqr.*} trong application.properties.
 */
@Component
@ConfigurationProperties(prefix = "vietqr")
public class VietQrProperties {

    /** Mã BIN ngân hàng theo chuẩn NAPAS (6 chữ số). */
    private String bankBin;

    /** Số tài khoản nhận tiền. */
    private String accountNo;

    /** Tên chủ tài khoản (hiển thị trên giao diện). */
    private String accountName;

    /** Bật thanh toán QR khi đã khai báo đủ ngân hàng và số tài khoản. */
    public boolean isEnabled() {
        return bankBin != null && !bankBin.isBlank()
                && accountNo != null && !accountNo.isBlank();
    }

    public String getBankBin() {
        return bankBin;
    }

    public void setBankBin(String bankBin) {
        this.bankBin = bankBin;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }
}
