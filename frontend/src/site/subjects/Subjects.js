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
            subjects: []
        };

        this.authMiddleware = new AuthMiddleware();
    }

    componentDidMount() {
        this.authMiddleware.isValid().then(res => {
            if (!res) {
                this.props.history.push('/login');
            }
        }).catch(err => {
            this.props.history.push('/login');
        });


        axios.get(apiRoute() + '/subjects').then(res => {
            this.setState({
                subjects: res.data
            });
        });

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

    render() {
        let subjectChilds = this.state.subjects;

        return(
            <div>
                <MetaTags>
                    <title>Profielwerkstuk — Subjects</title>
                    <meta name="description" content="Select a subject to start learning from." />
                </MetaTags>
                <Header />
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
