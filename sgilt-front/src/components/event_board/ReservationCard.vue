<template>
  <div class="boarding-pass">
    <div class="header">
      <CategoryChip :categoryName="reservation?.partner.tags[0]?.category || ''" />
      <span class="title">{{ reservation?.partner?.title }}</span>
    </div>
    <div class="content">
      <div class="left">
        <div class="partnerImage">
          <img :src="reservation?.partner.imageUrl" alt="Logo du partenaire" />
        </div>
        <div class="reservation-status">STATUS</div>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import type { Reservation } from '@/data/domain/Reservation'
import CategoryChip from '@/components/basics/chips/CategoryChip.vue'

defineProps<{
  reservation?: Reservation
}>()
</script>

<style scoped lang="scss">
.boarding-pass {
  display: flex;
  flex-direction: column;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  width: 400px;
  border-left: 6px solid #333;
  position: relative;
}

.header {
  background: var(--status-color);
  color: white;
  font-weight: bold;
  padding: 10px;
  text-align: center;
  font-size: 1.2em;
}

.content {
  display: flex;
  padding: 15px;
  justify-content: space-between;
}

.left {
  flex: 1;
}

.provider {
  font-size: 1.3em;
  font-weight: bold;
  margin-bottom: 8px;
}

.info {
  font-size: 0.95em;
  margin-bottom: 5px;
}

.qr-code img {
  width: 60px;
  height: 60px;
  opacity: 0.8;
}

/* 🎨 Statuts */
.status-pending {
  --status-color: #f5a623;
}
.status-warning {
  --status-color: #ffcc00;
}
.status-success {
  --status-color: #28a745;
}
.status-danger {
  --status-color: #dc3545;
}
</style>
