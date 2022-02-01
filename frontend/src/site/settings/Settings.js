import React, { Fragment } from "react";
import MetaTags from "react-meta-tags";
import { AuthMiddleware } from "../../middlewares/AuthMiddleware";
import { apiRoute } from "../App";
import axios from "axios";
import { Header } from "../shared/Header";
import { Switch } from "@headlessui/react";

export class Settings extends React.Component {

    constructor(props) {
        super(props);

        this.state = {
            dyslexia: false,
            loggedIn: true,

            darkMode: false,
        };

        this.authMiddleware = new AuthMiddleware();
        this.setDarkMode = this.setDarkMode.bind(this);
        this.setDyslexiaMode = this.setDyslexiaMode.bind(this);
        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    async componentDidMount() {
        let valid = await this.authMiddleware.isValid();

        if (!valid) {
            this.setState(prevState => ({
                loggedIn: false
            }));
            this.props.history.push('/login');
        }

        let user = await this.authMiddleware.getUser();

        if ((user.flags & 0x1) === 0x1) {
            this.setState(prevState => ({
                dyslexia: true
            }));
        } else {
            this.setState(prevState => ({
                dyslexia: false
            }));
        }

        //Dark Mode
        if ((user.flags & 0x2) === 0x2) {
            document.documentElement.classList.add('dark');

            this.setState(prevState => ({
                darkMode: true
            }));
        } else if ((user.flags & 0x4) === 0x4) {
            document.documentElement.classList.remove('dark');

            this.setState(prevState => ({
                darkMode: false
            }));
        } else {
            window.matchMedia("(prefers-color-scheme: dark)").addListener(function () {
                if (window.matchMedia('(prefers-color-scheme: dark)').matches) {
                    document.documentElement.classList.add('dark')
                } else {
                    document.documentElement.classList.remove('dark')
                }
            });

            if (window.matchMedia('(prefers-color-scheme: dark)').matches) {
                document.documentElement.classList.add('dark');

                this.setState(prevState => ({
                    darkMode: true
                }));
            }
        }
    }

    async handleSubmit(event) {
        event.preventDefault();

        console.log("dyslexia", this.state.dyslexia)
        console.log("darkMode", this.state.darkMode)

        axios.patch(`${apiRoute()}/users/@me`, {dyslexiaMode: this.state.dyslexia, darkMode: this.state.darkMode}, {
            headers: {
                "Authorization": this.authMiddleware.token
            }
        }).then(result => {
            let user = result.data;

            if ((user.flags & 0x1) === 0x1) {
                this.setState(prevState => ({
                    dyslexia: true
                }));
            } else {
                this.setState(prevState => ({
                    dyslexia: false
                }));
            }

            //Dark Mode
            if ((user.flags & 0x2) === 0x2) {
                document.documentElement.classList.add('dark');

                this.setState(prevState => ({
                    darkMode: true
                }));
            } else if ((user.flags & 0x4) === 0x4) {
                document.documentElement.classList.remove('dark');

                this.setState(prevState => ({
                    darkMode: false
                }));
            } else {
                window.matchMedia("(prefers-color-scheme: dark)").addListener(function () {
                    if (window.matchMedia('(prefers-color-scheme: dark)').matches) {
                        document.documentElement.classList.add('dark')
                    } else {
                        document.documentElement.classList.remove('dark')
                    }
                });

                if (window.matchMedia('(prefers-color-scheme: dark)').matches) {
                    document.documentElement.classList.add('dark');

                    this.setState(prevState => ({
                        darkMode: true
                    }));
                }
            }
        }).catch(error => {
            alert('Er is iets fout gegaan.');
            console.error(error);
        });
    }

    handleChange(event) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;

        this.setState({
            [name]: value
        });
    }

    setDarkMode(enabled) {
        this.setState({
            darkMode: enabled
        })

        if (enabled) {
            document.documentElement.classList.add('dark');
        } else {
            document.documentElement.classList.remove('dark');
        }
    }

    setDyslexiaMode(enabled) {
        this.setState({
            dyslexia: enabled
        })
    }

    render() {
        let data = {
            authMiddleware: this.authMiddleware,
            dyslexia: this.state.dyslexia,
            loggedIn: this.state.loggedIn,
        }

        return(
            <div>
                <MetaTags>
                    <title>Modern Math — Instellingen</title>
                    <meta name="description" content="Select a subject to start learning from." />
                </MetaTags>
                <Header data={data} />
                <div className="container mx-auto px-5">
                    <h1 className={`text-center text-3xl font-bold dark:text-dark-text-primary ${this.state.dyslexia ? 'dyslexia-font' : ''}`}>
                        Instellingen
                    </h1>

                    <form onSubmit={this.handleSubmit} className={`${this.state.dyslexia ? 'dyslexia-font' : ''}`}>
                        <Switch.Group as="div" className="flex items-center space-x-4 mt-2">
                            <Switch.Label>Donkere modus</Switch.Label>
                            <Switch as="button" checked={this.state.darkMode} onChange={this.setDarkMode} className={`${this.state.darkMode ? 'bg-green-400 dark:bg-green-600' : 'bg-gray-200 dark:bg-dark-primary'} mr-5 relative inline-flex flex-shrink-0 h-6 w-12 translation-colors duration-200 ease-in-out border-2 border-transparent rounded-full cursor-pointer focus:outline-none focus:shadow-outline`}>
                                <span className={`${this.state.darkMode ? 'translate-x-6' : 'translate-x-0'} inline-block w-5 h-5 translation duration-200 ease-in-out transform bg-white dark:bg-dark-secondary rounded-full`} />
                            </Switch>
                        </Switch.Group>
                        <Switch.Group as="div" className="flex items-center space-x-4 mt-2">
                            <Switch.Label>Dyslexie modus</Switch.Label>
                            <Switch as="button" checked={this.state.dyslexia} onChange={this.setDyslexiaMode} className={`${this.state.dyslexia ? 'bg-green-400 dark:bg-green-600' : 'bg-gray-200 dark:bg-dark-primary'} mr-5 relative inline-flex flex-shrink-0 h-6 w-12 translation-colors duration-200 ease-in-out border-2 border-transparent rounded-full cursor-pointer focus:outline-none focus:shadow-outline`}>
                                <span className={`${this.state.dyslexia ? 'translate-x-6' : 'translate-x-0'} inline-block w-5 h-5 translation duration-200 ease-in-out transform bg-white dark:bg-dark-secondary rounded-full`} />
                            </Switch>
                        </Switch.Group>
                        <button type="submit" className="whitespace-nowrap mt-10 px-4 py-2 border border-transparent rounded-md shadow-sm text-base font-medium text-white bg-blue-500 hover:bg-blue-600">Opslaan</button>
                    </form>
                </div>
            </div>
        );
    }

}
