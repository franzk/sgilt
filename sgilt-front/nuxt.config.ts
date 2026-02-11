// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  compatibilityDate: '2025-07-15',

  devtools: { enabled: true },

  modules: [
    '@nuxtjs/i18n',
    '@nuxtjs/seo',
    '@nuxtjs/robots',
    '@pinia/nuxt'
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
        'X-Robots-Tag': 'noindex, nofollow'
      }
    },

    // Recherche = utilitaire, pas indexée
    '/recherche': {
      headers: {
        'X-Robots-Tag': 'noindex, nofollow'
      }
    }

    // /            -> SSR + SEO (default)
    // /m/**        -> SSR + SEO (default)
    // /:slug       -> SSR + SEO (default)
  },


  // Source de vérité SEO (utilisée notamment pour les canonicals)
  site: {
    url: 'https://sgilt.alsace',
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
        allow: '/',
        disallow: [
          '/app/',
          '/recherche'
        ]
      }
    ]
  },

  /**
   * ======================
   * i18n 
   * ======================
   */
  i18n: {
    defaultLocale: 'fr',
    locales: [
      { code: 'fr', language: 'fr-FR', name: 'Français' }
    ],
    strategy: 'no_prefix',
    detectBrowserLanguage: {
      useCookie: true,
      cookieKey: 'sgilt_lang',
      redirectOn: 'root'
    }
  }
})
