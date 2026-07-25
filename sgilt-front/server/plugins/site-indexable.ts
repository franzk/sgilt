export default defineNitroPlugin((nitroApp) => {
  nitroApp.hooks.hook('site-config:init', (ctx) => {
    ctx.siteConfig.push({
      _context: 'env:ENV',
      indexable: process.env.ENV === 'production',
    })
  })
})