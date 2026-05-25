<template>
  <svg
    class="onboarding-illustration"
    viewBox="0 0 320 210"
    role="img"
    aria-label="Illustration d’un espace prêt à être ouvert"
  >
    <!-- halo -->
    <path
      d="M80 72 C112 25, 213 26, 252 73 C286 114, 253 170, 174 174 C91 178, 42 127, 80 72Z"
      fill="#FFF4CC"
    />

    <!-- confettis -->
    <circle cx="76" cy="74" r="3" fill="#E6B800" />
    <circle cx="250" cy="73" r="3" fill="#E6B800" />
    <circle cx="230" cy="45" r="2.5" fill="#E5DFD8" />
    <rect
      x="68"
      y="132"
      width="8"
      height="3"
      rx="1.5"
      fill="#E6B800"
      transform="rotate(-18 72 133.5)"
    />
    <path
      d="M263 96 C270 94, 273 88, 269 82"
      fill="none"
      stroke="#E6B800"
      stroke-width="2"
      stroke-linecap="round"
    />

    <!-- plante -->
    <g transform="translate(232 106)">
      <path d="M24 52 V18" stroke="#6F8F5F" stroke-width="2" stroke-linecap="round" />
      <path
        d="M24 28 C10 17, 7 8, 19 11 C29 14, 29 25, 24 28Z"
        fill="#C9DDB6"
        stroke="#6F8F5F"
        stroke-width="1.5"
      />
      <path
        d="M25 36 C40 25, 48 22, 45 34 C42 45, 30 43, 25 36Z"
        fill="#C9DDB6"
        stroke="#6F8F5F"
        stroke-width="1.5"
      />
      <path
        d="M24 45 C10 37, 4 37, 7 48 C11 58, 21 52, 24 45Z"
        fill="#C9DDB6"
        stroke="#6F8F5F"
        stroke-width="1.5"
      />
      <rect x="9" y="51" width="31" height="23" rx="4" fill="#F2DEC2" />
      <path d="M11 56 H38" stroke="#D4BE9E" stroke-width="1.5" />
    </g>

    <!-- enveloppe ombre -->
    <ellipse cx="145" cy="158" rx="92" ry="10" fill="#E5DFD8" opacity=".55" />

    <!-- enveloppe corps -->
    <rect
      x="58"
      y="72"
      width="174"
      height="91"
      rx="8"
      fill="#FFFDF8"
      stroke="#D8D0C5"
      stroke-width="2"
    />

    <!-- rabats -->
    <path
      d="M60 76 L145 128 L230 76"
      fill="none"
      stroke="#D8D0C5"
      stroke-width="2"
      stroke-linejoin="round"
    />
    <path
      d="M60 160 L122 113"
      fill="none"
      stroke="#D8D0C5"
      stroke-width="2"
      stroke-linecap="round"
    />
    <path
      d="M230 160 L168 113"
      fill="none"
      stroke="#D8D0C5"
      stroke-width="2"
      stroke-linecap="round"
    />

    <!-- pastille clé -->
    <circle cx="145" cy="121" r="25" fill="#F4BE12" />
    <circle cx="145" cy="121" r="25" fill="none" stroke="#D99F00" stroke-width="2" opacity=".45" />

    <!-- clé -->
    <g transform="translate(132 109) rotate(-35 13 13)">
      <circle cx="9" cy="9" r="6" fill="none" stroke="#FFFDF8" stroke-width="4" />
      <path d="M14 14 L31 31" stroke="#FFFDF8" stroke-width="5" stroke-linecap="round" />
      <path d="M25 26 L20 31" stroke="#FFFDF8" stroke-width="4" stroke-linecap="round" />
      <path d="M30 31 L25 36" stroke="#FFFDF8" stroke-width="4" stroke-linecap="round" />
    </g>
  </svg>
</template>

<script setup lang="ts"></script>

<style scoped lang="scss">
.onboarding-illustration {
  width: min(44vw, 180px);
  height: auto;
  display: block;
}
</style>
