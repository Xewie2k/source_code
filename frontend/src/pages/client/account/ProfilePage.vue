<template>
  <div>
    <h5 class="fw-bold mb-4">Hồ sơ của tôi</h5>
    <div v-if="loading" class="text-center py-5">
        <div class="spinner-border text-danger" role="status"></div>
    </div>
    <div v-else>
         <div class="mb-3">
            <label class="form-label fw-bold text-secondary small">Tên khách hàng</label>
            <input type="text" class="form-control" v-model="profile.tenKhachHang" readonly>
         </div>
         
         <div class="row">
             <div class="col-md-6 mb-3">
                <label class="form-label fw-bold text-secondary small">Email</label>
                 <div class="input-group">
                    <input type="email" class="form-control" v-model="profile.email" readonly>
                     <span class="input-group-text bg-light text-success fw-bold" style="font-size: 12px;">Đã xác thực</span>
                 </div>
             </div>
             <div class="col-md-6 mb-3">
                <label class="form-label fw-bold text-secondary small">Số điện thoại</label>
                <input type="text" class="form-control" v-model="profile.soDienThoai" readonly>
             </div>
         </div>

         <div class="row">
             <div class="col-md-6 mb-3">
                <label class="form-label fw-bold text-secondary small">Giới tính</label>
                <div class="d-flex gap-3 align-items-center h-100">
                    <div class="form-check">
                        <input class="form-check-input" type="radio" :checked="profile.gioiTinh === true" disabled>
                        <label class="form-check-label">Nam</label>
                    </div>
                    <div class="form-check">
                        <input class="form-check-input" type="radio" :checked="profile.gioiTinh === false" disabled>
                        <label class="form-check-label">Nữ</label>
                    </div>
                </div>
             </div>
             <div class="col-md-6 mb-3">
                <label class="form-label fw-bold text-secondary small">Ngày sinh</label>
                <input type="date" class="form-control" v-model="profile.ngaySinh" readonly>
             </div>
         </div>
         
         <div class="mt-4 p-3 bg-light rounded border">
             <h6 class="fw-bold mb-3">Thông tin nhanh</h6>
             <div class="small">Email: <span class="text-muted">{{ profile.email }}</span></div>
             <div class="small">Điện thoại: <span class="text-muted">{{ profile.soDienThoai }}</span></div>
             <div class="small">Vai trò: <span class="text-muted">Khách hàng</span></div>
         </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import apiClient from '@/services/apiClient';

const loading = ref(true);
const profile = ref({});

onMounted(async () => {
    try {
        // Hardcoded ID 1 for demo
        const res = await apiClient.get('/api/client/profile/1');
        profile.value = res.data;
    } catch (e) {
        console.error(e);
    } finally {
        loading.value = false;
    }
});
</script>
