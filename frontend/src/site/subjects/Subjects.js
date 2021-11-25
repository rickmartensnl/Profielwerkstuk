import React from "react";
import MetaTags from "react-meta-tags";
import { AuthMiddleware } from "../../middlewares/AuthMiddleware";
import { SubjectsChild } from './SubjectsChild';
import { apiRoute } from "../App";
import axios from "axios";
import { Header } from "../shared/Header";
export * from './SubjectsChild';

export class Subjects extends React.Component {

    constructor(props) {
        super(props);

        this.state = {
            dyslexia: false,
            loggedIn: true,
            subjects: []
        };

        this.authMiddleware = new AuthMiddleware();
    }

    async componentDidMount() {
        try {
            let valid = await this.authMiddleware.isValid();

            if (!valid) {
                this.setState(prevState => ({
                    loggedIn: false
                }));
                this.props.history.push('/login');
            }
        } catch (e) {
            this.props.history.push('/login');
            return;
        }

        let subjectsRequest = await axios.get(apiRoute() + '/subjects');
        this.setState({
            subjects: subjectsRequest.data
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

    render() {
        let subjectChilds = this.state.subjects;

        let data = {
            authMiddleware: this.authMiddleware,
            dyslexia: this.state.dyslexia,
            loggedIn: this.state.loggedIn,
        }

        return(
            <div>
                <MetaTags>
                    <title>Profielwerkstuk — Subjects</title>
                    <meta name="description" content="Select a subject to start learning from." />
                </MetaTags>
                <Header data={data} />
                <div className="container mx-auto">
                    <h1 className={`text-center text-3xl font-bold dark:text-dark-text-primary ${this.state.dyslexia ? 'dyslexia-font' : ''}`}>
                        Hoofdstukken
                    </h1>
                    <h2 className={`text-center text-1xl text-gray-700 dark:text-dark-text-secondary ${this.state.dyslexia ? 'dyslexia-font' : ''}`}>
                        Selecteer een hoofdstuk om te beginnen met oefenen.
                    </h2>
                    <div className="mt-10 grid justify-items-center gap-4 mx-4">
                        {subjectChilds.map(subject => {
                            const data = {
                                authMiddleware: this.authMiddleware,
                                dyslexia: this.state.dyslexia,
                                subject: subject
                            }

                            return(<SubjectsChild data={data} key={subject.uuid.toString()} />);
                        })}
                    </div>
                </div>
            </div>
        );
    }

}
