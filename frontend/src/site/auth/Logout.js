import React from 'react';
import {Link} from "react-router-dom";

export class Logout extends React.Component {

    constructor(props) {
        super(props);

        document.title = 'Profielwerkstuk — Logout';
    }

    componentDidMount() {
        localStorage.removeItem("token");
        this.props.history.push('/login');
    }

    render() {
        return(
            <div className="min-h-screen bg-gray-100 flex flex-col justify-center sm:py-12">
                <div className="p-10 xs:p-0 mx-auto md:w-full md:max-w-md">
                    <h1 className="font-bold text-center text-2xl mb-5">Profielwerkstuk</h1>
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
