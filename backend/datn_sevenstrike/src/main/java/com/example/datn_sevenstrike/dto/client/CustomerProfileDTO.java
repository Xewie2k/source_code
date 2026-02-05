package com.example.datn_sevenstrike.dto.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerProfileDTO {
    private Integer id;
    private String tenKhachHang;
    private String email;
    private String soDienThoai;
    private Boolean gioiTinh;
    private LocalDate ngaySinh;
}
