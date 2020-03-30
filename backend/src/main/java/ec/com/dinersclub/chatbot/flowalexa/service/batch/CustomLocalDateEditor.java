package ec.com.dinersclub.chatbot.flowalexa.service.batch;

import java.text.DateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.propertyeditors.CustomDateEditor;

import ec.com.dinersclub.chatbot.flowalexa.web.rest.vm.CastUtils;

public class CustomLocalDateEditor extends CustomDateEditor {
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

	public CustomLocalDateEditor(DateFormat dateFormat, boolean allowEmpty) {
		super(dateFormat, allowEmpty);
	}

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		if (StringUtils.isNotEmpty(text)) {
			setValue(LocalDate.parse(text, formatter));
		} else {
			setValue(null);
		}
	}

	@Override
	public String getAsText() throws IllegalArgumentException {
		Object date = getValue();
		if (date != null) {
			return formatter.format(CastUtils.convertInstanceOfObject(getValue(), LocalDate.class));
		} else {
			return "";
		}
	}
}
