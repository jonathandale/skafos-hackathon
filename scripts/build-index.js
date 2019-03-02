const pug = require('pug');
const argv = require('minimist')(process.argv.slice(2));
const fs = require('fs');

let isProd = argv["prod"];

html = pug.renderFile('./scripts/index.pug', {isProd: isProd, pretty: true});

fs.writeFile("./public/index.html", html, function(err) {
  if(err) return console.log(err);
});
