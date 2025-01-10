<template>
  <vue-plyr v-if="youtubeId" class="video-player">
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

<style lang="scss">
.video-player {
  width: 100%;
}

.plyr__video-embed {
  // width: 60vw;
  aspect-ratio: 1.78;
}

.video-iframe {
  height: 100%;
  width: 100%;
}
</style>
