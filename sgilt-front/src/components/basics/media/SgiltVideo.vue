<template>
  <vue-plyr v-if="youtubeId">
    <div class="plyr__video-embed">
      <iframe
        :src="`https://www.youtube-nocookie.com/embed/${youtubeId}`"
        allowfullscreen
        allow="accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture"
        class="video-iframe"
      >
      </iframe>
    </div>
  </vue-plyr>
  <div v-else class="skeleton video-placeholder"></div>
</template>

<script setup lang="ts">
defineProps<{
  youtubeId: string
}>()
</script>

<style lang="scss" scoped>
.video-placeholder {
  width: 100%;
  height: calc(100vw * 9 / 16);
}
.plyr__video-embed {
  height: 100%;
}

.video-iframe {
  border-radius: $border-radius-s;
  width: 100%;
  height: 100%;
}
</style>
