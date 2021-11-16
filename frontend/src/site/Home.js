import React from 'react';
import MetaTags from "react-meta-tags";
import { Header } from "./shared/Header";

export class Home extends React.Component {

    render() {
        return(
            <div>
                <MetaTags>
                    <title>Profielwerkstuk — Modernize your Learning Experience</title>
                    <meta name="description" content="A modernized way to improve learning experiences." />
                </MetaTags>
                <Header />
                <p>
                    This is the body
                </p>
            </div>
        );
    }

}
