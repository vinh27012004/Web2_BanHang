package ntu.vinh.banhang.service;

import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Service;

import ntu.vinh.banhang.config.VietQrProperties;

/**
 * Sinh chuỗi dữ liệu VietQR theo chuẩn EMVCo (NAPAS 247) cho thanh toán
 * chuyển khoản nhanh tới tài khoản. Chuỗi này sau đó được mã hóa thành ảnh QR
 * để khách quét bằng ứng dụng ngân hàng.
 */
@Service
public class VietQrService {

    // Mã định danh nhà cung cấp dịch vụ QR của NAPAS
    private static final String GUID = "A000000727";
    // Dịch vụ chuyển khoản nhanh tới tài khoản (Inter-Bank Funds Transfer To Account)
    private static final String SERVICE_TRANSFER_TO_ACCOUNT = "QRIBFTTA";

    private final VietQrProperties props;

    public VietQrService(VietQrProperties props) {
        this.props = props;
    }

    /**
     * Tạo payload VietQR động với số tiền cố định.
     *
     * @param amount  số tiền (VND, số nguyên)
     * @param addInfo nội dung chuyển khoản (có thể null)
     * @return chuỗi payload đã kèm CRC, sẵn sàng để mã hóa thành QR
     */
    public String buildPayload(long amount, String addInfo) {
        String accountInfo = tlv("00", props.getBankBin()) + tlv("01", props.getAccountNo());
        String merchantAccount = tlv("00", GUID)
                + tlv("01", accountInfo)
                + tlv("02", SERVICE_TRANSFER_TO_ACCOUNT);

        StringBuilder sb = new StringBuilder();
        sb.append(tlv("00", "01"));                       // Payload Format Indicator
        sb.append(tlv("01", "12"));                       // Point of Initiation: 12 = QR động (một lần)
        sb.append(tlv("38", merchantAccount));            // Thông tin tài khoản thụ hưởng
        sb.append(tlv("53", "704"));                      // Tiền tệ: 704 = VND
        sb.append(tlv("54", String.valueOf(amount)));     // Số tiền
        sb.append(tlv("58", "VN"));                        // Mã quốc gia
        if (addInfo != null && !addInfo.isBlank()) {
            sb.append(tlv("62", tlv("08", addInfo)));      // Nội dung chuyển khoản
        }
        sb.append("6304");                                 // ID + độ dài của trường CRC
        sb.append(crc16(sb.toString()));
        return sb.toString();
    }

    /** Đóng gói một trường theo định dạng TLV: ID(2) + độ dài(2) + giá trị. */
    private static String tlv(String id, String value) {
        return id + String.format("%02d", value.length()) + value;
    }

    /** CRC-16/CCITT-FALSE (poly 0x1021, init 0xFFFF) theo yêu cầu của chuẩn EMVCo. */
    private static String crc16(String data) {
        int crc = 0xFFFF;
        for (byte b : data.getBytes(StandardCharsets.UTF_8)) {
            crc ^= (b & 0xFF) << 8;
            for (int i = 0; i < 8; i++) {
                if ((crc & 0x8000) != 0) {
                    crc = (crc << 1) ^ 0x1021;
                } else {
                    crc <<= 1;
                }
                crc &= 0xFFFF;
            }
        }
        return String.format("%04X", crc);
    }
}
