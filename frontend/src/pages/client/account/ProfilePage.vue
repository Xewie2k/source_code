<template>
  <div>
    <h5 class="fw-bold mb-4">Hồ sơ của tôi</h5>
    <div v-if="loading" class="text-center py-5">
        <div class="spinner-border text-danger" role="status"></div>
    </div>
    <div v-else class="row">
        <div class="col-md-4 text-center mb-4">
            <div class="d-inline-block position-relative">
                 <img :src="profile.anhDaiDien || 'https://placehold.co/150x150'" class="rounded-circle border" width="150" height="150" style="object-fit: cover;">
                 <button class="btn btn-sm btn-light border position-absolute bottom-0 end-0 rounded-circle shadow-sm" style="width: 32px; height: 32px;">
                    <i class="bi bi-camera"></i>
                 </button>
            </div>
            <div class="mt-3 fw-bold">{{ profile.tenKhachHang }}</div>
            <div class="text-secondary small">CHỌN ẢNH</div>
        </div>
        <div class="col-md-8">
             <div class="mb-3">
                <label class="form-label fw-bold text-secondary small">Tên khách hàng</label>
                <input type="text" class="form-control" v-model="profile.tenKhachHang" readonly>
             </div>
             <div class="mb-3">
                <label class="form-label fw-bold text-secondary small">Email</label>
                 <div class="input-group">
                    <input type="email" class="form-control" v-model="profile.email" readonly>
                     <span class="input-group-text bg-light text-success fw-bold" style="font-size: 12px;">Đã xác thực</span>
                 </div>
             </div>
             <div class="mb-3">
                <label class="form-label fw-bold text-secondary small">Số điện thoại</label>
                <input type="text" class="form-control" v-model="profile.soDienThoai" readonly>
             </div>
             
             <div class="mt-4 p-3 bg-light rounded border">
                 <h6 class="fw-bold mb-3">Thông tin nhanh</h6>
                 <div class="small">Email: <span class="text-muted">{{ profile.email }}</span></div>
                 <div class="small">Điện thoại: <span class="text-muted">{{ profile.soDienThoai }}</span></div>
                 <div class="small">Vai trò: <span class="text-muted">Khách hàng</span></div>
             </div>
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
