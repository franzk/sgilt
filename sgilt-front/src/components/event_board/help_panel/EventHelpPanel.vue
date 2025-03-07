<template>
  <div class="event-help-panel">
    <h3>🎯 Comment ça marche ?</h3>
    <span class="close" @click="$emit('close')"><SgiltIcon icon="Close" /></span>
    <ul>
      <li>
        <EventHelpItem
          icon="Mail"
          title="Vous envoyez votre demande"
          content="→ Votre partenaire vous répond sous 24h."
        />
      </li>
      <li>
        <EventHelpItem
          icon="Eye"
          title="Votre partenaire consulte votre demande"
          content="→ Il vous contacte pour valider les détails, comprendre vos attentes et confirmer sa disponibilité avant d’accepter ou refuser."
        />
      </li>
      <li>
        <EventHelpItem
          icon="Check"
          title="Votre demande est acceptée"
          content="→ Vous avez 48h pour finaliser le paiement et sécuriser votre réservation. Passé ce délai, la demande expire automatiquement."
        />
      </li>
      <li>
        <EventHelpItem
          icon="Calendar"
          title="C’est réservé !"
          content="→ Une fois le paiement validé, votre réservation est confirmée !"
        />
      </li>
    </ul>
    <p class="warning">
      ⚠️ N’oubliez pas ! Si le paiement n’est pas effectué dans les 48h après acceptation, la
      réservation est annulée.
    </p>

    <div class="divider" />

    <h3>📞 Assistance & Contact</h3>
    <p>
      <EventHelpItem icon="Mail" title="Support client :" content="support@sgilt.com" oneline />
    </p>
    <p>
      <EventHelpItem icon="Phone" title="Assistance téléphonique" content="+33 1 23 45 67 89" />
    </p>

    <div class="divider" />

    <h3>💡 Questions fréquentes</h3>
    <p>
      <EventHelpItem
        icon="CreditCard"
        title="Comment payer ma réservation ?"
        content="→ Depuis l’event board, cliquez sur 'Payer'."
      />
    </p>
    <p>
      <EventHelpItem
        icon="Cancel"
        title="Puis-je annuler une réservation ?"
        content="→ Oui, tant qu’elle n’est pas payée."
      />
    </p>
  </div>
</template>

<script setup lang="ts">
import { defineEmits } from 'vue'
import EventHelpItem from '@/components/event_board/help_panel/EventHelpItem.vue'
import SgiltIcon from '@/components/basics/icons/SgiltIcon.vue'

defineEmits(['close'])
</script>

<style scoped lang="scss">
.event-help-panel {
  padding: 0 $spacing-m;
  background: $color-white;
  position: relative;

  svg {
    height: 1.5rem;
    aspect-ratio: 1;
  }
}

ul {
  list-style: none;
  padding: 0;
}

ul li {
  padding: $spacing-m 0;
}

.divider {
  height: 1px;
  background: $color-divider;
  margin: $spacing-m 0;
}

.warning {
  color: $color-danger;
  font-weight: 600;
}

.close {
  position: absolute;
  top: $spacing-s;
  right: $spacing-s;
  cursor: pointer;
}
</style>
