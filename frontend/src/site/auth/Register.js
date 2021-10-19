import React from 'react';
import { Link } from "react-router-dom";
import axios from "axios";
import { apiRoute } from "../App";
import MetaTags from "react-meta-tags";

export class Register extends React.Component {

    constructor(props) {
        super(props);

        this.state = {
            email: '',
            username: '',
            password: '',
            passwordConfirm: '',
            terms: false,
            invalidEmail: false,
            isEmptyEmail: false,
            isEmptyUsername: false,
            isEmptyPassword: false,
            isEmptyPasswordConfirm: false,
            isNotAcceptedTerms: false
        };

        this.handleChange = this.handleChange.bind(this);
        this.handleRegister = this.handleRegister.bind(this);
    }

    componentDidMount() {
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
        this.setState({
            invalidEmail: false,
            isEmptyEmail: false,
            isEmptyUsername: false,
            isEmptyPassword: false,
            isEmptyPasswordConfirm: false,
            isNotAcceptedTerms: false
        })

        let emailRegEX = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
        let email = this.state.email;
        let username = this.state.username;
        let password = this.state.password;
        let passwordConfirm = this.state.passwordConfirm;
        let terms = this.state.terms;

        if (username === '') {
            this.setState({
                isEmptyUsername: username === ''
            });
        }

        if (password === '') {
            this.setState({
                isEmptyPassword: password === ''
            });
        }

        if (passwordConfirm === '') {
            this.setState({
                isEmptyPasswordConfirm: passwordConfirm === ''
            });
        }

        this.setState({
            isNotAcceptedTerms: terms
        })

        if (email === '') {
            this.setState({
                isEmptyEmail: email === '',
            });
            return;
        }

        if (emailRegEX.test(email)) {
            if (this.state.isEmptyUsername || this.state.isEmptyPassword || this.state.isEmptyPasswordConfirm || this.isNotAcceptedTerms) {
                return;
            }

            axios.post(`${apiRoute()}/auth/register`, {email: email, username: username, password: password, terms: terms}).then(result => {
                alert('Je gegevens zijn correct. Ik ga je token opslaan.');
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
            <div className="min-h-screen bg-gray-100 flex flex-col justify-center sm:py-12">
                <MetaTags>
                    <title>Profielwerkstuk — Register</title>
                    <meta name="description" content="Create your account for a modernized learning experience." />
                </MetaTags>
                <div className="p-10 xs:p-0 mx-auto md:w-full md:max-w-md">
                    <h1 className="font-bold text-center text-2xl mb-5">Profielwerkstuk</h1>
                    <div className="bg-white shadow w-full rounded-lg divide-y divide-gray-200">
                        <form onSubmit={this.handleRegister} className="px-5 py-7">
                            <label className={`font-semibold text-sm ${this.state.isEmptyEmail || this.state.invalidEmail ? 'text-red-500' : 'text-gray-600'} pb-1 block`}>Email<span className="italic">{this.state.isEmptyEmail ? ' — This field is required' : ''}{this.state.invalidEmail ? ' — This is not a valid email' : ''}</span></label>
                            <input name="email" type="text" autoComplete="email" value={this.state.email} onChange={this.handleChange} className={`border rounded-lg px-3 py-2 mt-1 mb-5 text-sm w-full ${this.state.isEmptyEmail || this.state.invalidEmail ? 'border-red-500' : ''}`} />
                            <label className={`font-semibold text-sm ${this.state.isEmptyUsername ? 'text-red-500' : 'text-gray-600'} pb-1 block`}>Username<span className="italic">{this.state.isEmptyUsername ? ' — This field is required' : ''}</span></label>
                            <input name="username" type="text" autoComplete="username" value={this.state.username} onChange={this.handleChange} className={`border rounded-lg px-3 py-2 mt-1 mb-5 text-sm w-full ${this.state.isEmptyUsername ? 'border-red-500' : ''}`} />
                            <label className={`font-semibold text-sm ${this.state.isEmptyPassword ? 'text-red-500' : 'text-gray-600'} pb-1 block`}>Password<span className="italic">{this.state.isEmptyPassword ? ' — This field is required' : ''}</span></label>
                            <input name="password" type="password" autoComplete="new-password" value={this.state.password} onChange={this.handleChange} className={`border rounded-lg px-3 py-2 mt-1 mb-5 text-sm w-full ${this.state.isEmptyPassword ? 'border-red-500' : ''}`}/>
                            <label className={`font-semibold text-sm text-gray-600 pb-1 block`}>Verify Password</label>
                            <input name="verifyPassword" type="password" autoComplete="new-password" className={`border rounded-lg px-3 py-2 mt-1 mb-5 text-sm w-full`} />
                            <label className="inline-flex cursor-pointer mt-1 mb-5">
                                <input id="customCheckLogin" type="checkbox" className="form-checkbox border rounded-lg px-3 py-2 mt-1 text-sm" />
                                <span className="ml-2 font-semibold text-sm text-gray-600 block">
                                    I have read and agree with our&nbsp;
                                    <Link to="/" className="text-blue-600 hover:underline">
                                        Terms of Service
                                    </Link>
                                    &nbsp;and&nbsp;
                                    <Link to="/" className="text-blue-600 hover:underline">
                                        Privacy Policy
                                    </Link>
                                    .
                                </span>
                            </label>
                            <button type="submit" className="transition duration-200 bg-blue-500 hover:bg-blue-600 focus:bg-blue-700 focus:shadow-sm focus:ring-4 focus:ring-blue-500 focus:ring-opacity-50 text-white w-full py-2.5 rounded-lg text-sm shadow-sm hover:shadow-md font-semibold text-center inline-block">
                                <span className="inline-block mr-2">Register</span>
                                <svg xmlns="http://www.w3.org/2000/svg" enableBackground="new 0 0 24 24" viewBox="0 0 24 24" className="w-4 h4 inline-block fill-current">
                                    <rect fill="none" height="24" width="24"/>
                                    <path d="M15,5l-1.41,1.41L18.17,11H2V13h16.17l-4.59,4.59L15,19l7-7L15,5z"/>
                                </svg>
                            </button>
                        </form>
                        <div className="py-5">
                            <div className="grid grid-cols-2 gap-1">
                                <div className="text-center sm:text-left  whitespace-nowrap">
                                    <Link to="/login" className="transition duration-200 mx-5 px-5 py-4 cursor-pointer font-normal text-sm rounded-lg text-gray-500 hover:bg-gray-100 focus:outline-none focus:bg-gray-200 focus:ring-2 focus:ring-gray-400 focus:ring-opacity-50 ring-inset">
                                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" className="w-4 h-4 inline-block align-text-bottom fill-current">
                                            <g>
                                                <rect fill="none" height="24" width="24"/>
                                            </g>
                                            <g>
                                                <path d="M11,7L9.6,8.4l2.6,2.6H2v2h10.2l-2.6,2.6L11,17l5-5L11,7z M20,19h-8v2h8c1.1,0,2-0.9,2-2V5c0-1.1-0.9-2-2-2h-8v2h8V19z"/>
                                            </g>
                                        </svg>
                                        <span className="inline-block ml-1">Already an account?</span>
                                    </Link>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div className="py-5">
                        <div className="grid grid-cols-2 gap-1">
                            <div className="text-center sm:text-left whitespace-nowrap">
                                <Link to="/" className="transition duration-200 mx-5 px-5 py-4 cursor-pointer font-normal text-sm rounded-lg text-gray-500 hover:bg-gray-200 focus:outline-none focus:bg-gray-300 focus:ring-2 focus:ring-gray-400 focus:ring-opacity-50 ring-inset">
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
