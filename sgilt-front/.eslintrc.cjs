module.exports = {
  root: true,
  extends: [
    'nuxt',
    'plugin:prettier/recommended'
  ],
  plugins: ['prettier'],
  rules: {
    'prettier/prettier': 'warn'
  }
}
