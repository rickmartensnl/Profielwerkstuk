import React from 'react';
import { Link } from "react-router-dom";
import axios from "axios";
import { apiRoute } from "../App";
import MetaTags from 'react-meta-tags';

export class Login extends React.Component {

    constructor(props) {
        super(props);

        this.state = {
            email: '',
            password: '',
            invalidEmail: false,
            isEmptyEmail: false,
            isEmptyPassword: false
        };

        this.handleChange = this.handleChange.bind(this);
        this.handleLogin = this.handleLogin.bind(this);
        this.handleForgot = this.handleForgot.bind(this);
        this.handleRegister = this.handleRegister.bind(this);
    }

    componentDidMount() {
        if (window.matchMedia('(prefers-color-scheme: dark)').matches) {
            document.documentElement.classList.add('dark');
            window.matchMedia("(prefers-color-scheme: dark)").addListener(function () {
                if (window.matchMedia('(prefers-color-scheme: dark)').matches) {
                    document.documentElement.classList.add('dark')
                } else {
                    document.documentElement.classList.remove('dark')
                }
            });
        }

        console.log("Checking if token is set.");
        let token = localStorage.getItem("token");

        if (token == null) {
            return;
        }

        let config = {
            headers: {
                "Authorization": token
            }
        };

        axios.get(`${apiRoute()}/users/@me`, config).then(result => {
            this.props.history.push('/app');
        }).catch(console.warn);
    }

    handleChange(event) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;

