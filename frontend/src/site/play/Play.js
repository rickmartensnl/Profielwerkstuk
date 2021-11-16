import React from "react";
import MetaTags from "react-meta-tags";
import { AuthMiddleware } from "../../middlewares/AuthMiddleware";
import { apiRoute } from "../App";
import axios from "axios";
import { Header } from "../shared/Header";

export class Play extends React.Component {

    constructor(props) {
        super(props);

        this.state = {
            dyslexia: false,
            myAnswer: "poep"
        };

        this.authMiddleware = new AuthMiddleware();
        this.handleChange = this.handleChange.bind(this);
    }

    componentDidMount() {
        this.authMiddleware.isValid().then(res => {
            if (!res) {
                this.props.history.push('/login');
            }
        }).catch(err => {
            this.props.history.push('/login');
        });


        //TODO: Request existing question if one otherwise ask for new question.

        this.authMiddleware.getUser().then(user => {
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
            } else if ((user.flags & 0x4) === 0x4) {
                document.documentElement.classList.remove('dark');
            } else if (window.matchMedia('(prefers-color-scheme: dark)').matches) {
                document.documentElement.classList.add('dark');
                window.matchMedia("(prefers-color-scheme: dark)").addListener(function () {
                    if (window.matchMedia('(prefers-color-scheme: dark)').matches) {
                        document.documentElement.classList.add('dark')
                    } else {
                        document.documentElement.classList.remove('dark')
                    }
                });
            }
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

    render() {
        return(
            <div>
                <MetaTags>
                    <title>Profielwerkstuk — Leren</title>
                    <meta name="description" content="Select a subject to start learning from." />
                </MetaTags>
                <Header />
                <div className="container mx-auto">
                    <h1 className={`text-center text-3xl font-bold dark:text-dark-text-primary ${this.state.dyslexia ? 'dyslexia-font' : ''}`}>
                        Titel van hoofdstuk etc.
                    </h1>
                    <h2 className={`text-1xl text-gray-700 dark:text-dark-text-secondary ${this.state.dyslexia ? 'dyslexia-font' : ''}`}>
                        Informatie
                    </h2>
                    <h2 className={`text-1xl text-gray-700 dark:text-dark-text-secondary ${this.state.dyslexia ? 'dyslexia-font' : ''}`}>
                        Vraag
                    </h2>
                    <label className={`font-semibold text-sm text-gray-600 dark:text-dark-text-primary pb-1 block ${this.state.dyslexia ? 'dyslexia-font' : ''}`}>Jouw antwoord: </label>
                    <input name="myAnswer" type="text" autoComplete="off" value={this.state.myAnswer} onChange={this.handleChange} className={`border dark:border-dark-tertiary rounded-lg px-3 py-2 mt-1 mb-5 text-sm w-full dark:bg-dark-secondary ${this.state.dyslexia ? 'dyslexia-font' : ''}`} />
                </div>
            </div>
        );
    }

}
