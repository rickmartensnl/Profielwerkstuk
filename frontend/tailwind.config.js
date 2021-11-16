module.exports = {
    purge: ['./src/**/*.{js,jsx,ts,tsx}', './public/index.html'],
    darkMode: 'class', // or 'media' or 'class'
    theme: {
        extend: {
            colors: {
                'dark-primary': '#171717',
                'dark-primary-2': '#171717CD',
                'dark-secondary': '#242424',
                'dark-tertiary': '#333333',
                'dark-text-primary': '#F4F4F4',
                'dark-text-secondary': '#B7B7B7',
            },
        },
    },
    variants: {
        extend: {
            placeholderColor: ['dark'],
            backgroundColor: ['dark']
        }
    },
    plugins: [
        require('@tailwindcss/forms'),
    ],
}
