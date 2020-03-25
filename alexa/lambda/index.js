// This sample demonstrates handling intents from an Alexa skill using the Alexa Skills Kit SDK (v2).
// Please visit https://alexa.design/cookbook for additional examples on implementing slots, dialog management,
// session persistence, api calls, and more.
const Alexa = require('ask-sdk-core');
const watson = require('./watson.js');

const LaunchRequestHandler = {
    canHandle(handlerInput) {
        return Alexa.getRequestType(handlerInput.requestEnvelope) === 'LaunchRequest';
    },
    async handle(handlerInput) {
        let alexaId = handlerInput.requestEnvelope.session.user.userId;
        console.log("AlexaId Bienbenida: " + alexaId);
        let speakOutput = 'En este momento no estamos disponibles, por favor intenta en unos minutos.';
    
        await watson.interaction('Bienvenida',alexaId)
        .then((res) => {
          if(res.data){
            console.log(JSON.stringify(res.data));
            speakOutput = res.data.output.text[0];
            return handlerInput.responseBuilder
                .speak(speakOutput)
                .reprompt(speakOutput)
                .getResponse();
          }
        })
        .catch((error) => {
            if(error.response){
                console.error(JSON.stringify(error.response.data));
                speakOutput = error.response.data.output.text[0];
            }
        });
        
        return handlerInput.responseBuilder
            .speak(speakOutput)
            //.reprompt(speakOutput)
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
        
        await watson.validation(otp, alexaId)
        .then((res) => {
          console.log(res.data);
          speakOutput = res.data.output.text[0];
        })
        .catch((error) => {
            console.error(JSON.stringify(error.response.data));
            speakOutput = error.response.data.output.text[0];
        });
        return handlerInput.responseBuilder
            .speak(speakOutput)
            .getResponse();
    }
};

const UnlockIntentHandler = {
    canHandle(handlerInput) {
        return Alexa.getRequestType(handlerInput.requestEnvelope) === 'IntentRequest'
            && Alexa.getIntentName(handlerInput.requestEnvelope) === 'UnlockIntent';
    },
    async handle(handlerInput) {
        let alexaId = handlerInput.requestEnvelope.session.user.userId;
        let speakOutput = "Diners Club no ha respondido";
        
        await watson.unlock(alexaId)
        .then((res) => {
          speakOutput = res.data.output.text[0];
        })
        .catch((error) => {
            console.error(JSON.stringify(error.response.data));
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
        console.log('Registro AlexaId: ' + alexaId);
        console.log(identification);
        let speakOutput = "Diners Club no ha respondido";
        
        await watson.register(identification, alexaId)
        .then((res) => {
          speakOutput = res.data.output.text[0];
        })
        .catch((error) => {
            console.error(JSON.stringify(error.response.data));
            speakOutput = error.response.data.output.text[0];
          
        });
        return handlerInput.responseBuilder
            .speak(speakOutput)
            .getResponse();
    }
};

const EverythingIntentHandler = {
    canHandle(handlerInput) {
        return Alexa.getRequestType(handlerInput.requestEnvelope) === 'IntentRequest'
            && Alexa.getIntentName(handlerInput.requestEnvelope) === 'EverythingIntent';
    },
    async handle(handlerInput) {
        console.log(JSON.stringify(handlerInput));

        let slot = Alexa.getSlotValue(handlerInput.requestEnvelope, 'words');
        let intent = slot;
        console.log(intent);
        let alexaId = handlerInput.requestEnvelope.session.user.userId;
        
        let speakOutput = intent;
        let stayAlive = false;
        await watson.interaction(intent,alexaId)
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
            console.error(JSON.stringify(error.response.data));
            speakOutput = error.response.data.output.text[0];
        });
        return handlerInput.responseBuilder
            .speak(speakOutput)
            .getResponse();
    }
};
/*
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
        await watson(intent,alexaId)
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
            console.error(JSON.stringify(error.response.data));
            speakOutput = error.response.data.output.text[0];
        });
        return handlerInput.responseBuilder
            .speak(speakOutput)
            .getResponse();
    }
};
*/

/*const FallbackIntentHandler = {
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
};*/
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
        const speakOutput = `No se encotró el intent llamado: ${intentName}`;

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
        const speakOutput = `En este momento no te podemos atender, por favor intenta más tarde.`;

        return handlerInput.responseBuilder
            .speak(speakOutput)
            //.reprompt(speakOutput)
            .getResponse();
    }
};

// The SkillBuilder acts as the entry point for your skill, routing all request and response
// payloads to the handlers above. Make sure any new handlers or interceptors you've
// defined are included below. The order matters - they're processed top to bottom.
exports.handler = Alexa.SkillBuilders.custom()
    .addRequestHandlers(
        RegisterUserIntentHandler,
        OTPIntentHandler,
        UnlockIntentHandler,
        EverythingIntentHandler,
        //FallbackIntentHandler,
        LaunchRequestHandler,
        HelpIntentHandler,
        CancelAndStopIntentHandler,
        SessionEndedRequestHandler,
        IntentReflectorHandler, // make sure IntentReflectorHandler is last so it doesn't override your custom intent handlers
    )
    .addErrorHandlers(
        ErrorHandler,
    )
    .lambda();
