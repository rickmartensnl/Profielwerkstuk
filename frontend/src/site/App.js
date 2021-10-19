import React from 'react';
import {
    BrowserRouter as Router,
    Switch,
    Route,
    Link
} from "react-router-dom";
import { Home } from './Home';
import { Login, Logout, Register } from './auth/Auth';
import { Dyslexia } from './Dyslexia';

export class App extends React.Component {
    render() {
        return (
            <Router>
                <Switch>
                    <Route exact path="/" component={Home} />

                    <Route exact path="/login" component={Login} />
                    <Route exact path="/logout" component={Logout} />
                    <Route exact path="/register" component={Register} />

                    <Route exact path="/app" component={Child} />

                    <Route exact path="/subjects" component={Child} />
                    <Route exact path="/subjects/:id" component={Child} />

                    <Route exact path="/subjects/:id/chapters" component={Child} />
                    <Route exact path="/subjects/:id/chapters/:id" component={Child} />

                    {/* Dyslexia test */}
                    <Route path="/dyslexia" component={Dyslexia} />

                    <Route path="*">
                        <div>
                            <h1>Whoops, I can't find this page!</h1>
                            <Link to="/">Get back to the home page.</Link>
                        </div>
                    </Route>
                </Switch>
            </Router>
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
            id: this.props.match.params.id
        };
    }

    render() {
        return (
            <p>ID: {this.state.id}</p>
        );
    }

}
