import React, { Fragment } from "react";
import MetaTags from "react-meta-tags";
import { AuthMiddleware } from "../../middlewares/AuthMiddleware";
import { apiRoute } from "../App";
import axios from "axios";
import { Header } from "../shared/Header";
import { Dialog, Transition } from '@headlessui/react'

export class Play extends React.Component {

    constructor(props) {
        super(props);

        this.state = {
            dyslexia: false,
            loggedIn: true,
            sessionId: "",
            paragraph: {},
            question: {},
            myAnswer: "",
            checkingAnswer: false,
            isEmptyAnswer: false,
            correctPercentage: null,
        };

        this.authMiddleware = new AuthMiddleware();
        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.nextQuestion = this.nextQuestion.bind(this);
        this.showSolution = this.showSolution.bind(this);
    }

    async componentDidMount() {
        let valid = await this.authMiddleware.isValid();

        if (!valid) {
            this.setState(prevState => ({
                loggedIn: false
            }));
            this.props.history.push('/login');
        }

        let paragraphRequest = await axios.get(apiRoute() + `/chapters/${this.props.match.params.chapterUuid}/paragraphs/${this.props.match.params.paragraphUuid}`);
        this.setState({
            paragraph: paragraphRequest.data
        });

        let questionPlayRequest = await this.authMiddleware.newOrLastQuestion();
        if (Array.isArray(questionPlayRequest)) {
            questionPlayRequest = questionPlayRequest[0]
        }
        let session = questionPlayRequest;

        for (const variable in questionPlayRequest.variableValues) {
            const varData = questionPlayRequest.variableValues[variable]
            questionPlayRequest.question.information = questionPlayRequest.question.information.replaceAll(`%%${variable}%%`, varData.theValue);
            questionPlayRequest.question.question = questionPlayRequest.question.question.replaceAll(`%%${variable}%%`, varData.theValue);
        }

        this.setState({
            sessionId: session.uuid,
            question: questionPlayRequest
        });

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
    }

    nextQuestion() {
        location.reload();
    }

    showSolution() {
        console.log("show solution")
    }

    async handleSubmit(event) {
        event.preventDefault();
        this.setState({
            isEmptyAnswer: false
        })

        let data = {
            answer: this.state.myAnswer,
            sessionId: this.state.sessionId
        }

        if (data.answer === "") {
            this.setState({
                isEmptyAnswer: true,
            })
            return;
        }

        this.setState({
            checkingAnswer: true,
        })

        let result = await axios.post(apiRoute() + '/users/@me/sessions', data, {
            headers: {
                "Authorization": localStorage.getItem("token")
            }
        });

        this.setState({
            correctPercentage: result.data.percentage,
        })
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

        let correctness = 'FOUT';
        if (this.state.correctPercentage != null) {
            if (this.state.correctPercentage === "100.0") {
                correctness = "GOED"
            } else if (this.state.correctPercentage === "80.0") {
                correctness = "BIJNA GOED"
            }
        }

        return(
            <div>
                <MetaTags>
                    <title>Profielwerkstuk — Leren</title>
                    <meta name="description" content="Select a subject to start learning from." />
                </MetaTags>
                <Header data={data} />
                <div className="container mx-auto">
                    <Transition appear show={this.state.checkingAnswer} as={Fragment}>
                        <Dialog as="div" className="fixed inset-0 z-10 overflow-y-auto" onClose={this.nextQuestion}>
                            <div className="min-h-screen px-4 text-center">
                                <Transition.Child as={Fragment} enter="ease-out duration-300" enterFrom="opacity-0" enterTo="opacity-100" leave="ease-in duration-200" leaveFrom="opacity-100" leaveTo="opacity-0">
                                    <Dialog.Overlay className="fixed inset-0" />
                                </Transition.Child>

                                <span className="inline-block h-screen align-middle" aria-hidden="true">&#8203;</span>
                                <Transition.Child as={Fragment} enter="ease-out duration-300" enterFrom="opacity-0 scale-95" enterTo="opacity-100 scale-100" leave="ease-in duration-200" leaveFrom="opacity-100 scale-100" leaveTo="opacity-0 scale-95">
                                    <div className="inline-block w-full max-w-md p-6 my-8 overflow-hidden text-left align-middle transition-all transform bg-white dark:bg-dark-primary shadow-xl rounded-2xl">
                                        <Dialog.Title as="h3" className="text-lg font-medium leading-6">
                                            { this.state.correctPercentage == null ? 'We zijn je antwoord aan het nakijken...' : 'Jouw antwoord is:' }
                                        </Dialog.Title>
                                        <div className="mt-2">
                                            <p className="text-sm text-gray-500 font-bold">
                                                { this.state.correctPercentage != null ? correctness : '...' }
                                            </p>
                                        </div>

                                        <div className="mt-4">
                                            {
                                                this.state.correctPercentage == null ? '' :
                                                <>
                                                    <button type="button" className="inline-flex justify-center px-4 py-2 text-sm font-medium text-blue-900 bg-blue-100 border border-transparent rounded-md hover:bg-blue-200 focus:outline-none focus-visible:ring-2 focus-visible:ring-offset-2 focus-visible:ring-blue-500" onClick={this.showSolution}>
                                                        Toon berekening
                                                    </button>
                                                    <button type="button" className="float-right inline-flex justify-center px-4 py-2 text-sm font-medium text-blue-900 bg-blue-100 border border-transparent rounded-md hover:bg-blue-200 focus:outline-none focus-visible:ring-2 focus-visible:ring-offset-2 focus-visible:ring-blue-500" onClick={this.nextQuestion}>
                                                        Volgende vraag
                                                    </button>
                                                </>
                                            }
                                        </div>
                                    </div>
                                </Transition.Child>
                            </div>
                        </Dialog>
                    </Transition>
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
                    <form onSubmit={this.handleSubmit}>
                        <label className={`mt-5 font-semibold text-sm ${this.state.isEmptyAnswer ? 'text-red-500' : 'text-gray-600 dark:text-dark-text-primary'} pb-1 block ${this.state.dyslexia ? 'dyslexia-font' : ''}`}>Jouw antwoord: <span className="italic">{this.state.isEmptyAnswer ? ' — Dit veld is verplicht' : ''}</span></label>
                        <input name="myAnswer" type="text" autoComplete="off" value={this.state.myAnswer} onChange={this.handleChange} className={`border dark:border-dark-tertiary rounded-lg px-3 py-2 mt-1 mb-5 text-sm w-full dark:bg-dark-secondary ${this.state.dyslexia ? 'dyslexia-font' : ''}`} />
                        <button type="submit" className="whitespace-nowrap px-4 py-2 border border-transparent rounded-md shadow-sm text-base font-medium text-white bg-blue-500 hover:bg-blue-600">Nakijken</button>
                    </form>
                </div>
            </div>
        );
    }

}
