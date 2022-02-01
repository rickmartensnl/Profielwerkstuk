import React from 'react';
import MetaTags from "react-meta-tags";
import { Header } from "../shared/Header";
import { AuthMiddleware } from "../../middlewares/AuthMiddleware";
import { ArrowLeftIcon, ExclamationCircleIcon } from "@heroicons/react/outline";
import { Link } from "react-router-dom";

export class NotFound extends React.Component {

    constructor(props) {
        super(props);

        this.state = {
            dyslexia: false,
            loggedIn: false
        };

        this.authMiddleware = new AuthMiddleware();
    }

    async componentDidMount() {
        let valid = await this.authMiddleware.isValid();

        if (valid) {
            this.setState(prevState => ({
                loggedIn: true
            }));
        }

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
        let data = {
            authMiddleware: this.authMiddleware,
            dyslexia: this.state.dyslexia,
            loggedIn: this.state.loggedIn,
        }

        return(
            <div>
                <MetaTags>
                    <title>Modern Math — Page not found!</title>
                    <meta name="description" content="A modernized way to improve learning experiences." />
                </MetaTags>
                <Header data={data} />
                <div className="container mx-auto p-5">
                    <h1 className={`group flex rounded-md items-center w-full px-2 py-2 text-4xl`}>
                        <ExclamationCircleIcon className="w-5 h-5 mr-5" />
                        Pagina niet gevonden!
                    </h1>
                    <p className={`dark:text-dark-text-secondary`}>
                        We konden de pagina die je zocht niet vinden. Probeer het later nog eens.
                    </p>
                    <Link to="/" className="mt-8 whitespace-nowrap inline-flex items-center justify-center px-4 py-2 border border-transparent rounded-md shadow-sm text-base font-medium text-white bg-blue-500 hover:bg-blue-600">
                        <ArrowLeftIcon className="w-5 h5 mr-2" />
                        Terug naar de homepagina!
                    </Link>
                </div>
            </div>
        );
    }

}
