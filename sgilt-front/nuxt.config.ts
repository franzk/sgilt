// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  compatibilityDate: '2025-07-15',

  devtools: { enabled: false },

  modules: [
    '@nuxtjs/i18n',
    '@nuxtjs/seo',
    '@nuxtjs/robots',
    '@pinia/nuxt',
    'reka-ui/nuxt',
    '@nuxtjs/google-fonts',
  ],

  /**
   * ======================
   * Rendering / Routing
   * ======================
   */
  routeRules: {
    // App = SPA, pas de SEO
    '/app/**': {
      ssr: false,
      headers: {
        'X-Robots-Tag': 'noindex, nofollow',
      },
    },

    // Recherche = utilitaire, pas indexée
    '/recherche': {
      headers: {
        'X-Robots-Tag': 'noindex, nofollow',
      },
    },

    // /            -> SSR + SEO (default)
    // /m/**        -> SSR + SEO (default)
    // /:slug       -> SSR + SEO (default)
  },

  // Source de vérité SEO (utilisée notamment pour les canonicals)
  site: {
    // url: 'https://sgilt.alsace',
    trailingSlash: false, // => canonicals en /path (sans / final)
  },

  /**
   * ======================
   * Robots.txt
   * ======================
   */
  robots: {
    groups: [
      {
        userAgent: '*',
        disallow: '/',
        // disallow: ['/app/', '/recherche'],
      },
    ],
  },

  /**
   * ======================
   * i18n
   * ======================
   */
  i18n: {
    defaultLocale: 'fr',
    strategy: 'no_prefix',

    // lazy: true,
    // langDir: 'lang/',

    locales: [{ code: 'fr', language: 'fr-FR', name: 'Français', file: 'fr.json' }],

    detectBrowserLanguage: {
      useCookie: true,
      cookieKey: 'sgilt_lang',
      redirectOn: 'root',
    },
  },

  css: ['@/assets/styles/main.scss'],

  vite: {
    css: {
      preprocessorOptions: {
        scss: {
          additionalData:
            '@use "@/assets/styles/colors" as *;\n' +
            '@use "@/assets/styles/spacing" as *;\n' +
            '@use "@/assets/styles/typography" as *;\n',
        },
      },
    },
  },
})
