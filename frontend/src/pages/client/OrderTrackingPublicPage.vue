<template>
  <div class="container py-5">
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-danger" role="status"></div>
      <p class="mt-3 text-muted">Đang tải thông tin đơn hàng...</p>
    </div>

    <div v-else-if="error" class="text-center py-5">
      <div class="d-inline-flex align-items-center justify-content-center bg-warning text-dark rounded-circle shadow-sm mb-3" style="width: 80px; height: 80px;">
        <span class="material-icons" style="font-size: 40px;">error_outline</span>
      </div>
      <h5 class="fw-bold mb-2">Không tìm thấy đơn hàng</h5>
      <p class="text-muted mb-4">Link tra cứu không hợp lệ hoặc đã hết hạn.</p>
      <router-link to="/client" class="btn btn-danger px-4 py-2">
        <i class="bi bi-house me-2"></i>Về trang chủ
      </router-link>
    </div>

    <div v-else-if="order" class="bg-white p-4 shadow-sm rounded-3">
      <div class="d-flex justify-content-between align-items-center mb-4 pb-3 border-bottom">
        <h5 class="fw-bold mb-0">
          <i class="bi bi-box-seam me-2 text-danger"></i>Tra cứu đơn hàng
        </h5>
        <router-link to="/client" class="btn btn-outline-secondary btn-sm">
          <i class="bi bi-house me-1"></i>Trang chủ
        </router-link>
      </div>

      <!-- Header -->
      <div class="row mb-4 border-bottom pb-3">
        <div class="col-md-8">
          <div class="mb-2">
            <span class="fw-bold fs-5 me-2">Đơn hàng {{ order.maHoaDon }}</span>
          </div>
          <div class="text-muted mb-1">
            Trạng thái: <span class="text-danger fw-bold">{{ order.trangThai }}</span>
          </div>
        </div>
        <div class="col-md-4 text-end">
          <div class="fw-bold text-danger fs-4">{{ formatCurrency(order.tongTien) }}</div>
          <div class="text-muted small">{{ formatDate(order.ngayTao) }}</div>
        </div>
      </div>

      <!-- Timeline -->
      <div class="mb-5 px-3">
        <h6 class="fw-bold mb-4 border-start border-4 border-danger ps-2">Trạng thái đơn hàng</h6>
        <OrderTimeline :timeline="order.timeline" />
      </div>

      <!-- Receiver -->
      <div class="mb-5">
        <h6 class="fw-bold mb-3 border-start border-4 border-danger ps-2">Địa chỉ nhận hàng</h6>
        <div class="p-3 bg-light rounded border-start border-4">
          <div class="d-flex align-items-center mb-2">
            <i class="bi bi-person-circle me-2 text-secondary"></i>
            <span class="fw-bold">{{ order.tenNguoiNhan }}</span>
            <span class="mx-2 text-muted">|</span>
            <span>{{ order.soDienThoai }}</span>
          </div>
          <div class="d-flex align-items-start">
            <i class="bi bi-geo-alt-fill me-2 text-danger"></i>
            <span class="text-muted">{{ order.diaChi }}</span>
          </div>
        </div>
      </div>

      <!-- Items Table -->
      <div class="table-responsive mb-4">
        <table class="table table-hover align-middle">
          <thead class="bg-light text-secondary small text-uppercase">
            <tr>
              <th class="border-0 py-3 ps-3">#</th>
              <th class="border-0 py-3">Sản phẩm</th>
              <th class="border-0 py-3 text-end">Đơn giá</th>
              <th class="border-0 py-3 text-center">Số lượng</th>
              <th class="border-0 py-3 text-end pe-3">Thành tiền</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(item, index) in order.items" :key="index">
              <td class="ps-3 fw-bold text-muted">{{ index + 1 }}</td>
              <td>
                <div class="d-flex align-items-center">
                  <img :src="item.anhDaiDien || 'https://placehold.co/60x60'" class="rounded border me-3" width="60" height="60" style="object-fit: cover;">
                  <div>
                    <div class="fw-bold mb-1 text-dark">{{ item.tenSanPham }}</div>
                    <div class="text-muted small" style="font-size: 0.85rem;">{{ item.phanLoai }}</div>
                  </div>
                </div>
              </td>
              <td class="text-end fw-bold">{{ formatCurrency(item.donGia) }}</td>
              <td class="text-center">{{ item.soLuong }}</td>
              <td class="text-end fw-bold text-danger pe-3">{{ formatCurrency(item.thanhTien) }}</td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Summary -->
      <div class="row justify-content-end mb-5">
        <div class="col-md-5">
          <div class="bg-light p-3 rounded">
            <div class="d-flex justify-content-between mb-2">
              <span class="text-muted">Tạm tính</span>
              <span class="fw-bold">{{ formatCurrency(order.tamTinh) }}</span>
            </div>
            <div class="d-flex justify-content-between mb-2">
              <span class="text-muted">Phí vận chuyển</span>
              <span class="fw-bold">{{ formatCurrency(order.phiVanChuyen) }}</span>
            </div>
            <div class="d-flex justify-content-between mb-3 border-bottom pb-2">
              <span class="text-muted">Giảm giá</span>
              <span class="fw-bold text-success">- {{ formatCurrency(order.giamGia) }}</span>
            </div>
            <div class="d-flex justify-content-between align-items-center">
              <span class="fw-bold fs-5">Tổng cộng</span>
              <span class="fw-bold fs-4 text-danger">{{ formatCurrency(order.tongTien) }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Actions -->
      <div class="d-flex justify-content-between align-items-center pt-3 border-top">
        <router-link to="/client" class="btn btn-outline-dark">
          <i class="bi bi-house me-1"></i> Tiếp tục mua sắm
        </router-link>

        <button v-if="canCancel" class="btn btn-danger px-4 py-2 fw-bold" @click="cancelOrder">
          HỦY ĐƠN HÀNG
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import apiClient from '@/services/apiClient';
import OrderTimeline from '@/components/client/OrderTimeline.vue';

export default {
  name: "OrderTrackingPublicPage",
  components: {
    OrderTimeline
  },
  data() {
    return {
      order: null,
      loading: true,
      error: false
    }
  },
  computed: {
    canCancel() {
      if (!this.order) return false;
      return this.order.trangThai === 'Chờ xác nhận' || this.order.trangThai === 'Chờ giao hàng';
    }
  },
  mounted() {
    this.fetchOrderByToken();
  },
  watch: {
    '$route.query.token'() {
      this.fetchOrderByToken();
    }
  },
  methods: {
    async fetchOrderByToken() {
      const token = this.$route.query.token;
      this.loading = true;
      this.error = false;
      this.order = null;

      if (!token) {
        this.error = true;
        this.loading = false;
        return;
      }

      try {
        const res = await apiClient.get(`/api/client/orders/track?token=${encodeURIComponent(token)}`);
        this.order = res.data;
      } catch (err) {
        console.error("Failed to fetch order by token", err);
        this.error = true;
      } finally {
        this.loading = false;
      }
    },
    async cancelOrder() {
      if (!confirm('Bạn có chắc chắn muốn hủy đơn hàng này?')) return;
      try {
        await apiClient.post(`/api/client/orders/${this.order.id}/cancel`);
        alert('Đã hủy đơn hàng thành công');
        this.fetchOrderByToken();
      } catch (e) {
        alert('Lỗi hủy đơn hàng: ' + (e.response?.data?.userMessage || e.message || 'Có lỗi xảy ra'));
      }
    },
    formatDate(value) {
      if (!value) return '';
      const date = new Date(value);
      return date.toLocaleDateString('vi-VN') + ' ' + date.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' });
    },
    formatCurrency(value) {
      if (!value) return '0 đ';
      return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
    }
  }
}
</script>
