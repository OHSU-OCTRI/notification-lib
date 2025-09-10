module.exports = {
  root: true,
  parserOptions: {
    parser: '@babel/eslint-parser'
  },
  extends: [
    'eslint:recommended',
    // https://eslint.vuejs.org/rules/#priority-a-essential-error-prevention-for-vue-js-3-x
    // consider switching to `plugin:vue/vue3-strongly-recommended` or `plugin:vue/vue3-recommended` for stricter rules.
    'plugin:vue/vue3-essential'
  ],
  env: {
    browser: true
  },
  globals: {
    $: true,
    bootstrap: true,
    Map: true,
    Set: true,
    Promise: true
  },
  plugins: ['vue'],
  rules: {
    eqeqeq: ['error', 'always'],
    'no-console': ['error', { allow: ['warn', 'error'] }],
    'no-unused-vars': ['error', { vars: 'all', args: 'none' }],
    'no-var': ['error']
  },
  overrides: [
    // node files
    {
      files: ['.eslintrc.js', '.prettierrc.js', 'karma.conf.js', 'webpack.config.js'],
      env: {
        browser: false,
        node: true
      }
    },

    // jasmine tests
    {
      files: ['src/main/resources/frontend/test/**/*.js'],
      env: {
        jasmine: true
      }
    }
  ]
};
