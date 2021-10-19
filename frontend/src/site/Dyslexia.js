import React from 'react';
import MetaTags from "react-meta-tags";
import { AuthMiddleware } from "../middlewares/AuthMiddleware";

export class Dyslexia extends React.Component {

    constructor(props) {
        super(props);

        this.state = {
            dyslexia: false
        };

        this.toggleDyslexia = this.toggleDyslexia.bind(this);
        let authMiddleware = new AuthMiddleware();
        console.log(authMiddleware.getUser());
    }

    toggleDyslexia() {
        this.setState(prevState => ({
            dyslexia: !prevState.dyslexia
        }));
    }

    render() {
        return(
            <div>
                <MetaTags>
                    <title>Profielwerkstuk — Dyslexia</title>
                    <meta name="description" content="Dyslexia test lol, fun." />
                </MetaTags>
                <p className={this.state.dyslexia ? 'dyslexia-font' : ''}>
                    Test I am testing very much
                </p>

                <button onClick={ this.toggleDyslexia }>
                    Toggle dyslexia
                </button>
            </div>
        );
    }

}
