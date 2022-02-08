/*
 * Copyright (c) 2022 Rick Martens - All rights not expressly granted herein are reserved
 *
 * This material is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International.
 */

module.exports = {
    content: ['./src/**/*.{js,jsx,ts,tsx}', './public/index.html'],
    darkMode: 'class', // or 'media' or 'class'
    theme: {
        extend: {
            colors: {
                'dark-primary': '#171717',
                'dark-primary-2': '#1C1C1C',
                'dark-secondary': '#242424',
                'dark-tertiary': '#333333',
                'dark-text-primary': '#F4F4F4',
                'dark-text-secondary': '#B7B7B7',
            },
        },
    },
    plugins: [
        require('@tailwindcss/forms'),
    ],
}
