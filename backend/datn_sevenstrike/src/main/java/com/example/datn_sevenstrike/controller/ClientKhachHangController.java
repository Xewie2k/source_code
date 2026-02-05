package com.example.datn_sevenstrike.controller;

import com.example.datn_sevenstrike.dto.request.KhachHangRequest;
import com.example.datn_sevenstrike.dto.response.KhachHangResponse;
import com.example.datn_sevenstrike.service.KhachHangService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/client/khach-hang")
@RequiredArgsConstructor
@Validated
public class ClientKhachHangController {

    private final KhachHangService service;

    @GetMapping("/demo-user")
    public KhachHangResponse getDemoUser() {
        return service.getDemoUser();
    }

    @GetMapping("/{id:\\d+}")
    public KhachHangResponse one(@PathVariable("id") Integer id) {
        return service.one(id);
    }

    @PutMapping("/{id:\\d+}")
    public KhachHangResponse update(@PathVariable("id") Integer id, @Valid @RequestBody KhachHangRequest req) {
        return service.update(id, req);
    }
}
