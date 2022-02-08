/*
 * Copyright (c) 2022 Rick Martens - All rights not expressly granted herein are reserved
 *
 * This material is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International.
 */

import React from 'react';
import MetaTags from "react-meta-tags";

export class Logout extends React.Component {

    constructor(props) {
        super(props);
    }

    componentDidMount() {
        localStorage.removeItem("token");
        this.props.history.push('/login');
    }

    render() {
        return(
            <div className="min-h-screen bg-gray-100 flex flex-col justify-center sm:py-12">
                <MetaTags>
                    <title>Modern Math — Logout</title>
                    <meta name="description" content="Logout from your modernized learning experience." />
                </MetaTags>
                <div className="p-10 xs:p-0 mx-auto md:w-full md:max-w-md">
                    <h1 className="font-bold text-center text-2xl mb-5">Modern Math</h1>
                    <div className="bg-white shadow w-full rounded-lg divide-y divide-gray-200">
                        <form className="px-5 py-7">
                            <h2 className="font-semibold text-lg text-gray-600 pb-1 block text-center">Logging out...</h2>
                        </form>
                    </div>
                </div>
            </div>
        );
    }

}
