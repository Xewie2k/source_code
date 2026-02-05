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
            <!-- Removed redundant "CHỌN ẢNH" text if sidebar has one or vice versa, but sticking to plan to improve this page -->
            <div class="text-secondary small mt-1">Cập nhật ảnh</div>
        </div>
        <div class="col-md-8">
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
             
             <!-- Addresses Section -->
             <div class="mt-4">
                <h6 class="fw-bold mb-3 border-bottom pb-2">Danh sách địa chỉ</h6>
                <div v-if="addresses.length === 0" class="text-muted small">Chưa có địa chỉ nào.</div>
                <div v-else class="d-flex flex-column gap-3">
                    <div v-for="addr in addresses" :key="addr.id" class="p-3 border rounded bg-white shadow-sm position-relative">
                        <div class="d-flex justify-content-between align-items-start">
                            <div>
                                <span class="fw-bold text-dark">{{ addr.tenDiaChi }}</span>
                                <div class="small text-secondary mt-1">
                                    {{ addr.diaChiCuThe }}, {{ addr.phuong }}, {{ addr.quan }}, {{ addr.thanhPho }}
                                </div>
                            </div>
                            <span v-if="addr.macDinh" class="badge bg-danger rounded-pill">Mặc định</span>
                        </div>
                    </div>
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import apiClient from '@/services/apiClient';

const loading = ref(true);
const profile = ref({});
const addresses = ref([]);

onMounted(async () => {
    try {
        // Hardcoded ID 1 for demo
        const res = await apiClient.get('/api/client/profile/1');
        profile.value = res.data;

        const resAddr = await apiClient.get('/api/client/address/1');
        addresses.value = resAddr.data;
    } catch (e) {
        console.error(e);
    } finally {
        loading.value = false;
    }
});
</script>
