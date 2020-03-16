// This sample demonstrates handling intents from an Alexa skill using the Alexa Skills Kit SDK (v2).
// Please visit https://alexa.design/cookbook for additional examples on implementing slots, dialog management,
// session persistence, api calls, and more.
const Alexa = require('ask-sdk-core');
const axios = require('axios');

const LaunchRequestHandler = {
    canHandle(handlerInput) {
        return Alexa.getRequestType(handlerInput.requestEnvelope) === 'LaunchRequest';
    },
    async handle(handlerInput) {
        let alexaId = handlerInput.requestEnvelope.session.user.userId;
        console.log("alexaId:" + alexaId);
        let speakOutput = 'Un mundo sin límites';
    
        await watson('Bienvenida',alexaId)
        .then((res) => {
          console.log(res.data);
          speakOutput = res.data.output.text[0];
        })
        .catch((error) => {
          console.error(error);
        });
        
        return handlerInput.responseBuilder
            .speak(speakOutput)
            .reprompt(speakOutput)
            .getResponse();
    }
};

const OTPIntentHandler = {
    canHandle(handlerInput) {
        return Alexa.getRequestType(handlerInput.requestEnvelope) === 'IntentRequest'
            && Alexa.getIntentName(handlerInput.requestEnvelope) === 'OTPIntent';
    },
    async handle(handlerInput) {
        let alexaId = handlerInput.requestEnvelope.session.user.userId;
        let otp = Alexa.getSlotValue(handlerInput.requestEnvelope, 'otp');
        console.log(alexaId);
        let speakOutput = "Diners Club no ha confirmado el OTP";
        
        await validation(otp, alexaId)
        .then((res) => {
          console.log(res.data);
          speakOutput = res.data.output.text[0];
        })
        .catch((error) => {
          console.error(error);
          speakOutput = error.response.data.output.text[0];
        });
        return handlerInput.responseBuilder
            .speak(speakOutput)
            .getResponse();
    }
};

const RegisterUserIntentHandler = {
    canHandle(handlerInput) {
        return Alexa.getRequestType(handlerInput.requestEnvelope) === 'IntentRequest'
            && Alexa.getIntentName(handlerInput.requestEnvelope) === 'RegisterUserIntent';
    },
    async handle(handlerInput) {
        let alexaId = handlerInput.requestEnvelope.session.user.userId;
        let identification = Alexa.getSlotValue(handlerInput.requestEnvelope, 'cedula');
        console.log(identification);
        let speakOutput = "Diners Club no ha respondido";
        
        await register(identification, alexaId)
        .then((res) => {
          speakOutput = res.data.output.text[0];
        })
        .catch((error) => {
          console.error(error);
        });
        return handlerInput.responseBuilder
            .speak(speakOutput)
            .getResponse();
    }
};

const AfirmativeIntentHandler = {
    canHandle(handlerInput) {
        return Alexa.getRequestType(handlerInput.requestEnvelope) === 'IntentRequest'
            && Alexa.getIntentName(handlerInput.requestEnvelope) === 'AfirmativeIntent';
    },
    async handle(handlerInput) {
        console.log(JSON.stringify(handlerInput));
        console.log(handlerInput.requestEnvelope.request.intent.name);
        console.log(handlerInput.requestEnvelope.session.user.userId);
        
        const sessionAttributes = handlerInput.attributesManager.getSessionAttributes();
        console.log(JSON.stringify(sessionAttributes));
        let slot = Alexa.getSlotValue(handlerInput.requestEnvelope, 'words');
        let intent = slot;
        console.log(intent);
        console.log(Alexa.getDialogState(handlerInput.requestEnvelope));
        
        let speakOutput = intent;
        let stayAlive = false;
        await watson(intent,'0917240293')
        .then((res) => {
          console.log(res.data);
          speakOutput = res.data.output.text[0];
          stayAlive = res.data.context.stayAlive;
          console.log(res.data.context.descripcion);
          if(stayAlive){
            return handlerInput.responseBuilder
                .speak(speakOutput)
                .reprompt('')
                .getResponse();
          }
        })
        .catch((error) => {
          console.error(error);
        });
        return handlerInput.responseBuilder
            .speak(speakOutput)
            .getResponse();
    }
};

