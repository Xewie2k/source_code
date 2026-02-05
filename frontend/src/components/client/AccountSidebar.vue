<template>
  <div class="account-sidebar bg-white p-3">
    <div class="text-center mb-4">
      <div class="mb-2">
        <img :src="avatarUrl" class="rounded-circle border" width="80" height="80" alt="Avatar" style="object-fit: cover;">
      </div>
      <h6 class="fw-bold mb-1">{{ userName }}</h6>
    </div>
    
    <div class="menu">
        <div class="mb-2">
             <div class="d-flex align-items-center mb-1 fw-bold text-dark text-decoration-none">
                <i class="bi bi-person me-2"></i> Tài khoản của tôi
             </div>
             <div class="ps-4 d-flex flex-column">
                 <router-link to="/client/account/profile" class="text-decoration-none text-muted mb-1" active-class="text-danger fw-bold">Hồ sơ</router-link>
                 <router-link to="/client/account/address" class="text-decoration-none text-muted mb-1" active-class="text-danger fw-bold">Địa chỉ</router-link>
                 <router-link to="/client/account/password" class="text-decoration-none text-muted mb-1" active-class="text-danger fw-bold">Đổi mật khẩu</router-link>
             </div>
        </div>

        <router-link to="/client/account/orders" class="d-flex align-items-center mb-2 text-decoration-none text-dark" active-class="text-danger fw-bold">
            <i class="bi bi-receipt me-2"></i> <span>Đơn mua</span>
        </router-link>

        <router-link to="/client/account/coupons" class="d-flex align-items-center mb-2 text-decoration-none text-dark" active-class="text-danger fw-bold">
            <i class="bi bi-ticket-perforated me-2"></i> <span>Phiếu giảm giá</span>
        </router-link>
    </div>
  </div>
</template>

<script>
import apiClient from '@/services/apiClient';

export default {
  name: "AccountSidebar",
  data() {
    return {
      userName: 'Loading...',
      avatarUrl: 'https://placehold.co/100x100'
    }
  },
  async mounted() {
    // SIMULATION: Giả định đã đăng nhập là Nguyễn Văn A (ID=1)
    localStorage.setItem('userId', '1');
    localStorage.setItem('userRole', 'CLIENT');

    // FIX: Backend chưa có endpoint /api/client/profile/1 nên tạm thời hardcode dữ liệu
    // để tránh lỗi 500 "No static resource"
    this.userName = 'Nguyễn Văn A';
    this.avatarUrl = `https://ui-avatars.com/api/?name=${encodeURIComponent(this.userName)}&background=random&color=fff`;
  }
}
</script>

<style scoped>
.menu a:hover {
    color: #dc3545 !important;
}
</style>
