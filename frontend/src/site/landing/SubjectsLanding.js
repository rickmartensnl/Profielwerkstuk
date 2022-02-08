/*
 * Copyright (c) 2022 Rick Martens - All rights not expressly granted herein are reserved
 *
 * This material is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International.
 */

import React from "react";
import MetaTags from "react-meta-tags";
import { AuthMiddleware } from "../../middlewares/AuthMiddleware";
import { apiRoute } from "../App";
import axios from "axios";
import { Header } from "../shared/Header";

export class SubjectsLanding extends React.Component {

    constructor(props) {
        super(props);

        this.state = {
            dyslexia: false,
            loggedIn: false,
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
                    <title>Modern Math — Subjects</title>
                    <meta name="description" content="About the mathematics subject." />
                </MetaTags>
                <Header data={data} />
                <div className="container mx-auto">
                    <h1 className={`text-center text-3xl font-bold dark:text-dark-text-primary ${this.state.dyslexia ? 'dyslexia-font' : ''}`}>
                        Wiskunde B
                    </h1>
                </div>
            </div>
        );
    }

}
