const express = require('express');
const proxy = require('http-proxy-middleware');
const argv = require('minimist')(process.argv.slice(2));
const chalk = require('chalk');
const api_app = express();
console.log(argv);
const onProxyRes = function(proxyRes, req, res){
  proxyRes.headers['Access-Control-Allow-Origin'] = 'http://localhost:8280';
};

const apiUrl = 'https://api.metif.wtf';

api_app.use('/', proxy({target: apiUrl, changeOrigin: true, onProxyRes: onProxyRes}));
api_app.listen(4000);

console.log(`\nRunning ${chalk.blue('api')} proxy for ${chalk.yellow.underline(mode.toUpperCase())} on port 4000`);
