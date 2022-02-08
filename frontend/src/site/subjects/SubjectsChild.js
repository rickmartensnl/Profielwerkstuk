/*
 * Copyright (c) 2022 Rick Martens - All rights not expressly granted herein are reserved
 *
 * This material is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International.
 */

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

        let progress = Math.floor(Math.random() * 100);

        let progressStyle = {
            width: progress + '%'
        }

        let subjectUuid = this.props.data.subject.uuid.replaceAll('-', '');

        return(
            <div className="rounded-xl p-8 w-full bg-gray-100 dark:bg-dark-primary">
                <div className="content-center">
                    <h3 className={`font-bold text-lg inline-block ${this.props.data.dyslexia ? 'dyslexia-font' : ''}`}>
                        {this.subject.name}
                        <div className={`has-tooltip inline-block fill-current align-middle ml-2 -mt-1.5 ${verified ? '' : 'hidden'}`}>
                            <span className="tooltip rounded shadow-lg p-1 bg-gray-100 dark:bg-dark-tertiary -mt-10 text-base font-normal">Verified Subject</span>
                            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" className="w-5 h-5">
                                <path d="M0 0h24v24H0V0z" fill="none"/>
                                <path d="M12 1L3 5v6c0 5.55 3.84 10.74 9 12 5.16-1.26 9-6.45 9-12V5l-9-4zm7 10c0 4.52-2.98 8.69-7 9.93-4.02-1.24-7-5.41-7-9.93V6.3l7-3.11 7 3.11V11zm-11.59.59L6 13l4 4 8-8-1.41-1.42L10 14.17z"/>
                            </svg>
                        </div>
                    </h3>
                    <Link to={`/subjects/${subjectUuid}`} className="p-2 text-right inline-block float-right bg-blue-500 rounded-lg">
                        Ga verder met leren!
                    </Link>
                </div>
                <div className="relative mt-8 -mx-8 -mb-8">
                    <div className="has-tooltip overflow-hidden h-3 text-xs flex rounded-b-xl bg-gray-300 dark:bg-dark-tertiary">
                        <div style={progressStyle} className="shadow-none flex flex-col text-center whitespace-nowrap text-white justify-center bg-green-400 dark:bg-green-600"/>
                        <span className="tooltip rounded shadow-lg p-1 bg-gray-100 dark:bg-dark-tertiary -mt-10 text-base font-normal">This is not your real progress.</span>
                    </div>
                </div>
            </div>
        );
    }

}