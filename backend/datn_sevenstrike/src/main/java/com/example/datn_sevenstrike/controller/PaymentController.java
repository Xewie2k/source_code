package com.example.datn_sevenstrike.controller;

import com.example.datn_sevenstrike.config.VNPayConfig;
import com.example.datn_sevenstrike.dto.client.ClientOrderDetailDTO;
import com.example.datn_sevenstrike.entity.HoaDon;
import com.example.datn_sevenstrike.repository.HoaDonRepository;
import com.example.datn_sevenstrike.service.BanHangOnlineService;
import com.example.datn_sevenstrike.service.OrderEmailService;
import com.example.datn_sevenstrike.service.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final VNPayService vnPayService;
    private final VNPayConfig vnPayConfig;
    private final HoaDonRepository hoaDonRepo;
    private final BanHangOnlineService banHangOnlineService;
    private final OrderEmailService orderEmailService;

    @PostMapping("/create_payment")
    public ResponseEntity<?> createPayment(@RequestBody Map<String, Object> req) {
        try {
            int amount = Integer.parseInt(req.get("amount").toString());
            String orderInfo = req.get("orderInfo") != null ? req.get("orderInfo").toString() : "Thanh toan don hang";
            String urlReturn = req.get("returnUrl") != null ? req.get("returnUrl").toString() : null;

            String paymentUrl = vnPayService.createOrder(amount, orderInfo, urlReturn);
            Map<String, String> response = new HashMap<>();
            response.put("paymentUrl", paymentUrl);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating payment: " + e.getMessage());
        }
    }

    @GetMapping("/vnpay_return")
    public void vnpayReturn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int paymentStatus = vnPayService.orderReturn(request);

        String orderInfoRaw = request.getParameter("vnp_OrderInfo");
        String orderInfo = orderInfoRaw != null ? URLDecoder.decode(orderInfoRaw, StandardCharsets.UTF_8) : "";
        String transactionId = request.getParameter("vnp_TransactionNo");
        String totalPrice = request.getParameter("vnp_Amount");

        // Khi thanh toán thành công: cập nhật ngày thanh toán và gửi email cho khách hàng
        if (paymentStatus == 1) {
            Integer orderId = parseOrderIdFromOrderInfo(orderInfo);
            if (orderId != null) {
                hoaDonRepo.findById(orderId).ifPresent(hd -> {
                    hd.setNgayThanhToan(LocalDateTime.now());
                    hoaDonRepo.save(hd);

                    String email = hd.getEmailKhachHang();
                    if (email != null && !email.isBlank()) {
                        ClientOrderDetailDTO orderDetail = banHangOnlineService.getOrderDetail(orderId);
                        String trackingUrl = banHangOnlineService.buildTrackingUrl(hd.getTrackingToken());
                        orderEmailService.sendOrderConfirmationEmail(email.trim(), hd, orderDetail, trackingUrl);
                    }
                });
            }
        }

        // Redirect to Frontend
        String baseUrl = vnPayConfig.getVnp_ReturnUrl();
        String redirectUrl = baseUrl + "?status=" + (paymentStatus == 1 ? "success" : "failed") +
                "&orderInfo=" + (orderInfo != null ? java.net.URLEncoder.encode(orderInfo, StandardCharsets.UTF_8) : "") +
                "&totalPrice=" + (totalPrice != null ? totalPrice : "") +
                "&transactionId=" + (transactionId != null ? transactionId : "");

        response.sendRedirect(redirectUrl);
    }

    /** Trích xuất order ID từ vnp_OrderInfo (vd: "Thanh toan don hang 123" -> 123) */
    private Integer parseOrderIdFromOrderInfo(String orderInfo) {
        if (orderInfo == null || orderInfo.isBlank()) return null;
        Matcher m = Pattern.compile("\\d+").matcher(orderInfo);
        return m.find() ? Integer.parseInt(m.group()) : null;
    }

}
