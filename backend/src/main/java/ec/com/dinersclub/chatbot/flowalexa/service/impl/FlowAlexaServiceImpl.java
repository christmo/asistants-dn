package ec.com.dinersclub.chatbot.flowalexa.service.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;

import ec.com.diners.chatbot.nlu.domain.MessageResponse;
import ec.com.diners.chatbot.nlu.domain.Output;
import ec.com.diners.chatbot.nlu.exception.ChatbotNluException;
import ec.com.dinersclub.chatbot.flowalexa.beans.FlowAlexaSession;
import ec.com.dinersclub.chatbot.flowalexa.domain.FlowAlexa;
import ec.com.dinersclub.chatbot.flowalexa.domain.FlowDeviceMappingId;
import ec.com.dinersclub.chatbot.flowalexa.domain.PromocionDiners;
import ec.com.dinersclub.chatbot.flowalexa.enums.ContextVarServices;
import ec.com.dinersclub.chatbot.flowalexa.enums.ContextVarServicesConsumedEnum;
import ec.com.dinersclub.chatbot.flowalexa.enums.DeviceRegisterStateEnum;
import ec.com.dinersclub.chatbot.flowalexa.enums.TipoPromocionEnum;
import ec.com.dinersclub.chatbot.flowalexa.exception.OtpException;
import ec.com.dinersclub.chatbot.flowalexa.exception.RegisterException;
import ec.com.dinersclub.chatbot.flowalexa.exception.ServiceNoDataException;
import ec.com.dinersclub.chatbot.flowalexa.exception.StateNotFoundException;
import ec.com.dinersclub.chatbot.flowalexa.repository.FlowAlexaRepository;
import ec.com.dinersclub.chatbot.flowalexa.repository.FlowDeviceMappingIdRepository;
import ec.com.dinersclub.chatbot.flowalexa.repository.PromocionDinersRepository;
import ec.com.dinersclub.chatbot.flowalexa.service.FlowAlexaService;
import ec.com.dinersclub.chatbot.flowalexa.service.FlowDeviceMappingService;
import ec.com.dinersclub.chatbot.flowalexa.service.FlowOtpService;
import ec.com.dinersclub.chatbot.flowalexa.service.HttpService;
import ec.com.dinersclub.chatbot.flowalexa.service.MailService;
import ec.com.dinersclub.chatbot.flowalexa.service.dto.FlowAlexaDTO;
import ec.com.dinersclub.chatbot.flowalexa.service.dto.FlowAlexaPagingDTO;
import ec.com.dinersclub.chatbot.flowalexa.service.dto.FlowStatus;
import ec.com.dinersclub.chatbot.flowalexa.service.dto.RegisterDTO;
import ec.com.dinersclub.chatbot.flowalexa.service.mapper.FlowAlexaMapper;
import ec.com.dinersclub.chatbot.flowalexa.web.rest.vm.CastUtils;
import ec.com.dinersclub.chatbot.flowalexa.web.rest.vm.consultaConsolidado.rq.ConsultaConsolidadoRQ;
import ec.com.dinersclub.chatbot.flowalexa.web.rest.vm.consultaConsolidado.rq.FlujoConsultaConsolidado;
import ec.com.dinersclub.chatbot.flowalexa.web.rest.vm.genericHeaderService.DinHeader;
import ec.com.dinersclub.chatbot.flowalexa.web.rest.vm.userDataService.rq.DinBody;
import ec.com.dinersclub.chatbot.flowalexa.web.rest.vm.userDataService.rq.SerExtConsultarDatosGPSRequest;
import ec.com.dinersclub.chatbot.flowalexa.web.rest.vm.userDataService.rq.UserDataRQ;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@Transactional
public class FlowAlexaServiceImpl extends HttpService implements FlowAlexaService {

