package ec.com.dinersclub.chatbot.flowalexa.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;

import ec.com.dinersclub.chatbot.flowalexa.service.FlowAlexaService;
import ec.com.dinersclub.chatbot.flowalexa.service.batch.CustomJobParameter;
import ec.com.dinersclub.chatbot.flowalexa.web.rest.vm.FileData;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Getter
@Configuration
@EnableAsync
public class AWSConfiguration {

	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	private Job job;

	@Autowired
	private FlowAlexaService flowAlexaService;

	@Value("${application.aws-sqs-interactions-fifo-name}")
	private String sqsInteractionsFifoName;

	@Value("${application.user-s3-key}")
	private String accessKey;

	@Value("${application.user-s3-password}")
	private String secretKey;

	@Value("${application.user-s3-bucket-name}")
	private String bucketName;

	@Value("${application.user-s3-bucket-key}")
	private String bucketKey;

	@Value("${application.user-s3-bucket-region}")
	private String bucketRegion;

	@Value("${application.flow-type-id}")
	private String flowTypeId;

	private Date dateLastMOdified;

	/**
	 * Method to get credentials. This impl. reads config credentials file in
	 * ~/.aws/credentials/
	 *
	 * @return ProfileCredentialsProvider
	 * @author Frank Rosales
	 */
	public ProfileCredentialsProvider credentialsProvider() {
		ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider("default");
		credentialsProvider.getCredentials();
		return credentialsProvider;
	}

	/**
	 * Method to create a client to connect sqs service on Regions.US_WEST_2
	 *
	 * @return AmazonSQS
	 * @author Frank Rosales
	 */
	@Bean
	public AmazonSQS sqs() {
		return AmazonSQSClientBuilder.standard().withCredentials(this.credentialsProvider())
				.withRegion(Regions.US_WEST_2).build();
	}

	public BasicAWSCredentials basicAWSCredentials() {
		return new BasicAWSCredentials(accessKey, secretKey);
	}

	public AmazonS3 amazonS3Client() {
		AmazonS3 amazonS3Client = AmazonS3ClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials())).withRegion(bucketRegion)
				.build();
		return amazonS3Client;
	}

	@Scheduled(cron = "0 0/1 * * * ?")
	public void verifyChangedFile() {
		log.info("******* Verificando archivo modificado *******");
		S3Object objectS3 = amazonS3Client().getObject(new GetObjectRequest(bucketName, bucketKey));
		if (objectS3 != null) {
			Date newDate = objectS3.getObjectMetadata().getLastModified();
			if (dateLastMOdified != null && objectS3 != null) {
				if (dateLastMOdified.before(newDate)) {
					log.info("******* Archivo modificado {} *******", dateLastMOdified);
					dateLastMOdified = newDate;
					readFileAWS();
				} else {
					log.info("******* Archivo no modificado *******");
				}
			} else {
				log.info("******* Cargando archivo por primera vez *******");
				dateLastMOdified = newDate;
				readFileAWS();
			}
		} else {
			log.info("******* Archivo no encontrado *******");
		}
	}

	private void readFileAWS() {
		InputStream fileInputStream = null;
		try {
			log.info("******* Leyendo archivo *******");
			TransferManager transferManager = TransferManagerBuilder.standard().withS3Client(amazonS3Client()).build();
			File file = new File("./temporal_file.csv");
			Download multipleFileDownload = transferManager.download(bucketName, bucketKey, file);
			while (multipleFileDownload.isDone() == false) {
				Thread.sleep(1000);
				log.info("Descargando !!! {}  {}", multipleFileDownload.getProgress().getBytesTransferred(),
						multipleFileDownload.getState());
			}
			log.info("******* inicializando read_buf *******");
			fileInputStream = new FileInputStream(file);
			byte[] data = new byte[fileInputStream.available()];
			log.info("byte array size {}", data.length);
			/* No borrar este while que sirve para escribir los bytes en el bytearray */
			while (fileInputStream.read(data) != -1) {
				log.info("******* Escribiendo bytes *******");
			}
			log.info("******* termino de escribir bits *******");
			log.info("byte array size {}", data.length);
			log.info("******* termino de escribir bits *******");
			FileData fileData = new FileData();
			fileData.setFlowTypeId(Long.parseLong(flowTypeId));
			fileData.setFile(data);
			flowAlexaService.deleteDataWithJdbcTemplate();
			HashMap parameters = new HashMap();
			parameters.put("fileData", fileData);
			JobParameters jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis())
					.addParameter("fileData", new CustomJobParameter<>(parameters)).toJobParameters();
			jobLauncher.run(job, jobParameters);
			file.delete();
			fileInputStream.close();
		} catch (AmazonServiceException e) {
			log.error("Amazon Service Exception {}", e.getErrorMessage());
		} catch (FileNotFoundException e) {
			log.error("FileNotFoundException {}", e.getMessage());
		} catch (InterruptedException e) {
			log.error("InterruptedException {}", e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected {}", e.getMessage());
		}
	}
}
