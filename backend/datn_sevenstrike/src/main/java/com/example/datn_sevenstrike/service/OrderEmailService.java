package com.example.datn_sevenstrike.service;

import com.example.datn_sevenstrike.dto.client.ClientOrderDetailDTO;
import com.example.datn_sevenstrike.entity.HoaDon;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Gửi email xác nhận đơn hàng khi thanh toán thành công (COD hoặc VNPay).
 * Email chứa thông tin hóa đơn và nút xem trạng thái đơn hàng không cần đăng nhập.
 */
@Service
@RequiredArgsConstructor
public class OrderEmailService {

    private final JavaMailSender mailSender;

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    @Async
    public void sendOrderConfirmationEmail(String toEmail, HoaDon hd, ClientOrderDetailDTO orderDetail, String trackingUrl) {
        if (toEmail == null || toEmail.isBlank()) return;

        try {
            NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            ClassPathResource htmlResource = new ClassPathResource("order_confirmation_email_template.html");
            String htmlTemplate;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(htmlResource.getInputStream(), "UTF-8"))) {
                htmlTemplate = reader.lines().collect(Collectors.joining(System.lineSeparator()));
            }

            String ngayTao = hd.getNgayTao() != null ? hd.getNgayTao().format(dateFormatter) : "";
            String tongTien = hd.getTongTienSauGiam() != null ? formatter.format(hd.getTongTienSauGiam()) + " VNĐ" : "0 VNĐ";

            StringBuilder itemsHtml = new StringBuilder();
            if (orderDetail != null && orderDetail.getItems() != null) {
                int stt = 1;
                for (var item : orderDetail.getItems()) {
                    String tenSp = item.getTenSanPham() != null ? item.getTenSanPham() : "Sản phẩm";
                    String donGia = item.getDonGia() != null ? formatter.format(item.getDonGia()) + " đ" : "0 đ";
                    int soLuong = item.getSoLuong() != null ? item.getSoLuong() : 0;
                    BigDecimal thanhTien = item.getDonGia() != null && item.getSoLuong() != null
                            ? item.getDonGia().multiply(BigDecimal.valueOf(item.getSoLuong()))
                            : BigDecimal.ZERO;
                    String thanhTienStr = formatter.format(thanhTien) + " đ";

                    itemsHtml.append("<tr><td style='padding:8px;border-bottom:1px solid #eee;'>").append(stt++).append("</td>");
                    itemsHtml.append("<td style='padding:8px;border-bottom:1px solid #eee;'>").append(tenSp).append("</td>");
                    itemsHtml.append("<td style='padding:8px;border-bottom:1px solid #eee;'>").append(donGia).append("</td>");
                    itemsHtml.append("<td style='padding:8px;border-bottom:1px solid #eee;'>").append(soLuong).append("</td>");
                    itemsHtml.append("<td style='padding:8px;border-bottom:1px solid #eee;'>").append(thanhTienStr).append("</td></tr>");
                }
            }

            String finalHtml = htmlTemplate
                    .replace("{{TEN_KHACH_HANG}}", hd.getTenKhachHang() != null ? hd.getTenKhachHang() : "Khách hàng")
                    .replace("{{MA_HOA_DON}}", hd.getMaHoaDon() != null ? hd.getMaHoaDon() : "")
                    .replace("{{NGAY_TAO}}", ngayTao)
                    .replace("{{TONG_TIEN}}", tongTien)
                    .replace("{{TRANG_THAI}}", orderDetail != null && orderDetail.getTrangThai() != null ? orderDetail.getTrangThai() : "Chờ xác nhận")
                    .replace("{{ITEMS_HTML}}", itemsHtml.length() > 0 ? itemsHtml.toString() : "<tr><td colspan='5' style='padding:8px;'>Không có sản phẩm</td></tr>")
                    .replace("{{TRACKING_URL}}", trackingUrl != null ? trackingUrl : frontendUrl)
                    .replace("{{CURRENT_YEAR}}", String.valueOf(java.time.Year.now().getValue()));

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(toEmail.trim());
            helper.setSubject("Xác nhận đơn hàng " + (hd.getMaHoaDon() != null ? hd.getMaHoaDon() : "") + " - SevenStrike");
            helper.setText(finalHtml, true);
            mailSender.send(message);
        } catch (Exception ex) {
            System.err.println("Lỗi gửi email xác nhận đơn hàng: " + ex.getMessage());
        }
    }
}
