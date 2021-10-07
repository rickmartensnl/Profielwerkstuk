import React from 'react';
import MetaTags from "react-meta-tags";

export class Home extends React.Component {

    render() {
        return(
            <div>
                <MetaTags>
                    <title>Profielwerkstuk — Modernize your Learning Experience</title>
                    <meta name="description" content="A modernized way to improve learning experiences." />
                </MetaTags>
                <p>
                    This is the body
                </p>
            </div>
        );
    }

}
