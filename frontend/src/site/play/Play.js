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
            loggedIn: true,
            paragraph: {},
            question: {},
            myAnswer: ""
        };

        this.authMiddleware = new AuthMiddleware();
        this.handleChange = this.handleChange.bind(this);
    }

    componentDidMount() {
        this.authMiddleware.isValid().then(res => {
            if (!res) {
                this.setState({
                    loggedIn: false
                });
                this.props.history.push('/login');
            }
        }).catch(err => {
            this.setState({
                loggedIn: false
            });
            this.props.history.push('/login');
        });


        //TODO: Request existing question if one otherwise ask for new question.
        axios.get(apiRoute() + `/chapters/${this.props.match.params.chapterUuid}/paragraphs/${this.props.match.params.paragraphUuid}`).then(res => {
            this.setState({
                paragraph: res.data
            });
        });

        this.authMiddleware.newOrLastQuestion().then(questionPlay => {
            questionPlay = questionPlay[0]

            for (const variable in questionPlay.variableValues) {
                const varData = questionPlay.variableValues[variable]
                questionPlay.question.information = questionPlay.question.information.replaceAll(`%%${variable}%%`, varData.theValue);
                questionPlay.question.question = questionPlay.question.question.replaceAll(`%%${variable}%%`, varData.theValue);
            }

            this.setState({
                question: questionPlay
            });
        })


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
                }
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
        let data = {
            authMiddleware: this.authMiddleware,
            dyslexia: this.state.dyslexia,
            loggedIn: this.state.loggedIn,
        }

        let question = this.state.question;

        if (question.question === undefined) {
            return(
                <div>
                    <MetaTags>
                        <title>Profielwerkstuk — Leren</title>
                        <meta name="description" content="Select a subject to start learning from." />
                    </MetaTags>
                    <Header data={data} />
                    <div className="container mx-auto">
                        <h1 className={`text-center text-3xl font-bold dark:text-dark-text-primary ${this.state.dyslexia ? 'dyslexia-font' : ''}`}>
                            Aan het laden...
                        </h1>
                        <h2 className={`mt-2 text-1xl text-gray-700 dark:text-dark-text-secondary ${this.state.dyslexia ? 'dyslexia-font' : ''}`}>
                            Je vraag wordt geladen...
                        </h2>
                        <h2 className={`mt-2 text-1xl text-gray-700 dark:text-dark-text-secondary ${this.state.dyslexia ? 'dyslexia-font' : ''}`}>
                            We zijn er bijna....
                        </h2>
                        <label className={`mt-5 font-semibold text-sm text-gray-600 dark:text-dark-text-primary pb-1 block ${this.state.dyslexia ? 'dyslexia-font' : ''}`}>Jouw antwoord: </label>
                        <input name="myAnswer" type="text" autoComplete="off" value={this.state.myAnswer} onChange={this.handleChange} className={`border dark:border-dark-tertiary rounded-lg px-3 py-2 mt-1 mb-5 text-sm w-full dark:bg-dark-secondary ${this.state.dyslexia ? 'dyslexia-font' : ''}`} />
                    </div>
                </div>
            );
        }

        return(
            <div>
                <MetaTags>
                    <title>Profielwerkstuk — Leren</title>
                    <meta name="description" content="Select a subject to start learning from." />
                </MetaTags>
                <Header data={data} />
                <div className="container mx-auto">
                    <h1 className={`text-center text-3xl font-bold dark:text-dark-text-primary ${this.state.dyslexia ? 'dyslexia-font' : ''}`}>
                        Titel van hoofdstuk etc.
                    </h1>
                    <h2 className={`mt-2 text-1xl text-gray-700 dark:text-dark-text-secondary ${this.state.dyslexia ? 'dyslexia-font' : ''}`}>
                        {
                            question.question.information
                        }
                    </h2>
                    <h2 className={`mt-2 text-1xl text-gray-700 dark:text-dark-text-secondary ${this.state.dyslexia ? 'dyslexia-font' : ''}`}>
                        {
                            question.question.question
                        }
                    </h2>
                    <label className={`mt-5 font-semibold text-sm text-gray-600 dark:text-dark-text-primary pb-1 block ${this.state.dyslexia ? 'dyslexia-font' : ''}`}>Jouw antwoord: </label>
                    <input name="myAnswer" type="text" autoComplete="off" value={this.state.myAnswer} onChange={this.handleChange} className={`border dark:border-dark-tertiary rounded-lg px-3 py-2 mt-1 mb-5 text-sm w-full dark:bg-dark-secondary ${this.state.dyslexia ? 'dyslexia-font' : ''}`} />
                </div>
            </div>
        );
    }

}
