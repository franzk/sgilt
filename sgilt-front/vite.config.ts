import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'
import { resolve, dirname } from 'path'
import VueI18nPlugin from '@intlify/unplugin-vue-i18n/vite'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
    VueI18nPlugin({
      include: resolve(dirname(fileURLToPath(import.meta.url)), './src/lang/**'),
      fullInstall: false,
      compositionOnly: true,
    }),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
  css: {
    preprocessorOptions: {
      scss: {
        additionalData: `@import "@/assets/stylesheets/_variables.scss"; @import "@/assets/stylesheets/_fonts.scss";`,
      },
    },
  },
  build: {
    rollupOptions: {
      output: {
        manualChunks(id) {
          if (id.includes('node_modules')) {
            const chunks = id.toString().split('node_modules/')[1].split('/')

            // Ex: 'node_modules/vue/dist/vue.runtime.esm.js'
            // => ['vue', 'dist', 'vue.runtime.esm.js']
            return chunks[0] // Regroupe par package : 'vue', 'lodash', etc.
          }
        },
      },
    },
  },
})
