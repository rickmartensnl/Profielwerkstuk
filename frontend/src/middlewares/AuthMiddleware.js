import axios from "axios";
import { apiRoute } from "../site/App";

export class AuthMiddleware {

    constructor() {
        this.token = localStorage.getItem("token");
    }

    isValid() {
        if (this.getUser() === null) {
            return false;
        }
    }

    getUser() {
        return this.doRequest('/users/@me', "GET").then(res => {
            return res.data;
        }).catch(err => {
            return null;
        });
    }

    doRequest($endpoint, $method) {
        if (this.token == null) {
            return null;
        }

        let config = {
            headers: {
                "Authorization": this.token
            }
        };

        return axios.get(apiRoute() + $endpoint, config).then(result => {
            return result
        }).catch(error => {
            return Promise.reject(error);
        });
    }

}
