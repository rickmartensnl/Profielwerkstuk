import React from 'react';
import MetaTags from "react-meta-tags";
import { Header } from "./shared/Header";
import {AuthMiddleware} from "../middlewares/AuthMiddleware";

export class Home extends React.Component {

    constructor(props) {
        super(props);

        this.state = {
            dyslexia: false,
            loggedIn: false
        };

        this.authMiddleware = new AuthMiddleware();
    }

    componentDidMount() {
        this.authMiddleware.isValid().then(res => {
            if (res) {
                this.setState(prevState => ({
                    loggedIn: true
                }));
            }
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

    render() {
        let data = {
            authMiddleware: this.authMiddleware,
            dyslexia: this.state.dyslexia,
            loggedIn: this.state.loggedIn,
        }

        return(
            <div>
                <MetaTags>
                    <title>Profielwerkstuk — Modernize your Learning Experience</title>
                    <meta name="description" content="A modernized way to improve learning experiences." />
                </MetaTags>
                <Header data={data} />
                <p>
                    This is the body
                </p>
            </div>
        );
    }

}