const WordsIntentHandler = {
    canHandle(handlerInput) {
        return Alexa.getRequestType(handlerInput.requestEnvelope) === 'IntentRequest'
            && Alexa.getIntentName(handlerInput.requestEnvelope) === 'WordsIntent';
    },
    async handle(handlerInput) {
        console.log(JSON.stringify(handlerInput));
        console.log(handlerInput.requestEnvelope.request.intent.name);
        console.log(handlerInput.requestEnvelope.session.user.userId);
        let alexaId = handlerInput.requestEnvelope.session.user.userId;
        let slot = Alexa.getSlotValue(handlerInput.requestEnvelope, 'words');
        let intent = slot;
        console.log(intent);
        console.log(Alexa.getDialogState(handlerInput.requestEnvelope));
        
        let speakOutput = intent;
        let stayAlive = false;
        await watson(intent,'0917240293')
        .then((res) => {
          console.log(res.data);
          speakOutput = res.data.output.text[0];
          stayAlive = res.data.context.stayAlive;
          console.log(res.data.context.descripcion);
          if(stayAlive){
            return handlerInput.responseBuilder
                .speak(speakOutput)
                .reprompt('')
                .getResponse();
          }
        })
        .catch((error) => {
          console.error(error);
        });
        return handlerInput.responseBuilder
            .speak(speakOutput)
            .getResponse();
    }
};

const watson = function(intent, userId){
    let url = 'https://chatqa.dce.ec:32510/alexa-demo/api/watson/init';
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
    let url = 'https://chatqa.dce.ec:32510/alexa-demo/api/watson/register';
    return axios.post(url, {
            "deviceId": userId,
            "identification": identification,
        });
};
const validation = function(otp, userId){
    let url = 'https://chatqa.dce.ec:32510/alexa-demo/api/watson/validate';
    return axios.post(url, {
            "deviceId": userId,
            "otp": otp,
        });
};

/*
const HelloWorldIntentHandler = {
    canHandle(handlerInput) {
        return Alexa.getRequestType(handlerInput.requestEnvelope) === 'IntentRequest'
            && Alexa.getIntentName(handlerInput.requestEnvelope) === 'HelloWorldIntent';
    },
    handle(handlerInput) {
         console.log('HelloWorldIntent');
         console.log(handlerInput);
        console.log(JSON.stringify(handlerInput).requestEnvelope.request.intent.name);
        const speakOutput = 'Hola Christian...';
        return handlerInput.responseBuilder
            .speak(speakOutput)
            //.reprompt('add a reprompt if you want to keep the session open for the user to respond')
            .getResponse();
    }
};*/

const saldoTarjetaHandler = {
    canHandle(handlerInput) {
        return Alexa.getRequestType(handlerInput.requestEnvelope) === 'IntentRequest'
            && Alexa.getIntentName(handlerInput.requestEnvelope) === 'saldoTarjeta';
    },
    async handle(handlerInput) {
        console.log(JSON.stringify(handlerInput));
        console.log(handlerInput.requestEnvelope.request.intent.name);
        console.log(handlerInput.requestEnvelope.session.user.userId);
        
        let slot = handlerInput.requestEnvelope.request.intent.slots.tarjeta.value;
        let intent = 'saldo ' + slot;
        console.log(intent);
        
        let speakOutput = 'Hola Christian...';
        await watson("bienvenido",'1104058837')
        .then((res) => {
          console.log(res.data);
          speakOutput = res.data.context.descripcion;
          console.log(res.data.context.descripcion);
        })
        .catch((error) => {
          console.error(error);
        });
        return handlerInput.responseBuilder
            .speak(speakOutput)
            //.reprompt('add a reprompt if you want to keep the session open for the user to respond')
            .getResponse();
    }
};

