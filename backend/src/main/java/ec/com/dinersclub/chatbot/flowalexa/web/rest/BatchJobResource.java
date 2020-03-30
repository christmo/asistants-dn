package ec.com.dinersclub.chatbot.flowalexa.web.rest;

import com.google.gson.Gson;

import ec.com.dinersclub.chatbot.flowalexa.service.FlowAlexaService;
import ec.com.dinersclub.chatbot.flowalexa.service.batch.CustomJobParameter;
import ec.com.dinersclub.chatbot.flowalexa.service.dto.FlowAlexaPagingDTO;
import ec.com.dinersclub.chatbot.flowalexa.web.rest.vm.FileData;
import lombok.extern.log4j.Log4j2;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
@RestController
@RequestMapping("/api")
public class BatchJobResource {

    private final JobLauncher jobLauncher;

    private final Job job;

    private final JobRepository jobRepository;

    private final FlowAlexaService flowAlexaService;

    private PolicyFactory policy = Sanitizers.FORMATTING.and(Sanitizers.LINKS);

    @Autowired
    private JobRegistry jobRegistry;

    public BatchJobResource(JobLauncher jobLauncher, Job job, JobRepository jobRepository, FlowAlexaService flowAlexaService, JobRegistry jobRegistry) {
        this.jobLauncher = jobLauncher;
        this.job = job;
        this.jobRepository = jobRepository;
        this.flowAlexaService = flowAlexaService;
        this.jobRegistry = jobRegistry;
    }

    @PostMapping(path = "/batch/clientData")
    public ResponseEntity<String> executeDataBatch(@RequestBody FileData fileData) {
        Map<String, Object> request = new HashMap<>();
        HttpHeaders httpHeaders = new HttpHeaders();
        log.info("EL FILE-->" + fileData);
        try {
            flowAlexaService.deleteDataWithJdbcTemplate();
            HashMap parameters = new HashMap();
            parameters.put("fileData", fileData);
            JobParameters jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis()).
                    addParameter("fileData", new CustomJobParameter<>(parameters)).toJobParameters();
            jobLauncher.run(job, jobParameters);
        } catch (Exception e) {
            log.info(e.getMessage());
        }

        return new ResponseEntity<String>("OK", httpHeaders, HttpStatus.OK);
    }

    @GetMapping("/batch/clients")
    public ResponseEntity<String> getBatchData(Pageable pageable) {
        final FlowAlexaPagingDTO clientsPaged = flowAlexaService.getClientsPaged(pageable);
        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<>(validateSpecialCharacters(new Gson().toJson(clientsPaged)), headers, HttpStatus.OK);

    }

    /**
     * Se valida que no existan caracteres especiales.
     *
     * @param input Cadena a validar.
     * @return Cadena sin caracteres especiales.
     */
    private String validateSpecialCharacters(String input) {
        String REGEX = "^[a-zA-Z0-9\\\" \\\"\\\\\\\\s\\\\dá-úÁ-Ú.@,\\\\\\\\'-]{0,200}$";
        Pattern p = Pattern.compile(REGEX);
        Matcher m = p.matcher(input);
        return m.replaceAll("");
    }
}
