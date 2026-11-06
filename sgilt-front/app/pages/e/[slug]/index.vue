<!--
  Fiche publique d'un événement (/e/[slug]) : présentation de l'événement (PublicEventView) et,
  s'il a une billetterie, son bloc d'achat (TicketingPurchaseCard) selon l'état de celle-ci.
  Événement chargé par usePublicEvent (slug inconnu : 404).
-->
<template>
  <PublicEventView :event="event" shareable>
    <template v-if="event.ticketing" #purchase>
      <TicketingPurchaseCard :slug="slug" :ticketing="event.ticketing" />
    </template>
  </PublicEventView>
</template>

<script setup lang="ts">
import PublicEventView from '~/components/ticketing/PublicEventView.vue'
import TicketingPurchaseCard from '~/components/ticketing/TicketingPurchaseCard.vue'
import { usePublicEvent } from '~/composables/usePublicEvent'

const route = useRoute()
const slug = String(route.params.slug)

const event = await usePublicEvent(slug)

useHead({ title: event.title })

const { toUrl } = useImageUrl()
useSeoMeta({
  ogTitle: `${event.title} · Sgilt`,
  ogImage: toUrl(event.heroImage),
})
</script>
