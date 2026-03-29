<template>
  <div class="flow">
    <p class="title">Comment ça marche ?</p>
    <div class="steps">
      <div class="step">
        <span class="dot">1</span>
        <span>Décrivez votre événement en quelques clics</span>
      </div>
      <div class="step">
        <span class="dot">2</span>
        <span>Validez votre demande par email</span>
      </div>
      <div class="step">
        <span class="dot">3</span>
        <span>Le prestataire vous répond</span>
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
