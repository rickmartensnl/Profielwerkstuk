import React from "react";
import MetaTags from "react-meta-tags";
import { AuthMiddleware } from "../../middlewares/AuthMiddleware";
import { ParagraphsChild } from './ParagraphsChild';
import { apiRoute } from "../App";
import axios from "axios";
import { Header } from "../shared/Header";
export * from './ParagraphsChild';

export class Paragraphs extends React.Component {

    constructor(props) {
        super(props);

        this.state = {
            dyslexia: false,
            loggedIn: true,
            subject: {},
            chapter: {},
            paragraphs: []
        };

        this.authMiddleware = new AuthMiddleware();
    }

    async componentDidMount() {
        let valid = await this.authMiddleware.isValid();

        if (!valid) {
            this.setState(prevState => ({
                loggedIn: false
            }));
            this.props.history.push('/login');
        }

        let subjectRequest = await axios.get(apiRoute() + `/subjects/${this.props.match.params.subjectUuid}`);
        let chapterRequest = await axios.get(apiRoute() + `/chapters/${this.props.match.params.chapterUuid}`);
        let paragraphsRequest = await axios.get(apiRoute() + `/chapters/${this.props.match.params.chapterUuid}/paragraphs`);
        this.setState({
            subject: subjectRequest.data,
            chapter: chapterRequest.data,
            paragraphs: paragraphsRequest.data
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
        let paragraphChilds = this.state.paragraphs;

        let data = {
            authMiddleware: this.authMiddleware,
            dyslexia: this.state.dyslexia,
            loggedIn: this.state.loggedIn,
        }

        return(
            <div>
                <MetaTags>
                    <title>Profielwerkstuk — Chapter — {this.state.chapter.name}</title>
                    <meta name="description" content={`Start learning for the ${this.state.chapter.name} subject!`} />
                </MetaTags>
                <Header data={data} />
                <div className="container mx-auto">
                    <h1 className={`text-center text-3xl font-bold dark:text-dark-text-primary ${this.state.dyslexia ? 'dyslexia-font' : ''}`}>
                        {this.state.subject.name} - {this.state.chapter.name}
                    </h1>
                    <h2 className={`text-center text-1xl text-gray-700 dark:text-dark-text-secondary ${this.state.dyslexia ? 'dyslexia-font' : ''}`}>
                        Selecteer een paragraaf om te leren!<br />
                    </h2>
                    <div className="mt-10 grid justify-center gap-4 mx-4 sm:grid-cols-1 lg:grid-cols-2 xl:grid-cols-3 2xl:grid-cols-4">
                        {paragraphChilds.map(paragraph => {
                            const data = {
                                authMiddleware: this.authMiddleware,
                                dyslexia: this.state.dyslexia,
                                paragraph: paragraph,
                                subject: this.state.subject,
                                chapter: this.state.chapter
                            }

                            return(<ParagraphsChild data={data} key={paragraph.uuid.toString()} />);
                        })}
                    </div>
                </div>
            </div>
        );
    }

}
