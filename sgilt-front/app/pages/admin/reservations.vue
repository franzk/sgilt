<template>
  <div class="admin-page">
    <div class="page-header">
      <h1>{{ $t('admin.reservations.title') }}</h1>
      <select v-model="selectedStatus" @change="onStatusChange">
        <option :value="null">{{ $t('admin.reservations.filter-all') }}</option>
        <option v-for="status in ADMIN_RESERVATION_STATUSES" :key="status" :value="status">
          {{ $t(`admin.reservations.status.${status}`) }}
        </option>
      </select>
    </div>

    <section class="list">
      <p v-if="loading">{{ $t('admin.reservations.loading') }}</p>
      <p v-else-if="rows.length === 0" class="empty">{{ $t('admin.reservations.empty') }}</p>
      <template v-else>
        <table class="reservations-table">
          <thead>
            <tr>
              <th>{{ $t('admin.reservations.table.event') }}</th>
              <th>{{ $t('admin.reservations.table.organizer') }}</th>
              <th>{{ $t('admin.reservations.table.provider') }}</th>
              <th>{{ $t('admin.reservations.table.status') }}</th>
              <th>{{ $t('admin.reservations.table.created-at') }}</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="row in rows" :key="row.id">
              <td>{{ row.eventTitle }}</td>
              <td>{{ row.organizerEmail }}</td>
              <td>
                <div class="provider-cell">
                  <span>{{ row.providerEmail }}</span>
                  <span class="slug">{{ row.providerSlug }}</span>
                </div>
              </td>
              <td><AdminReservationStatusBadge :status="row.status" /></td>
              <td>{{ formatDateTime(row.createdAt) }}</td>
            </tr>
          </tbody>
        </table>

        <ul class="reservations-cards">
          <li v-for="row in rows" :key="row.id" class="card">
            <p class="event">{{ row.eventTitle }}</p>
            <p class="field">
              <span class="label">{{ $t('admin.reservations.table.organizer') }}</span>
              <span class="value">{{ row.organizerEmail }}</span>
            </p>
            <p class="field">
              <span class="label">{{ $t('admin.reservations.table.provider') }}</span>
              <span class="value">{{ row.providerEmail }} · {{ row.providerSlug }}</span>
            </p>
            <div class="footer">
              <AdminReservationStatusBadge :status="row.status" />
              <span class="date">{{ formatDateTime(row.createdAt) }}</span>
            </div>
          </li>
        </ul>
      </template>
    </section>
  </div>
</template>

<script setup lang="ts">
import AdminReservationStatusBadge from '~/components/admin/AdminReservationStatusBadge.vue'
import { ADMIN_RESERVATION_STATUSES } from '~/data/admin/domain/AdminReservationStatus'
import type { AdminReservationStatus } from '~/data/admin/domain/AdminReservationStatus'
import { formatDateTime } from '~/utils/dateUtils'

definePageMeta({ layout: 'admin' })

const { rows, loading, setStatusFilter, load } = useAdminReservations()

const selectedStatus = ref<AdminReservationStatus | null>(null)

async function onStatusChange() {
  await setStatusFilter(selectedStatus.value)
}

onMounted(() => load())
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.admin-page {
  display: flex;
  flex-direction: column;
  gap: $spacing-l;
  padding: $spacing-l;
  max-width: 960px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: $spacing-s;

  h1 {
    margin: 0;
  }

  select {
    padding: $spacing-xs $spacing-s;
    border: 1px solid $divider-color;
    border-radius: $radius-sm;
    font-family: inherit;
    font-size: 0.85rem;
  }
}

.list {
  .empty {
    color: $text-secondary;
  }
}

.reservations-table {
  display: none;
  width: 100%;
  border-collapse: collapse;
  font-size: 0.85rem;

  @media (min-width: $breakpoint-desktop) {
    display: table;
  }

  th,
  td {
    text-align: left;
    padding: $spacing-xs $spacing-s;
    border-bottom: 1px solid $divider-color;
  }

  th {
    color: $text-secondary;
    font-weight: 500;
    font-size: 0.75rem;
    text-transform: uppercase;
  }

  .provider-cell {
    display: flex;
    flex-direction: column;
    gap: 2px;

    .slug {
      font-size: 0.75rem;
      color: $text-secondary;
    }
  }
}

.reservations-cards {
  display: flex;
  flex-direction: column;
  gap: $spacing-xs;
  list-style: none;
  margin: 0;
  padding: 0;
  width: 100%;

  @media (min-width: $breakpoint-desktop) {
    display: none;
  }

  .card {
    display: flex;
    flex-direction: column;
    gap: 4px;
    min-width: 0;
    padding: $spacing-s;
    border-radius: $radius-md;
    background: $surface-soft;
  }

  .event {
    margin: 0;
    font-weight: 600;
  }

  .field {
    display: flex;
    flex-direction: column;
    margin: 0;
    min-width: 0;

    .label {
      font-size: 0.7rem;
      text-transform: uppercase;
      color: $text-secondary;
    }

    .value {
      font-size: 0.85rem;
      overflow-wrap: anywhere;
    }
  }

  .footer {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: $spacing-xs;
    margin-top: 4px;

    .date {
      font-size: 0.75rem;
      color: $text-secondary;
    }
  }
}
</style>
