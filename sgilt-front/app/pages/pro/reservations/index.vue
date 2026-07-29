<template>
  <div class="pro-board">
    <!-- ── Header mobile (masqué desktop) ───────────────────────────────────────── -->
    <div class="header-mobile">
      <ProBoardGreeting :loading="loading" :counts="boardCounts" @filter="activeFilter = $event" />
    </div>

    <!-- ── Filtres sticky ────────────────────────────────────────────────────────── -->
    <div class="filters">
      <ProStatusPills v-model="activeFilter" />
    </div>

    <!-- ── Corps ────────────────────────────────────────────────────────────────── -->
    <div class="body">
      <!-- Sidebar desktop (masquée mobile) -->
      <aside class="sidebar">
        <ProBoardGreeting
          :loading="loading"
          :counts="boardCounts"
          @filter="activeFilter = $event"
        />
      </aside>

      <!-- Liste des bookings -->
      <div class="bookings-list">
        <template v-if="loading">
          <ProBookingCard v-for="i in 4" :key="i" skeleton />
        </template>

        <div v-else-if="filteredReservations.length === 0" class="empty">
          <p class="empty-title">{{ $t('pro.reservations.empty-title') }}</p>
          <i18n-t keypath="pro.reservations.empty-text" tag="p" class="empty-text" scope="global">
            <template #link>
              <NuxtLink to="/pro/page-edition">{{ $t('pro.reservations.empty-link') }}</NuxtLink>
            </template>
          </i18n-t>
        </div>

        <ProBookingCard
          v-for="(reservation, index) in filteredReservations"
          v-else
          :key="reservation.id"
          :reservation="reservation"
          :animation-delay="index * 60"
          @click="navigateTo(`/pro/reservations/${reservation.id}`)"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
definePageMeta({ layout: 'pro' })

import { useProReservations } from '~/data/reservation/useProReservations'
import type { ReservationStatut } from '~/constants/reservation-status'
import ProStatusPills from '~/components/pro/ProStatusPills.vue'

// ── Données ────────────────────────────────────────────────────────────────────
const { reservations, boardCounts, loading } = useProReservations()

const activeFilter = ref<ReservationStatut | null>(null)

// ── Filtrage ───────────────────────────────────────────────────────────────────
const filteredReservations = computed(() =>
  reservations.value.filter((r) => activeFilter.value === null || r.statut === activeFilter.value),
)
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

$desktop: $breakpoint-desktop;
$filter-h: 50px;

.pro-board {
  min-height: 100%;
  background-color: $brand-background-alt;
  display: flex;
  flex-direction: column;

  // ── Header mobile ──────────────────────────────────────────────────────────────
  .header-mobile {
    background: #fff;
    padding: $spacing-m $spacing-m $spacing-s;
    display: flex;
    flex-direction: column;
    gap: 4px;

    @media (min-width: $desktop) {
      display: none;
    }
  }

  // ── Filtres sticky ─────────────────────────────────────────────────────────────
  .filters {
    position: sticky;
    top: $app-header-height;
    z-index: $z-header;
    background: #fff;
    padding: $spacing-s $spacing-m;
    border-bottom: 1px solid rgba(0, 0, 0, 0.06);
    margin-bottom: $spacing-s;

    @media (min-width: $desktop) {
      padding: $spacing-s max($spacing-xl, calc((100% - 1200px) / 2));
    }
  }

  // ── Corps ─────────────────────────────────────────────────────────────────────
  .body {
    display: flex;
    flex-direction: column;
    flex: 1;
    padding: 0 $spacing-xs;

    @media (min-width: $desktop) {
      display: grid;
      grid-template-columns: 400px 1fr;
      gap: $spacing-xl;
      padding: $spacing-l max($spacing-xl, calc((100% - 1200px) / 2)) $spacing-xl;
    }
  }

  // ── Sidebar desktop ────────────────────────────────────────────────────────────
  .sidebar {
    display: none;

    @media (min-width: $desktop) {
      display: block;
      position: sticky;
      top: calc($app-header-height + $filter-h + $spacing-l);
      min-height: 15rem;
      align-self: start;
      background: #fff;
      border-radius: $radius-md;
      border: 1px solid $divider-color;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
      padding: $spacing-m;
    }
  }
}

// ── Liste des cards ────────────────────────────────────────────────────────────
.bookings-list {
  display: flex;
  flex-direction: column;
  gap: $spacing-s;

  @media (min-width: $desktop) {
    gap: $spacing-m;
    padding: 0;
  }

  .empty {
    display: flex;
    flex-direction: column;
    gap: $spacing-xs;
    text-align: center;
    padding: $spacing-xl $spacing-m;
  }

  .empty-title {
    font-size: 1rem;
    font-weight: $font-weight-medium;
    color: $text-primary;
    margin: 0;
  }

  .empty-text {
    font-size: 0.875rem;
    color: $text-secondary;
    margin: 0;

    a {
      color: $color-primary;
      text-decoration: underline;
    }
  }
}
</style>
