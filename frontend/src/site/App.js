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
import { NotFound } from "./errors/404";
import { SubjectsLanding } from "./landing/SubjectsLanding";
import { Settings } from "./settings/Settings";

export class App extends React.Component {

    render() {
        return (
            <BrowserRouter>
                <Switch>
                    <Route exact path="/" component={Home} />

                    <Route exact path="/login" component={Login} />
                    <Route exact path="/logout" component={Logout} />
                    <Route exact path="/register" component={Register} />

                    <Route exact path="/settings" component={Settings} />

                    <Route exact path="/app">
                        <Redirect to="/subjects" />
                    </Route>

                    <Route exact path="/subjects" component={Subjects} />
                    <Route exact path="/subjects/:subjectUuid" component={Chapters} />
                    <Route exact path="/subjects/:subjectUuid/:chapterUuid" component={Paragraphs} />
                    <Route exact path="/subjects/:subjectUuid/:chapterUuid/:paragraphUuid/play" component={Play} />

                    <Route exact path="/landing/subjects/:subjectUuid" component={SubjectsLanding} />

                    {/* Dyslexia test */}
                    <Route path="/dyslexia" component={Dyslexia} />

                    <Route path="*" component={NotFound} />
                </Switch>
            </BrowserRouter>
        );
    }
}

export function apiRoute() {
    return location.origin + '/api/v1'
}
