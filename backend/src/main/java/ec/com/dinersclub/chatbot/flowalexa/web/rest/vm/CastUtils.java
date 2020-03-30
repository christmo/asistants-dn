package ec.com.dinersclub.chatbot.flowalexa.web.rest.vm;

import javax.validation.ValidationException;

import org.springframework.context.annotation.Configuration;

@Configuration
public class CastUtils {

	@SafeVarargs
	@SuppressWarnings("unchecked")
	public static <T> T convertInstanceOfObject(Object o, Class<T>... clazz) {
		try {
			if (clazz != null && clazz.length > 0) {
				return clazz[0].cast(o);
			}
			return (T) o;
		} catch (ClassCastException e) {
			throw new ValidationException("No se puede realizar la conversion.", e);
		}
	}

}
