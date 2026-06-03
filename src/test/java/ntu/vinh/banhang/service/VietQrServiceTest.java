package ntu.vinh.banhang.service;

import ntu.vinh.banhang.config.VietQrProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VietQrServiceTest {

    private VietQrService service;

    @BeforeEach
    void setUp() {
        VietQrProperties props = new VietQrProperties();
        props.setBankBin("970415");
        props.setAccountNo("0123456789");
        props.setAccountName("NGUYEN VAN A");
        service = new VietQrService(props);
    }

    @Test
    void buildPayload_dungChuanEmvcoVietQR() {
        String payload = service.buildPayload(30000, "POS123");

        // Mở đầu bằng Payload Format Indicator 00 + QR động 01=12
        assertThat(payload).startsWith("000201" + "010212");
        // Có GUID NAPAS, mã ngân hàng, số tài khoản và mã dịch vụ chuyển khoản
        assertThat(payload).contains("A000000727");
        assertThat(payload).contains("970415");
        assertThat(payload).contains("0123456789");
        assertThat(payload).contains("QRIBFTTA");
        // Tiền tệ VND, số tiền, mã quốc gia, nội dung
        assertThat(payload).contains("5303704");
        assertThat(payload).contains("540530000");
        assertThat(payload).contains("5802VN");
        assertThat(payload).contains("POS123");
        // Kết thúc bằng trường CRC: "6304" + 4 ký tự hex
        assertThat(payload).contains("6304");
        assertThat(payload.substring(payload.length() - 4)).matches("[0-9A-F]{4}");
    }

    @Test
    void buildPayload_crcOnDinh_choCungDauVao() {
        assertThat(service.buildPayload(30000, "POS123"))
                .isEqualTo(service.buildPayload(30000, "POS123"));
    }
}
