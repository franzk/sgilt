<template>
  <div class="flow">
    <p class="title">{{ $t('tunnel.comment-ca-marche.title') }}</p>
    <div class="steps">
      <div class="step">
        <span class="dot">1</span>
        <span>{{ $t('tunnel.comment-ca-marche.step-1') }}</span>
      </div>
      <div class="step">
        <span class="dot">2</span>
        <span>{{ $t('tunnel.comment-ca-marche.step-2') }}</span>
      </div>
      <div class="step">
        <span class="dot">3</span>
        <span>{{ $t('tunnel.comment-ca-marche.step-3') }}</span>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.flow {
  padding: $spacing-m;
  background: $surface-soft;
  border: 1px solid $divider-color;
  border-radius: $radius-md;

  .title {
    font-size: 0.7rem;
    font-weight: 600;
    letter-spacing: 0.08em;
    text-transform: uppercase;
    color: $text-secondary;
    margin: 0 0 $spacing-m;
  }

  .steps {
    display: flex;
    flex-direction: column;
    gap: $spacing-s;
  }

  .step {
    display: flex;
    align-items: center;
    gap: $spacing-s;
    font-size: 0.9rem;
    color: $text-secondary;
  }

  .dot {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 1.4rem;
    height: 1.4rem;
    border-radius: 50%;
    background: rgba($brand-accent, 0.15);
    color: $brand-accent;
    font-size: 0.7rem;
    font-weight: 700;
    flex-shrink: 0;
  }
}
</style>
