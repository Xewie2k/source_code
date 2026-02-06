<template>
  <div class="container py-5 d-flex flex-column align-items-center justify-content-center min-vh-75">
    
    <div v-if="loading" class="text-center">
       <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>
        <p class="mt-3 text-muted">Đang xử lý kết quả...</p>
    </div>

    <div v-else class="text-center animate__animated animate__fadeInUp">
        <div class="mb-4">
            <div v-if="success" class="d-inline-flex align-items-center justify-content-center bg-success text-white rounded-circle shadow-sm" style="width: 100px; height: 100px;">
                <span class="material-icons" style="font-size: 60px;">check</span>
            </div>
             <div v-else class="d-inline-flex align-items-center justify-content-center bg-danger text-white rounded-circle shadow-sm" style="width: 100px; height: 100px;">
                <span class="material-icons" style="font-size: 60px;">priority_high</span>
            </div>
        </div>
        
        <h2 class="fw-bold mb-3">{{ success ? 'ĐẶT HÀNG THÀNH CÔNG!' : 'THANH TOÁN THẤT BẠI' }}</h2>
        
        <p class="text-secondary mb-4" style="max-width: 500px; margin: 0 auto;">
           {{ success 
              ? 'Cảm ơn bạn đã mua sắm tại SevenStrike. Đơn hàng của bạn đã được ghi nhận và sẽ sớm được xử lý.' 
              : 'Giao dịch thanh toán của bạn không thành công hoặc đã bị hủy. Vui lòng thử lại.' 
           }}
        </p>

        <div class="bg-light p-4 rounded-3 shadow-sm mx-auto mb-5 text-start" style="max-width: 450px;">
             <h6 class="fw-bold border-bottom pb-2 mb-3 text-uppercase text-muted" style="font-size: 12px;">Thông tin đơn hàng</h6>
             
             <!-- Case 1: COD Order ID available -->
             <div v-if="orderId" class="d-flex justify-content-between mb-2">
                 <span class="text-secondary">Mã đơn hàng:</span>
                 <span class="fw-bold">{{ orderCode ? '#' + orderCode : '#' + orderId }}</span>
             </div>

             <!-- Case 2: VNPAY Info -->
             <div v-if="transactionId" class="d-flex justify-content-between mb-2">
                 <span class="text-secondary">Mã giao dịch:</span>
                 <span class="fw-bold">{{ transactionId }}</span>
             </div>
             <div v-if="totalPrice" class="d-flex justify-content-between mb-2">
                 <span class="text-secondary">Tổng thanh toán:</span>
                 <span class="fw-bold text-danger">{{ formatPrice(totalPrice) }}</span>
             </div>
             
             <div v-if="orderInfo" class="mt-3 small text-muted fst-italic">
                "{{ orderInfo }}"
             </div>

             <!-- Hiển thị danh sách sản phẩm -->
             <div v-if="orderItems.length" class="mt-4 pt-3 border-top">
                 <h6 class="fw-bold mb-3 text-uppercase text-muted" style="font-size: 11px;">Sản phẩm đã đặt</h6>
                 <div v-for="item in orderItems" :key="item.id" class="d-flex gap-3 mb-3">
                     <img :src="item.anh || '/placeholder-shoe.png'" class="rounded border" style="width: 50px; height: 50px; object-fit: cover;">
                     <div class="flex-grow-1" style="font-size: 13px;">
                         <div class="fw-bold text-dark text-truncate" style="max-width: 200px;">{{ item.tenSanPham }}</div>
                         <div class="text-muted small">
                             {{ item.mauSac }} / {{ item.kichThuoc }} <br>
                             x{{ item.soLuong }}
                         </div>
                     </div>
                     <div class="fw-bold text-dark small">
                         {{ formatPrice(item.donGia) }}
                     </div>
                 </div>
             </div>
        </div>
        
        <div class="d-flex gap-3 justify-content-center">
            <router-link to="/client" class="btn btn-outline-danger rounded-1 px-4 py-2">
                TIẾP TỤC MUA SẮM
            </router-link>
            <router-link v-if="success" to="/client/account/orders" class="btn btn-danger rounded-1 px-4 py-2 text-white">
                XEM LỊCH SỬ ĐƠN HÀNG
            </router-link>
             <router-link v-if="!success" to="/client/cart" class="btn btn-danger rounded-1 px-4 py-2 text-white">
                QUAY LẠI GIỎ HÀNG
            </router-link>
        </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute } from 'vue-router';
