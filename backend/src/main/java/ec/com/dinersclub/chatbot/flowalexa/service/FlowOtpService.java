package ec.com.dinersclub.chatbot.flowalexa.service;

import ec.com.dinersclub.chatbot.flowalexa.exception.OtpException;

public interface FlowOtpService {

	/**
	 * Registra usuario para envio otp.
	 * 
	 * @param identification Identificacion usuario.
	 * @throws OtpException
	 */
	void enrollmentOtp(String identification) throws OtpException;

	/**
	 * Se envia el codigo otp.
	 * 
	 * @param identification Identificacion del usuario.
	 * @param phoneNumber    TODO
	 * @throws OtpException
	 */
	void sendOtp(String identification, String phoneNumber) throws OtpException;

	/**
	 * Se valida el codigo otp recibido.
	 * 
	 * @param identification Identificacion del usuario.
	 * @param otpCode        Codigo otp recibido.
	 * @throws OtpException
	 */
	void validateOtp(String identification, String otpCode) throws OtpException;

	/**
	 * Se desbloquea el usuario.
	 * 
	 * @param identification Identificacion del usuario
	 * @throws OtpException
	 */
	void unlockOtp(String identification) throws OtpException;

}
