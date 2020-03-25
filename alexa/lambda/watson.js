const axios = require('axios');

const endpoint = 'https://chatqa.dce.ec:30013/alexa-demo';
const interaction = function(intent, userId){
    let url = endpoint + '/api/watson/init';
    return axios.post(url, {
            "input": {
                "text": intent
            },
            "context": {
                "device_id": userId
            }
        });
};
const register = function(identification, userId){
    let url = endpoint + '/api/watson/register';
    return axios.post(url, {
            "deviceId": userId,
            "identification": identification,
        });
};
const validation = function(otp, userId){
    let url = endpoint + '/api/watson/validate';
    return axios.post(url, {
            "deviceId": userId,
            "otp": otp,
        });
};
const unlock = function(userId){
    let url = endpoint + '/api/watson/unlock';
    return axios.post(url, {
            "deviceId": userId,
        });
};
const resend = function(userId){
    let url = endpoint + '/api/watson/resend';
    return axios.post(url, {
            "deviceId": userId,
        });
};

module.exports = {
    interaction,
    register,
    validation,
    unlock,
    resend
}