import apiClient from '@/services/apiClient';

const route = useRoute();
const loading = ref(true);
const success = ref(false);

const orderId = ref(null);
const orderCode = ref(null);
const transactionId = ref(null);
const totalPrice = ref(null);
const orderInfo = ref(null);
const orderItems = ref([]);

const formatPrice = (value) => {
    
    let val = Number(value);
    if (isNaN(val)) return '0 đ';
    
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val);
};

onMounted(async () => {
    // Check params
    // 1. COD: ?id=...
    if (route.query.id) {
        orderId.value = route.query.id;
        success.value = true;
    }

    // 2. VNPAY: ?status=success|failed & ...
    else if (route.query.status) {
        success.value = route.query.status === 'success';
        transactionId.value = route.query.transactionId;
        
        // Xử lý giá từ backend redirect (nếu có)
        let p = Number(route.query.totalPrice);
        if (p > 1000000000) p = p / 100; // Heuristic: nếu giá quá lớn thì chia 100
        totalPrice.value = p;
        
        orderInfo.value = route.query.orderInfo;
        
        // Try to extract order ID from orderInfo if possible "Thanh toan don hang {id}"
        if (orderInfo.value) {
            const match = orderInfo.value.match(/\d+/);
            if (match) {
                orderId.value = match[0];
            }
        }
    }
    
    // 3. Xử lý kết quả trực tiếp từ VNPay (vnp_ResponseCode)
    else if (route.query.vnp_ResponseCode) {
        success.value = route.query.vnp_ResponseCode === '00';
        transactionId.value = route.query.vnp_TransactionNo;
        // vnp_Amount của VNPay luôn nhân 100, nên cần chia 100
        totalPrice.value = route.query.vnp_Amount ? Number(route.query.vnp_Amount) / 100 : 0;
        orderInfo.value = route.query.vnp_OrderInfo;
        
        if (orderInfo.value) {
            const match = orderInfo.value.match(/\d+/);
            if (match) orderId.value = match[0];
        }
    }

    // Fetch details to get Order Code
    if (orderId.value) {
        try {
            const res = await apiClient.get(`/api/client/orders/${orderId.value}`);
            if (res.data && res.data.maHoaDon) {
                orderCode.value = res.data.maHoaDon;
            }
            
            // Lấy danh sách sản phẩm
            const data = res.data;
            const details = data?.hoaDonChiTiets || data?.orderDetails || [];
            
            orderItems.value = details.map(d => {
                const ctsp = d.chiTietSanPham || {};
                const sp = ctsp.sanPham || {};
                const ms = ctsp.mauSac || {};
                const kt = ctsp.kichThuoc || {};
                
                let img = null;
                if (ctsp.anhChiTietSanPhams && ctsp.anhChiTietSanPhams.length > 0) {
                    img = ctsp.anhChiTietSanPhams[0].duongDanAnh;
                }

                return {
                    id: d.id,
                    tenSanPham: sp.tenSanPham || d.tenSanPham || 'Sản phẩm',
                    mauSac: ms.tenMauSac || '',
                    kichThuoc: kt.tenKichThuoc || '',
                    soLuong: d.soLuong,
                    donGia: d.donGia,
                    anh: img
                };
            });
        } catch (e) {
            console.error("Failed to fetch order details", e);
        }
    }

    loading.value = false;
});
</script>

<style scoped>
.min-vh-75 {
    min-height: 75vh;
}
</style>
