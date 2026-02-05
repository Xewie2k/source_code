<template>
  <div class="bg-white p-4 shadow-sm h-100">
    <div class="d-flex justify-content-between align-items-center mb-4 border-bottom pb-2">
      <h5 class="fw-bold m-0">Đơn mua</h5>
      <div class="text-muted small">Quản lý đơn hàng của bạn</div>
    </div>
    
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-danger" role="status"></div>
    </div>
    
    <div v-else-if="orders.length === 0" class="text-center py-5">
      <div class="mb-3">
          <i class="bi bi-clipboard-x text-muted" style="font-size: 3rem;"></i>
      </div>
      <p class="text-muted">Chưa có đơn hàng nào</p>
      <router-link to="/client" class="btn btn-danger">Tìm kiếm sản phẩm</router-link>
    </div>

    <div v-else>
      <div v-for="order in orders" :key="order.id" class="card mb-3 border">
        <div class="card-header bg-white border-bottom-0 d-flex justify-content-between align-items-center py-3">
            <div class="d-flex align-items-center">
                <span class="fw-bold me-3">Đơn hàng #{{ order.maHoaDon }}</span>
                <span class="text-muted small">| {{ formatDate(order.ngayTao) }}</span>
            </div>
            <span class="fw-bold text-uppercase" :class="getStatusColor(order.trangThai)">{{ order.trangThai }}</span>
        </div>
        <div class="card-body border-top">
            <div class="d-flex">
                <div class="me-3">
                    <img :src="order.anhDaiDien || 'https://placehold.co/80x80'" class="border rounded" width="80" height="80" style="object-fit: cover;">
                </div>
                <div class="flex-grow-1">
                    <h6 class="mb-1 text-dark fw-bold">{{ order.sanPhamDaiDien || 'Sản phẩm' }}</h6>
                    <div class="text-muted small" v-if="order.soLuongSanPham > 1">
                        + {{ order.soLuongSanPham - 1 }} sản phẩm khác
                    </div>
                </div>
                <div class="text-end">
                    <div class="text-muted small mb-1">Tổng tiền</div>
                    <div class="text-danger fw-bold fs-5">{{ formatCurrency(order.tongTien) }}</div>
                </div>
            </div>
        </div>
        <div class="card-footer bg-white border-top-0 d-flex justify-content-end pb-3 gap-2">
             <button 
                v-if="order.trangThai === 'Chờ xác nhận'" 
                class="btn btn-outline-secondary px-4"
                @click="cancelOrder(order)"
             >
                Hủy đơn hàng
             </button>
             <router-link :to="'/client/account/orders/' + order.id" class="btn btn-danger px-4">Xem chi tiết</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import apiClient from '@/services/apiClient';
import Swal from 'sweetalert2';

export default {
  name: "OrderHistoryPage",
  data() {
    return {
      orders: [],
      loading: true,
      customerId: 1 // Mock user
    }
  },
  mounted() {
    this.fetchOrders();
  },
  methods: {
    async fetchOrders() {
      try {
        const res = await apiClient.get(`/api/client/orders?customerId=${this.customerId}`);
        this.orders = res.data;
      } catch (error) {
        console.error("Failed to fetch orders", error);
      } finally {
        this.loading = false;
      }
    },
    async cancelOrder(order) {
        const result = await Swal.fire({
            title: 'Hủy đơn hàng?',
            text: "Bạn có chắc chắn muốn hủy đơn hàng này không?",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#d33',
            cancelButtonColor: '#3085d6',
            confirmButtonText: 'Đồng ý hủy',
            cancelButtonText: 'Không'
        });

        if (result.isConfirmed) {
            try {
                await apiClient.post(`/api/client/orders/${order.id}/cancel?reason=KhachHangHuy`);
                Swal.fire(
                    'Đã hủy!',
                    'Đơn hàng đã được hủy thành công.',
                    'success'
                );
                this.fetchOrders();
            } catch (e) {
                console.error(e);
                Swal.fire(
                    'Lỗi!',
                    'Không thể hủy đơn hàng. Vui lòng thử lại.',
                    'error'
                );
            }
        }
    },
    formatDate(value) {
      if (!value) return '';
      const date = new Date(value);
      return date.toLocaleDateString('vi-VN');
    },
    formatCurrency(value) {
      if (!value) return '0 đ';
      return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
    },
    getStatusColor(status) {
        if (status === 'Hoàn thành' || status === 'Đã giao hàng') return 'text-success';
        if (status === 'Đã hủy') return 'text-secondary';
        if (status === 'Chờ xác nhận') return 'text-warning';
        return 'text-danger'; // Default for processing
    }
  }
}
</script>
