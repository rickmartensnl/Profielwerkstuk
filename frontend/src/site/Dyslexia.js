import React from 'react';

export class Dyslexia extends React.Component {

    constructor(props) {
        super(props);

        this.state = {
            dyslexia: false
        };

        this.toggleDyslexia = this.toggleDyslexia.bind(this);
    }

    toggleDyslexia() {
        this.setState(prevState => ({
            dyslexia: !prevState.dyslexia
        }));
    }

    render() {
        return(
            <div>
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
