<template>
  <div>
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h5 class="fw-bold m-0">Địa chỉ của tôi</h5>
        <button class="btn btn-danger btn-sm rounded-1 text-white" disabled>
            <i class="bi bi-plus"></i> Thêm địa chỉ mới
        </button>
    </div>

    <div v-if="loading" class="text-center py-5">
        <div class="spinner-border text-danger" role="status"></div>
    </div>

    <div v-else-if="addresses.length > 0" class="d-flex flex-column gap-3">
        <div v-for="addr in addresses" :key="addr.id" class="p-3 border rounded bg-white position-relative">
            <div class="d-flex justify-content-between">
                <div>
                    <div class="d-flex align-items-center mb-1">
                        <span class="fw-bold">{{ profileName }}</span>
                        <span v-if="addr.macDinh" class="ms-2 badge bg-danger rounded-1">Mặc định</span>
                    </div>
                    <div class="text-secondary small mb-1">{{ profilePhone }}</div>
                    <div class="text-muted small">{{ addr.diaChiCuThe }}</div>
                    <div class="text-muted small">{{ addr.phuong }}, {{ addr.quan }}, {{ addr.thanhPho }}</div>
                </div>
                <div class="d-flex flex-column align-items-end justify-content-between">
                     <div class="d-flex gap-2">
                         <button class="btn btn-link text-decoration-none p-0 small">Cập nhật</button>
                         <button v-if="!addr.macDinh" class="btn btn-link text-decoration-none p-0 small text-danger">Xóa</button>
                     </div>
                     <button v-if="!addr.macDinh" class="btn btn-outline-secondary btn-sm rounded-1" disabled>Thiết lập mặc định</button>
                </div>
            </div>
        </div>
    </div>
    <div v-else class="text-center py-5 text-muted">
        Chưa có địa chỉ nào.
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import apiClient from '@/services/apiClient';

const loading = ref(true);
const addresses = ref([]);
const profileName = ref('Nguyễn Văn A');
const profilePhone = ref('');

onMounted(async () => {
    try {
        // Hardcoded ID 1 for demo
        const [resAddr, resProf] = await Promise.all([
             apiClient.get('/api/client/address/1'),
             apiClient.get('/api/client/profile/1')
        ]);
        addresses.value = resAddr.data;
        if (resProf.data) {
            profileName.value = resProf.data.tenKhachHang;
            profilePhone.value = resProf.data.soDienThoai;
        }
    } catch (e) {
        console.error(e);
    } finally {
        loading.value = false;
    }
});
</script>