	@Autowired
	private FlowAlexaRepository flowAlexaRepository;
	@Autowired
	private FlowAlexaMapper flowAlexaMapper;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private FlowOtpService flowOtpService;
	@Autowired
	private FlowDeviceMappingIdRepository flowDeviceMappingIdRepository;
	@Autowired
	private FlowDeviceMappingService flowDeviceMappingService;
	@Autowired
	private WatsonNLUImpl watsonImpl;
	@Autowired
	private FlowAlexaSession flowAlexaSession;
	@Autowired
	private PromocionDinersRepository promocionDinersRepository;
	@Autowired
	private MailService mailService;

	@Value("${application.user-data-backend-url}")
	private String userDataBackendUrl;

	@Value("${application.consulta-consolidado-backend-url}")
	private String consultaConsolidadoBackendUrl;

	@Override
	public FlowAlexaDTO getFlowByClientIdentification(String identification) {
		FlowAlexa flowAlexa = flowAlexaRepository.findByIdentification(identification);
		if (flowAlexa == null) {
			throw new EntityNotFoundException("No existe el cliente.");
		}
		return flowAlexaMapper.toDto(flowAlexa);
	}

	@Override
	public FlowAlexaDTO getFlowByToken(String token) {
		FlowAlexa flowAlexa = flowAlexaRepository.getOne(token);
		return flowAlexaMapper.toDto(flowAlexa);
	}

	@Override
	public FlowAlexaDTO save(FlowAlexaDTO flowAlexaDTO) {
		FlowAlexa flowAlexa = flowAlexaMapper.toEntity(flowAlexaDTO);
		flowAlexa = flowAlexaRepository.save(flowAlexa);
		return flowAlexaMapper.toDto(flowAlexa);
	}

	@Override
	@Transactional
	public FlowAlexaDTO saveById(String tokenId) {
		FlowAlexa flowAlexa = flowAlexaRepository.getOne(tokenId);
		flowAlexa.setStatus(FlowStatus.FINISH.getStatus());
		FlowAlexaDTO flowAlexaDTO = flowAlexaMapper.toDto(flowAlexa);
		return this.save(flowAlexaDTO);
	}

	@Override
	public void deleteDataWithJdbcTemplate() {
		jdbcTemplate.update("DELETE FROM promocion_diners");
	}

	@Override
	public Page<FlowAlexaDTO> getAllClients(Pageable pageable) {
		return flowAlexaRepository.findAll(pageable).map(flowAlexaMapper::toDto);
	}

