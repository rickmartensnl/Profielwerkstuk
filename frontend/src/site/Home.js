/*
 * Copyright (c) 2022 Rick Martens - All rights not expressly granted herein are reserved
 *
 * This material is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International.
 */

import React from 'react';
import MetaTags from "react-meta-tags";
import { Header } from "./shared/Header";
import { AuthMiddleware } from "../middlewares/AuthMiddleware";

export class Home extends React.Component {

    constructor(props) {
        super(props);

        this.state = {
            dyslexia: false,
            loggedIn: false
        };

        this.authMiddleware = new AuthMiddleware();
    }

    async componentDidMount() {
        try {
            this.setState(prevState => ({
                loggedIn: true
            }));

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
                throw new Error("Fix darkmode");
            }
        } catch (e) {
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

    render() {
        let data = {
            authMiddleware: this.authMiddleware,
            dyslexia: this.state.dyslexia,
            loggedIn: this.state.loggedIn,
        }

        return(
            <div>
                <MetaTags>
                    <title>Modern Math — Modernize your Learning Experience</title>
                    <meta name="description" content="A modernized way to improve learning experiences." />
                </MetaTags>
                <Header data={data} />
                <div className="container mx-auto">
                    <h1 className={`text-center text-3xl font-bold dark:text-dark-text-primary ${this.state.dyslexia ? 'dyslexia-font' : ''}`}>Modern Math</h1>
                    <h2 className={`text-center text-xl text-gray-700 dark:text-dark-text-secondary ${this.state.dyslexia ? 'dyslexia-font' : ''}`}>
                        Een moderne leeromgeving voor wiskunde
                    </h2>
                    <p className={`mt-4 text-center text-base text-gray-700 dark:text-dark-text-secondary ${this.state.dyslexia ? 'dyslexia-font' : ''}`}>
                        Dit project is een profielwerkstuk en is gemaakt om te helpen met het herhalen van voorgaande hoofdstukken om jezelf voor te bereiden op de examens.<br/>
                    </p>
                    <div className="max-w-lg mx-auto pt-5">
                        <div className="flex bg-yellow-100 rounded-lg p-4 mb-4 text-sm text-yellow-700" role="alert">
                            <svg className="w-5 h-5 inline mr-3" fill="currentColor" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg">
                                <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clip-rule="evenodd"/>
                            </svg>
                            <div>
                                <span className="font-medium">Waarschuwing!</span> Dit programma is in bèta, er kunnen nog bugs in het systeem zitten.
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        );
    }

}