        this.setState({
            [name]: value
        });
    }

    handleRegister(event) {
        event.preventDefault();
        this.props.history.push('/register');
    }

    handleLogin(event) {
        event.preventDefault();
        this.setState({
            invalidEmail: false,
            isEmptyEmail: false,
            isEmptyPassword: false
        })

        let emailRegEX = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
        let email = this.state.email;
        let password = this.state.password;

        if (password === '') {
            this.setState({
                isEmptyPassword: password === ''
            });
        }

        if (email === '') {
            this.setState({
                isEmptyEmail: email === '',
            });
            return;
        }

        if (emailRegEX.test(email)) {
            if (this.state.isEmptyPassword) {
                return;
            }

            axios.post(`${apiRoute()}/auth/login`, {email: email, password: password}).then(result => {
                localStorage.setItem("token", result.data.token);
                this.props.history.push('/app');
            }).catch(error => {
                alert('Er is iets fout gegaan.');
                console.error(error);
            });
        } else {
            this.setState({
                invalidEmail: true
            })
        }
    }

    handleForgot() {
        alert('Requesting new password');
    }

    render() {
        return(
            <div className="min-h-screen bg-gray-100 dark:bg-dark-secondary flex flex-col justify-center sm:py-12">
                <MetaTags>
                    <title>Profielwerkstuk — Login</title>
                    <meta name="description" content="Login to the modernized learning experiences." />
                </MetaTags>
                <div className="p-10 xs:p-0 mx-auto md:w-full md:max-w-md">
                    <h1 className="font-bold text-center text-2xl mb-5">Profielwerkstuk</h1>
                    <div className="bg-white dark:bg-dark-primary shadow w-full rounded-lg divide-y divide-gray-200 dark:divide-dark-tertiary">
                        <form onSubmit={this.handleLogin} className="px-5 py-7">
                            <label className={`font-semibold text-sm ${this.state.isEmptyEmail || this.state.invalidEmail ? 'text-red-500' : 'text-gray-600 dark:text-dark-text-primary'} pb-1 block`}>Email<span className="italic">{this.state.isEmptyEmail ? ' — This field is required' : ''}{this.state.invalidEmail ? ' — This is not a valid email' : ''}</span></label>
                            <input name="email" type="text" autoComplete="email" value={this.state.email} onChange={this.handleChange} className={`border dark:border-dark-tertiary rounded-lg px-3 py-2 mt-1 mb-5 text-sm w-full dark:bg-dark-secondary ${this.state.isEmptyEmail || this.state.invalidEmail ? 'border-red-500' : ''}`} />
                            <label className={`font-semibold text-sm ${this.state.isEmptyPassword ? 'text-red-500' : 'text-gray-600 dark:text-dark-text-primary'} pb-1 block`}>Password<span className="italic">{this.state.isEmptyPassword ? ' — This field is required' : ''}</span></label>
                            <input name="password" type="password" autoComplete="current-password" value={this.state.password} onChange={this.handleChange} className={`border dark:border-dark-tertiary rounded-lg px-3 py-2 mt-1 mb-5 text-sm w-full dark:bg-dark-secondary ${this.state.isEmptyPassword ? 'border-red-500' : ''}`}/>
                            <button type="submit" className="transition duration-200 bg-blue-500 hover:bg-blue-600 focus:bg-blue-700 focus:shadow-sm focus:ring-4 focus:ring-blue-500 focus:ring-opacity-50 text-white w-full py-2.5 rounded-lg text-sm shadow-sm hover:shadow-md font-semibold text-center inline-block">
                                <span className="inline-block mr-2">Login</span>
                                <svg xmlns="http://www.w3.org/2000/svg" enableBackground="new 0 0 24 24" viewBox="0 0 24 24" className="w-4 h4 inline-block fill-current">
                                    <rect fill="none" height="24" width="24"/>
                                    <path d="M15,5l-1.41,1.41L18.17,11H2V13h16.17l-4.59,4.59L15,19l7-7L15,5z"/>
                                </svg>
                            </button>
                        </form>
                        <div className="py-5">
                            <div className="flex">
                                <div className="flex-grow text-center sm:text-left whitespace-nowrap">
                                    <div onClick={this.handleForgot} className="transition duration-200 mx-5 px-5 py-4 cursor-pointer font-normal text-sm rounded-lg text-gray-500 dark:text-dark-text-primary hover:bg-gray-100 dark:hover:bg-dark-secondary focus:outline-none focus:bg-gray-200 focus:ring-2 focus:ring-gray-400 focus:ring-opacity-50 ring-inset">
                                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" className="w-4 h-4 inline-block align-text-top fill-current">
                                            <path d="M0 0h24v24H0z" fill="none"/>
                                            <path d="M12 17c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9 2 2 2zm6-9h-1V6c0-2.76-2.24-5-5-5S7 3.24 7 6h1.9c0-1.71 1.39-3.1 3.1-3.1 1.71 0 3.1 1.39 3.1 3.1v2H6c-1.1 0-2 .9-2 2v10c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V10c0-1.1-.9-2-2-2zm0 12H6V10h12v10z"/>
                                        </svg>
                                        <span className="inline-block ml-1">Forgot Password</span>
                                    </div>
                                </div>
                                <div className="flex-grow text-center sm:text-right whitespace-nowrap">
                                    <div onClick={this.handleRegister} className="transition duration-200 mx-5 px-5 py-4 cursor-pointer font-normal text-sm rounded-lg text-gray-500 dark:text-dark-text-primary hover:bg-gray-100 dark:hover:bg-dark-secondary focus:outline-none focus:bg-gray-200 focus:ring-2 focus:ring-gray-400 focus:ring-opacity-50 ring-inset">
                                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" className="w-4 h-4 inline-block align-text-bottom fill-current">
                                            <path d="M0 0h24v24H0z" fill="none"/>
                                            <path d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z"/>
                                        </svg>
                                        <span className="inline-block ml-1">Register</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div className="py-5">
                        <div className="grid grid-cols-2 gap-1">
                            <div className="text-center sm:text-left whitespace-nowrap">
                                <Link to="/" className="transition duration-200 mx-5 px-5 py-4 cursor-pointer font-normal text-sm rounded-lg text-gray-500 dark:text-dark-text-primary hover:bg-gray-200 dark:hover:bg-dark-tertiary focus:outline-none focus:bg-gray-300 focus:ring-2 focus:ring-gray-400 focus:ring-opacity-50 ring-inset">
                                    <svg xmlns="http://www.w3.org/2000/svg" enableBackground="new 0 0 24 24" viewBox="0 0 24 24" className="w-4 h-4 inline-block align-text-top fill-current">
                                        <rect fill="none" height="24" width="24"/><
                                        path d="M9,19l1.41-1.41L5.83,13H22V11H5.83l4.59-4.59L9,5l-7,7L9,19z"/>
                                    </svg>
                                    <span className="inline-block ml-1">Back to Profielwerkstuk</span>
                                </Link>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        );
    }

}
