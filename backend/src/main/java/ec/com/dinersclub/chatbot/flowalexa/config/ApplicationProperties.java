package ec.com.dinersclub.chatbot.flowalexa.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

	private final Async async = new Async();
	private String timeZone;
	private String backendUrl;
	private String proxyEnabled;
	private String proxyAddress;
	private String proxyPort;
	private String watsonWorkspace;
	private String watsonApiKey;
	private String watsonVersion;
	private String cardServiceBackend;
	private String otpServiceBackend;
	private String flowTypeId;
	private String backendUser;
	private String backendPassword;
	private String intentBackendUrl;
	private String mailerBackendUrl;
	private String bigUrlFrontEnd;
	private String flowName;
	private String userS3;
	private String userS3Key;
	private String userS3Password;
	private String userS3BucketName;
	private String userS3BucketKey;
	private String userS3BucketRegion;
	private String cloudAwsRegionStatic;
	private String userDataServiceBackend;
	private String entriesCountServiceBackend;
	private String registerTransactionServiceBackend;
	private String authorizerTransactionConsumer;
	private String conciliateFileBackend;
	private String awsSqsInteractionsFifoName;
	private String userDataBackendUrl;
	private String consultaConsolidadoBackendUrl;

	public static class Async {

		private int corePoolSize = AsyncI.corePoolSize;

		private int maxPoolSize = AsyncI.maxPoolSize;

		private int queueCapacity = AsyncI.queueCapacity;

		public int getCorePoolSize() {
			return corePoolSize;
		}

		public void setCorePoolSize(int corePoolSize) {
			this.corePoolSize = corePoolSize;
		}

		public int getMaxPoolSize() {
			return maxPoolSize;
		}

		public void setMaxPoolSize(int maxPoolSize) {
			this.maxPoolSize = maxPoolSize;
		}

		public int getQueueCapacity() {
			return queueCapacity;
		}

		public void setQueueCapacity(int queueCapacity) {
			this.queueCapacity = queueCapacity;
		}
	}

	interface AsyncI {
		int corePoolSize = 2;
		int maxPoolSize = 50;
		int queueCapacity = 10000;
	}

}
