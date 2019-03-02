# Kids eat free UI

A web app for [KidsEatFree](KidsEatFree.co) written in ClojureScript using [re-frame](https://github.com/Day8/re-frame), & compiled with [shadow-cljs](https://shadow-cljs.github.io/docs/UsersGuide.html).

### Install Javascript dependencies
```bash
yarn
```

### Run dev server
```bash
yarn dev

open http://localhost:8280
```

### Build app
```bash
yarn release
```

### Serve app
Serve `public` dir as an SPA. I recommend [serve](https://www.npmjs.com/package/serve) for serving a dir for an SPA.

```bash
cd public
serve -s -n -o
```