const FallbackIntentHandler = {
    canHandle(handlerInput) {
        return Alexa.getRequestType(handlerInput.requestEnvelope) === 'IntentRequest'
            && Alexa.getIntentName(handlerInput.requestEnvelope) === 'AMAZON.FallbackIntent';
    },
    handle(handlerInput) {
         console.log(JSON.stringify(handlerInput));
        //const speakOutput = 'No olvides decir la palabra quiero, antes de tu solicitud';
        const speakOutput = 'No entendí dímelo nuevamente';

        return handlerInput.responseBuilder
            .speak(speakOutput)
            .reprompt(speakOutput)
            .getResponse();
    }
};
const HelpIntentHandler = {
    canHandle(handlerInput) {
        return Alexa.getRequestType(handlerInput.requestEnvelope) === 'IntentRequest'
            && Alexa.getIntentName(handlerInput.requestEnvelope) === 'AMAZON.HelpIntent';
    },
    handle(handlerInput) {
        const speakOutput = 'You can say hello to me! How can I help?';

        return handlerInput.responseBuilder
            .speak(speakOutput)
            .reprompt(speakOutput)
            .getResponse();
    }
};
const CancelAndStopIntentHandler = {
    canHandle(handlerInput) {
        return Alexa.getRequestType(handlerInput.requestEnvelope) === 'IntentRequest'
            && (Alexa.getIntentName(handlerInput.requestEnvelope) === 'AMAZON.CancelIntent'
                || Alexa.getIntentName(handlerInput.requestEnvelope) === 'AMAZON.StopIntent');
    },
    handle(handlerInput) {
        const speakOutput = 'Goodbye!';
        return handlerInput.responseBuilder
            .speak(speakOutput)
            .getResponse();
    }
};
const SessionEndedRequestHandler = {
    canHandle(handlerInput) {
        return Alexa.getRequestType(handlerInput.requestEnvelope) === 'SessionEndedRequest';
    },
    handle(handlerInput) {
        // Any cleanup logic goes here.
        return handlerInput.responseBuilder.getResponse();
    }
};

// The intent reflector is used for interaction model testing and debugging.
// It will simply repeat the intent the user said. You can create custom handlers
// for your intents by defining them above, then also adding them to the request
// handler chain below.
const IntentReflectorHandler = {
    canHandle(handlerInput) {
        return Alexa.getRequestType(handlerInput.requestEnvelope) === 'IntentRequest';
    },
    handle(handlerInput) {
        const intentName = Alexa.getIntentName(handlerInput.requestEnvelope);
        const speakOutput = `You just triggered ${intentName}`;

        return handlerInput.responseBuilder
            .speak(speakOutput)
            //.reprompt('add a reprompt if you want to keep the session open for the user to respond')
            .getResponse();
    }
};

// Generic error handling to capture any syntax or routing errors. If you receive an error
// stating the request handler chain is not found, you have not implemented a handler for
// the intent being invoked or included it in the skill builder below.
const ErrorHandler = {
    canHandle() {
        return true;
    },
    handle(handlerInput, error) {
        console.log(`~~~~ Error handled: ${error.stack}`);
        const speakOutput = `Sorry, I had trouble doing what you asked. Please try again.`;

        return handlerInput.responseBuilder
            .speak(speakOutput)
            .reprompt(speakOutput)
            .getResponse();
    }
};

// The SkillBuilder acts as the entry point for your skill, routing all request and response
// payloads to the handlers above. Make sure any new handlers or interceptors you've
// defined are included below. The order matters - they're processed top to bottom.
exports.handler = Alexa.SkillBuilders.custom()
    .addRequestHandlers(
        WordsIntentHandler,
        AfirmativeIntentHandler,
        RegisterUserIntentHandler,
        OTPIntentHandler,
        FallbackIntentHandler,
        LaunchRequestHandler,
        //HelloWorldIntentHandler,
        HelpIntentHandler,
        CancelAndStopIntentHandler,
        SessionEndedRequestHandler,
        IntentReflectorHandler, // make sure IntentReflectorHandler is last so it doesn't override your custom intent handlers
    )
    .addErrorHandlers(
        ErrorHandler,
    )
    .lambda();
