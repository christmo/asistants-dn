package ec.com.dinersclub.chatbot.flowalexa.service.batch;

import java.text.NumberFormat;

import org.apache.commons.lang3.StringUtils;

public class CustomNumberEditor extends org.springframework.beans.propertyeditors.CustomNumberEditor {

    public CustomNumberEditor(Class<? extends Number> numberClass, NumberFormat numberFormat, boolean allowEmpty) throws IllegalArgumentException {
        super(numberClass, numberFormat, allowEmpty);
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        super.setAsText(StringUtils.replaceChars(text, ',', '.'));
    }

}
