import React from 'react';
import {
    BrowserRouter,
    Switch,
    Redirect,
    Route,
    Link
} from "react-router-dom";
import { Home } from './Home';
import { Subjects } from './subjects/Subjects';
import { Paragraphs } from "./paragraphs/Paragraphs";
import { Chapters } from './chapters/Chapters';
import { Play } from './play/Play';
import { Login, Logout, Register } from './auth/Auth';
import { Dyslexia } from './Dyslexia';

export class App extends React.Component {

    render() {
        return (
            <BrowserRouter>
                <Switch>
                    <Route exact path="/" component={Home} />

                    <Route exact path="/login" component={Login} />
                    <Route exact path="/logout" component={Logout} />
                    <Route exact path="/register" component={Register} />

                    <Route exact path="/app">
                        <Redirect to="/subjects" />
                    </Route>

                    <Route exact path="/subjects" component={Subjects} />
                    <Route exact path="/subjects/:subjectUuid" component={Chapters} />
                    <Route exact path="/subjects/:subjectUuid/:chapterUuid" component={Paragraphs} />
                    <Route exact path="/subjects/:subjectUuid/:chapterUuid/:paragraphUuid/play" component={Play} />

                    {/* Dyslexia test */}
                    <Route path="/dyslexia" component={Dyslexia} />

                    <Route path="*">
                        <div>
                            <h1>Whoops, I can't find this page!</h1>
                            <Link to="/">Get back to the home page.</Link>
                        </div>
                    </Route>
                </Switch>
            </BrowserRouter>
        );
    }
}

export function apiRoute() {
    return location.origin + '/api/v1'
}

export class Child extends React.Component {

    constructor(props) {
        super(props);

        this.state = {
            paragraphUuid: this.props.match.params.paragraphUuid
        };
    }

    render() {
        return (
            <p>ID: {this.state.paragraphUuid}, Je zal verder gaan met dit leren als ik ready to go ben.</p>
        );
    }

}
