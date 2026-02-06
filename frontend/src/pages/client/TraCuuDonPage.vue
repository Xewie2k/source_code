<template>
  <div class="container py-5">
    <div class="row justify-content-center">
      <div class="col-md-6">
        <div class="bg-white p-4 shadow-sm rounded-3">
          <h5 class="fw-bold mb-4">
            <i class="bi bi-search me-2 text-danger"></i>Tra cứu đơn hàng
          </h5>
          <p class="text-muted small mb-4">
            Nhập mã tra cứu từ email xác nhận đơn hàng để xem trạng thái đơn hàng của bạn.
          </p>
          <form @submit.prevent="handleSubmit">
            <div class="mb-3">
              <label class="form-label fw-bold">Mã tra cứu</label>
              <input
                v-model="token"
                type="text"
                class="form-control form-control-lg"
                placeholder="Dán mã từ email vào đây"
                required
              />
            </div>
            <button type="submit" class="btn btn-danger w-100 py-2 fw-bold">
              <i class="bi bi-search me-2"></i>Xem trạng thái đơn hàng
            </button>
          </form>
          <p class="text-muted small mt-3 mb-0">
            Không có tài khoản? Bạn vẫn tra cứu được bằng mã gửi qua email sau khi đặt hàng.
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const token = ref('');

const handleSubmit = () => {
  const t = token.value?.trim();
  if (!t) return;
  router.push({ path: '/client/track-order', query: { token: t } });
};
</script>