	@Override
	public FlowAlexaPagingDTO getClientsPaged(Pageable pageable) {
		final Page<FlowAlexaDTO> page = this.getAllClients(pageable);
		FlowAlexaPagingDTO repo = new FlowAlexaPagingDTO();
		repo.setRows(page.getContent().stream().collect(Collectors.toSet()));
		repo.setTotal(page.getTotalElements());
		repo.setNextPage(page.getTotalPages());
		return repo;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getUserData(String identification) throws ServiceNoDataException {
		log.info("Obtiene data de cliente.");
		Map<String, Object> context = new HashMap<String, Object>();
		DinHeader dinHeader = new DinHeader();
		dinHeader.setNombreServicio("UserData");
		SerExtConsultarDatosGPSRequest consultaDatos = new SerExtConsultarDatosGPSRequest();
		DinBody dinBody = new DinBody();
		dinBody.setIdentificacion(identification);
		consultaDatos.setDinBody(dinBody);
		UserDataRQ userDataRQ = new UserDataRQ(dinHeader, consultaDatos);
		super.setBody(new Gson().toJson(userDataRQ));
		log.info("Body servicio consultas: ");
		log.info(new Gson().toJson(userDataRQ));
		ResponseEntity<Map> response = restTemplate.exchange(userDataBackendUrl, HttpMethod.POST, getHttpEntity(),
				Map.class);
		Map<String, Object> userData = CastUtils.convertInstanceOfObject(response.getBody());
		if (userData != null && userData.get("SerExtConsultarDatosGPSResponse") != null
				&& CastUtils.convertInstanceOfObject(
						CastUtils.convertInstanceOfObject(userData.get("SerExtConsultarDatosGPSResponse"), Map.class)
								.get("dinBody")) != null) {
			context.putAll(CastUtils.convertInstanceOfObject(CastUtils
					.convertInstanceOfObject(userData.get("SerExtConsultarDatosGPSResponse"), Map.class).get("dinBody"),
					Map.class));
		} else {
			throw new ServiceNoDataException("No existe informacion del cliente.");
		}
		return context;
	}

	@Override
	public Map<String, Object> getCardData(String identification) throws ServiceNoDataException {
		log.info("Obtiene data de saldos.");
		Map<String, Object> context = new HashMap<String, Object>();
		DinHeader dinHeader = new DinHeader();
		dinHeader.setNombreServicio("IntegracionWSConsultaTarjetas");
		dinHeader.setLlaveSimetrica("");
		dinHeader.getPaginado().setCantRegistros("20");
		dinHeader.getPaginado().setNumPagActual("1");
		FlujoConsultaConsolidado flujoConsulta = new FlujoConsultaConsolidado();
		ec.com.dinersclub.chatbot.flowalexa.web.rest.vm.consultaConsolidado.rq.DinBody dinBody = new ec.com.dinersclub.chatbot.flowalexa.web.rest.vm.consultaConsolidado.rq.DinBody();
		dinBody.setCodFamiliaProducto("TAR");
		dinBody.setNumIdentificacion(identification);
		dinBody.setTipIdentificacion("1");
		flujoConsulta.setDinBody(dinBody);
		ConsultaConsolidadoRQ consultaConsolidado = new ConsultaConsolidadoRQ(dinHeader, flujoConsulta);
		super.setBody(consultaConsolidado);
		ResponseEntity<Map> response = restTemplate.exchange(consultaConsolidadoBackendUrl, HttpMethod.POST,
				getHttpEntity(), Map.class);
		Map<String, Object> saldos = CastUtils.convertInstanceOfObject(response.getBody());
		if (saldos.get("listaTarjetasDeCredito") != null) {
			List tarjetas = CastUtils.convertInstanceOfObject(saldos.get("listaTarjetasDeCredito"), List.class);
			if (tarjetas == null || tarjetas.isEmpty()) {
				throw new ServiceNoDataException("No existe informacion de tarjetas.");
			}
			for (Object tarjeta : tarjetas) {
				context.putAll(CastUtils.convertInstanceOfObject(tarjeta));
			}
		} else {
			throw new ServiceNoDataException("No existe informacion de tarjetas.");
		}
		return context;
	}

	@Override
	public void registerStep1(RegisterDTO registerDTO) throws OtpException, RegisterException, ServiceNoDataException {
		log.info("identificacion alexa: " + registerDTO.getIdentification());
		String identificacion = registerDTO.getIdentification().replaceAll(" ", "");
		FlowDeviceMappingId deviceMapping = flowDeviceMappingService.findRegister(registerDTO.getDeviceId(),
				identificacion);
		if (deviceMapping != null && !deviceMapping.getState().isNoValidado()) {
			throw new RegisterException("El usuario ya se encuentra registrado.");
		}
		log.info("identificación sin espacios: " + identificacion);
		Map<String, Object> response = getUserData(identificacion);
		if (response != null && response.get("identificacion") != null) {
			flowOtpService.enrollmentOtp(identificacion);
			flowOtpService.unlockOtp(identificacion);
			flowOtpService.sendOtp(identificacion, CastUtils.convertInstanceOfObject(response.get("telNotificacion")));
			flowDeviceMappingService.save(new FlowDeviceMappingId(registerDTO.getDeviceId().trim(), identificacion,
					DeviceRegisterStateEnum.PENDIENTE_VALIDACION, registerDTO.getTipo()));
			log.info("Se registra en pendiente de validacion.");
		} else {
			throw new RegisterException("No se ha encontrado el cliente");
		}
	}

	@Override
	public void registerStep2(RegisterDTO registerDTO) throws OtpException, RegisterException {
		Optional<FlowDeviceMappingId> deviceMappingOptional = flowDeviceMappingIdRepository
				.findById(registerDTO.getDeviceId());
		String codigoOtp = registerDTO.getOtp().replaceAll(" ", "");
		FlowDeviceMappingId deviceMapping = deviceMappingOptional.get();
		int numIntentos = deviceMapping.getRemainingAttempts() == 0 ? deviceMapping.getRemainingAttempts() + 1
				: deviceMapping.getRemainingAttempts();
		try {
			flowOtpService.validateOtp(deviceMapping.getIdentification(), codigoOtp);
			deviceMapping.setState(DeviceRegisterStateEnum.VALIDADO);
			deviceMapping.setRemainingAttempts(0);
			deviceMapping.setFechaBloqueo(null);
		} catch (OtpException ex) {
			if (numIntentos == 3) {
				deviceMapping.setState(DeviceRegisterStateEnum.BLOQUEADO);
				deviceMapping.setFechaBloqueo(LocalDate.now());
				throw new RegisterException("Demasiados intentos");
			}
			deviceMapping.setRemainingAttempts(numIntentos + 1);
			throw new OtpException(ex.getMessage());
		} finally {
			flowDeviceMappingIdRepository.save(deviceMapping);
		}
	}

	@Override
	public MessageResponse getWatsonResponse(MessageResponse messageIn)
			throws ChatbotNluException, StateNotFoundException, ServiceNoDataException {
		String originalInput = null;
		messageIn.setContext(isUserRegistered(messageIn));
		if (DeviceRegisterStateEnum
				.getDeviceRegisterState(
						CastUtils.convertInstanceOfObject(messageIn.getContext().get("estado"), String.class))
				.isValidado()) {
			originalInput = CastUtils.convertInstanceOfObject(messageIn.getInput().get("text"));
			if (messageIn.getContext().get(ContextVarServicesConsumedEnum.SRV_TARJETAS.getEtiqueta()) == null) {
				messageIn.getContext().put(ContextVarServicesConsumedEnum.SRV_TARJETAS.getEtiqueta(), false);
			}
			if (messageIn.getContext().get(ContextVarServicesConsumedEnum.SRV_CAMPANAS.getEtiqueta()) == null) {
				messageIn.getContext().put(ContextVarServicesConsumedEnum.SRV_CAMPANAS.getEtiqueta(), false);
			}
			messageIn = watsonImpl.requestAPI(messageIn);
			if (!CastUtils.convertInstanceOfObject(
					messageIn.getContext().get(ContextVarServicesConsumedEnum.SRV_TARJETAS.getEtiqueta()),
					Boolean.class)
					&& messageIn.getContext().get("service") != null
					&& ContextVarServices.valueOf(
							CastUtils.convertInstanceOfObject(messageIn.getContext().get("service"), String.class))
							.isSRV0001()) {
				messageIn.getContext().putAll(getCardData(flowAlexaSession.getDeviceMappingId().getIdentification()));
				messageIn.getContext().put(ContextVarServicesConsumedEnum.SRV_TARJETAS.getEtiqueta(), true);
				log.info("entra en consulta de tarjetas");
			} else if (!CastUtils.convertInstanceOfObject(
					messageIn.getContext().get(ContextVarServicesConsumedEnum.SRV_CAMPANAS.getEtiqueta()),
					Boolean.class)
					&& messageIn.getContext().get("service") != null
					&& ContextVarServices.valueOf(
							CastUtils.convertInstanceOfObject(messageIn.getContext().get("service"), String.class))
							.isSRV0002()) {
				List<PromocionDiners> promo = promocionDinersRepository
						.findByTipoPromocion(TipoPromocionEnum.MODO_TASTY);
				if (promo != null && promo.size() > 0) {
					messageIn.getContext().put("ciudad", promo.get(0).getCiudad());
					messageIn.getContext().put("contactoEstablecimiento", promo.get(0).getContactoEstablecimiento());
					messageIn.getContext().put("direccionEstablecimiento", promo.get(0).getDireccionEstablecimiento());
					messageIn.getContext().put("establecimiento", promo.get(0).getEstablecimiento());
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
					messageIn.getContext().put("fechaDesde", promo.get(0).getFechaDesde().format(formatter));
					messageIn.getContext().put("fechaHasta", promo.get(0).getFechaHasta().format(formatter));
					messageIn.getContext().put("promocion", promo.get(0).getPromocion());
					messageIn.getContext().put("tipoPromocion", promo.get(0).getTipoPromocion());
					messageIn.getContext().put(ContextVarServicesConsumedEnum.SRV_CAMPANAS.getEtiqueta(), true);
					if (CastUtils.convertInstanceOfObject(messageIn.getContext().get("sendMail"), Boolean.class)) {
						mailService.sendPromocionModoTasty("cfmora@dinersclub.com.ec");
					}
				} else {
					throw new ServiceNoDataException("Al momento no existen promociones vigentes.");
				}
				log.info("entra en flujo modo tasty");
			}
		}
		if (messageIn.getInput() == null) {
			messageIn.setInput(new HashMap<String, Object>());
			messageIn.getInput().put("text", originalInput);
		}
		flowAlexaSession.setContext(messageIn.getContext());
		messageIn = watsonImpl.requestAPI(messageIn);
		messageIn = killSession(messageIn);
		return messageIn;
	}

	/**
	 * @param messageIn
	 * @return
	 */
	private MessageResponse killSession(MessageResponse messageIn) {
		if (messageIn.getContext().get("stayAlive") != null
				&& CastUtils.convertInstanceOfObject(messageIn.getContext().get("stayAlive")).equals("false")) {
			messageIn.getContext().clear();
			flowAlexaSession.setContext(null);
		}
		return messageIn;
	}

	/**
	 * @param messageIn
	 * @return
	 * @throws ServiceNoDataException
	 */
	private Map<String, Object> isUserRegistered(MessageResponse messageIn) throws ServiceNoDataException {
		String deviceId = CastUtils.convertInstanceOfObject(messageIn.getContext().get("device_id"));
		final Map<String, Object> context = flowAlexaSession.getContext();
		context.putAll(messageIn.getContext());
		Optional<FlowDeviceMappingId> device = flowDeviceMappingIdRepository.findById(deviceId);
		if (!device.isPresent()) {
			context.put("estado", DeviceRegisterStateEnum.NO_VALIDADO.getEtiqueta());
		} else {
			context.put("estado", device.get().getState().getEtiqueta());
			flowAlexaSession.setDeviceMappingId(device.get());
			context.putAll(getUserData(device.get().getIdentification()));
			log.info("entra en consulta de cliente");
		}
		return context;
	}

	@Override
	public MessageResponse unlockUser(RegisterDTO registerDTO)
			throws OtpException, RegisterException, ServiceNoDataException {
		MessageResponse messageResponse = new MessageResponse();
		messageResponse.setOutput(new Output());
		messageResponse.getOutput().setText(new ArrayList<>());
		Optional<FlowDeviceMappingId> deviceMappingOptional = flowDeviceMappingIdRepository
				.findById(registerDTO.getDeviceId());
		if (deviceMappingOptional.isPresent()) {
			FlowDeviceMappingId deviceMapping = deviceMappingOptional.get();
			if (deviceMapping.getState().isBloqueado()) {
				flowOtpService.unlockOtp(deviceMapping.getIdentification());
				deviceMapping.setState(DeviceRegisterStateEnum.PENDIENTE_VALIDACION);
				deviceMapping.setFechaBloqueo(null);
				deviceMapping.setRemainingAttempts(0);
				String identificacion = deviceMapping.getIdentification();
				Map<String, Object> response = getUserData(identificacion);
				String numTelefono = CastUtils.convertInstanceOfObject(response.get("telNotificacion"));
				flowOtpService.sendOtp(identificacion, numTelefono);
				flowDeviceMappingIdRepository.save(deviceMapping);
				messageResponse.getOutput().getText().add("Se ha enviado un codigo al número de teléfono terminado en "
						+ numTelefono.substring(6, numTelefono.length()));
			} else {
				throw new RegisterException("El usuario no esta bloqueado");
			}
		} else {
			throw new EntityNotFoundException("Cliente no registrado");
		}
		return messageResponse;
	}
}
