import axios from "axios";
import { apiRoute } from "../site/App";
import {Redirect} from "react-router-dom";

export class AuthMiddleware {

    constructor() {
        this.token = localStorage.getItem("token");
    }

    isValid() {
        return this.getUser().then(res => {
            return res !== null;
        });
    }

    getUser() {
        try {
            return this.doRequest('/users/@me', "GET").then(res => {
                return res.data;
            });
        } catch (e) {
            return null;
        }
    }

    doRequest($endpoint, $method) {
        if (this.token == null) {
            return Promise.reject("noTokenSaved");
        }

        let config = {
            method: $method,
            url: apiRoute() + $endpoint,
            headers: {
                "Authorization": this.token
            }
        };

        return axios.request(config).then(result => {
            return result
        }).catch(error => {
            return Promise.reject(error);
        });
    }

}
