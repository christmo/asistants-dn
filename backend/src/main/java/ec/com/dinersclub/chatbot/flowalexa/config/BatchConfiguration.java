package ec.com.dinersclub.chatbot.flowalexa.config;

import java.beans.PropertyEditor;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import ec.com.dinersclub.chatbot.flowalexa.service.batch.ClientItemProcessor;
import ec.com.dinersclub.chatbot.flowalexa.service.batch.CustomLocalDateEditor;
import ec.com.dinersclub.chatbot.flowalexa.service.batch.CustomNumberEditor;
import ec.com.dinersclub.chatbot.flowalexa.service.batch.JobCompletionNotificationListener;
import ec.com.dinersclub.chatbot.flowalexa.service.dto.PromocionDinersDTO;
import ec.com.dinersclub.chatbot.flowalexa.web.rest.vm.CastUtils;
import ec.com.dinersclub.chatbot.flowalexa.web.rest.vm.FileData;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {
	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Bean
	@StepScope
	public FlatFileItemReader<PromocionDinersDTO> reader(
			@Value("#{jobParameters[fileData]}") Map<String, Object> fileData) {

		if (Objects.isNull(fileData)) {
			return null;
		}
		FileData fileData1 = CastUtils.convertInstanceOfObject(fileData.get("fileData"));
		Map<Object, PropertyEditor> customEditors = new HashMap<>();
		customEditors.put(LocalDate.class, new CustomLocalDateEditor(new SimpleDateFormat("yyyyMMdd"), false));
		customEditors.put(BigDecimal.class, new CustomNumberEditor(BigDecimal.class, new DecimalFormat(""), false));
		return new FlatFileItemReaderBuilder<PromocionDinersDTO>().name("personItemReader")
				.resource(new ByteArrayResource(fileData1.getFile())).delimited().delimiter(";")
				.names(new String[] { "promocion", "establecimiento", "direccionEstablecimiento",
						"contactoEstablecimiento", "fechaDesde", "fechaHasta", "tipoPromocion", "ciudad" })
				.linesToSkip(1).fieldSetMapper(new BeanWrapperFieldSetMapper<PromocionDinersDTO>() {
					{
						setTargetType(PromocionDinersDTO.class);
						setCustomEditors(customEditors);
					}
				}).build();
	}

	@Bean
	public ClientItemProcessor processor() {
		return new ClientItemProcessor();
	}

	@Bean
	public JdbcBatchItemWriter<PromocionDinersDTO> writer(DataSource dataSource) {
		return new JdbcBatchItemWriterBuilder<PromocionDinersDTO>()
				.itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
				.sql("INSERT INTO promocion_diners(promocion, establecimiento, direccion_establecimiento, contacto_establecimiento, fecha_desde, "
						+ "fecha_hasta, tipo_promocion, ciudad) "
						+ "VALUES (:promocion, :establecimiento, :direccionEstablecimiento, :contactoEstablecimiento, :fechaDesde, :fechaHasta, :tipoPromocion, :ciudad)")
				.dataSource(dataSource).build();
	}

	@Bean
	public Job importUserJob(JobCompletionNotificationListener listener, Step step1) {
		return jobBuilderFactory.get("importUserJob").incrementer(new RunIdIncrementer()).listener(listener).flow(step1)
				.end().build();
	}

	@Bean
	public Step step1(JdbcBatchItemWriter<PromocionDinersDTO> writer) {
		return stepBuilderFactory.get("step1").<PromocionDinersDTO, PromocionDinersDTO>chunk(5000).reader(reader(null))
				.processor(processor()).writer(writer).taskExecutor(taskExecutor()).build();
	}

	@Bean(name = "batchTaskExecutor")
	public TaskExecutor taskExecutor() {
		SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
		taskExecutor.setConcurrencyLimit(1);
		return taskExecutor;
	}

}
