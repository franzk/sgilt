<template>
  <MobileScreenTitle icon="Help" title="Aide" />

  <div class="event-help-panel">
    <h3>🎯 {{ $t('event.help.steps.title') }}</h3>
    <ul>
      <li>
        <EventHelpItem
          icon="Mail"
          :title="$t('event.help.steps.step-1.title')"
          :content="$t('event.help.steps.step-1.content')"
        />
      </li>
      <li>
        <EventHelpItem
          icon="Eye"
          :title="$t('event.help.steps.step-2.title')"
          :content="$t('event.help.steps.step-2.content')"
        />
      </li>
      <li>
        <EventHelpItem
          icon="Check"
          :title="$t('event.help.steps.step-3.title')"
          :content="$t('event.help.steps.step-3.content')"
        />
      </li>
      <li>
        <EventHelpItem
          icon="Calendar"
          :title="$t('event.help.steps.step-4.title')"
          :content="$t('event.help.steps.step-4.content')"
        />
      </li>
    </ul>
    <p class="warning">
      {{ $t('event.help.steps.warning') }}
    </p>

    <div class="divider" />

    <h3>{{ $t('event.help.contact.title') }}</h3>
    <p>
      <EventHelpItem icon="Mail" title="Support client :" content="support@sgilt.com" oneline />
    </p>
    <p>
      <EventHelpItem icon="Phone" title="Assistance téléphonique" content="+33 1 23 45 67 89" />
    </p>

    <div class="divider" />

    <h3>{{ $t('event.help.faq.title') }}</h3>
    <p>
      <EventHelpItem
        icon="CreditCard"
        :title="$t('event.help.faq.pay.title')"
        :content="$t('event.help.faq.pay.content')"
      />
    </p>
    <p>
      <EventHelpItem
        icon="Cancel"
        :title="$t('event.help.faq.cancel.title')"
        :content="$t('event.help.faq.cancel.content')"
      />
    </p>
  </div>
</template>

<script setup lang="ts">
import EventHelpItem from '@/components/event_board/help_panel/EventHelpItem.vue'
import MobileScreenTitle from '@/components/event_board/MobileScreenTitle.vue'
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
</style>
