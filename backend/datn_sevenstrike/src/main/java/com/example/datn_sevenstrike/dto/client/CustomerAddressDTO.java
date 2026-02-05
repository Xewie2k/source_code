package com.example.datn_sevenstrike.dto.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerAddressDTO {
    private Integer id;
    private String tenDiaChi;
    private String diaChiCuThe;
    private String phuong;
    private String quan;
    private String thanhPho;
    private Boolean macDinh;
}
