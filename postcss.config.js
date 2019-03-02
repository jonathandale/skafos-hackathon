const tailwindcss = require('tailwindcss');
module.exports = {
    plugins: [
        tailwindcss('./src/styles/tailwind.js'),
        require('cssnano')
    ],
};
