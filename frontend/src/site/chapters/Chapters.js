import React from "react";
import MetaTags from "react-meta-tags";
import { AuthMiddleware } from "../../middlewares/AuthMiddleware";
import { ChaptersChild } from './ChaptersChild';
import { apiRoute } from "../App";
import axios from "axios";
export * from './ChaptersChild';

export class Chapters extends React.Component {

    constructor(props) {
        super(props);

        this.state = {
            dyslexia: false,
            subject: {},
            chapters: []
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


        axios.get(apiRoute() + `/subjects/${this.props.match.params.subjectUuid}`).then(res => {
            this.setState({
                subject: res.data
            });
        });

        axios.get(apiRoute() + `/subjects/${this.props.match.params.subjectUuid}/chapters`).then(res => {
            this.setState({
                chapters: res.data
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
        let chapterChilds = this.state.chapters;

        return(
            <div className="container mx-auto">
                <MetaTags>
                    <title>Profielwerkstuk — Subject — {this.state.subject.name}</title>
                    <meta name="description" content={`Start learning for the ${this.state.subject.name} subject!`} />
                </MetaTags>
                <h1 className={`text-center text-3xl font-bold dark:text-dark-text-primary ${this.state.dyslexia ? 'dyslexia-font' : ''}`}>
                    {this.state.subject.name}
                </h1>
                <h2 className={`text-center text-1xl text-gray-700 dark:text-dark-text-secondary ${this.state.dyslexia ? 'dyslexia-font' : ''}`}>
                    Selecteer een hoofdstuk om verder te gaan met leren, <br />
                    of leer alle hoofdstukken in een keer.
                </h2>
                <div className="mt-10 grid justify-center gap-4 mx-4 sm:grid-cols-1 lg:grid-cols-2 xl:grid-cols-3 2xl:grid-cols-4">
                    {chapterChilds.map(chapter => {
                        const data = {
                            authMiddleware: this.authMiddleware,
                            dyslexia: this.state.dyslexia,
                            chapter: chapter,
                            subject: this.state.subject
                        }

                        return(<ChaptersChild data={data} key={chapter.uuid.toString()} />);
                    })}
                </div>
            </div>
        );
    }

}