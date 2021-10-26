import React from "react";
import { Link } from "react-router-dom";

export class SubjectsChild extends React.Component {

    constructor(props) {
        super(props);

        this.subject = props.data.subject;

        this.authMiddleware = this.props.data.authMiddleware;
    }

    render() {
        let verified = (this.subject.flags & 0x1) === 0x1;

        return(
            <div className="rounded-xl p-8 w-full bg-gray-100 dark:bg-dark-primary">
                <h3 className={`font-bold text-lg mb-2 ${this.props.data.dyslexia ? 'dyslexia-font' : ''}`}>
                    {this.subject.name}
                    <div className="has-tooltip inline-block fill-current ml-2">
                        <span className="tooltip rounded shadow-lg p-1 bg-gray-100 dark:bg-dark-tertiary -mt-9 text-base font-normal">Verified Chapter</span>
                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" className="w-5 h-5">
                            <path d="M0 0h24v24H0V0z" fill="none"/>
                            <path d="M12 1L3 5v6c0 5.55 3.84 10.74 9 12 5.16-1.26 9-6.45 9-12V5l-9-4zm7 10c0 4.52-2.98 8.69-7 9.93-4.02-1.24-7-5.41-7-9.93V6.3l7-3.11 7 3.11V11zm-11.59.59L6 13l4 4 8-8-1.41-1.42L10 14.17z"/>
                        </svg>
                    </div>
                </h3>
                <Link to={`/subjects/${this.subject.uuid.replaceAll('-', '')}`} className="bg-blue-500 p-2 m-5">
                    Ga verder met leren!
                </Link>
            </div>
        );
    }

}