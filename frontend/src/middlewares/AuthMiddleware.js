/*
 * Copyright (c) 2022 Rick Martens - All rights not expressly granted herein are reserved
 *
 * This material is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International.
 */

import axios from "axios";
import { apiRoute } from "../site/App";

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

    newOrLastQuestion() {
        try {
            return this.doRequest('/users/@me/sessions?type=unfinished', "GET").then(res => {
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
