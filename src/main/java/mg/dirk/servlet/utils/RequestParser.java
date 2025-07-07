package mg.dirk.servlet.utils;

import java.io.InvalidClassException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;

import mg.dirk.csv.annotations.SkipDeserialization;
import mg.dirk.reflections.ReflectUtils;

public class RequestParser {
    final HttpServletRequest request;

    public RequestParser(HttpServletRequest request) {
        this.request = request;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public <T extends Object> T getFromParameters(Class<T> toUseClass)
            throws InvalidClassException, InstantiationException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchMethodException, SecurityException, ParseException {
        return this.getFromParameters(toUseClass, null);
    }

    @SuppressWarnings("unchecked")
    public <T extends Object> T getFromParameters(Class<T> toUseClass, String prefix)
            throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, InvalidClassException, ParseException {
        T t = toUseClass.getConstructor().newInstance();
        boolean isPrefixUnvalid = prefix == null || prefix.trim().isEmpty();
        String maybePrefix;
        if (!isPrefixUnvalid) {
            if (prefix.endsWith(".")) {
                maybePrefix = prefix;
            } else {
                maybePrefix = String.format("%s.", prefix);
            }
        } else {
            maybePrefix = "";
        }
        for (Field field : ReflectUtils.getFieldsWithExcludedAnnotations(toUseClass, SkipDeserialization.class)) {
            Class<?> fieldType = field.getType();
            if (fieldType.equals(String[].class)) {
                ReflectUtils.getFieldSetter(toUseClass, field).invoke(t,
                        (Object) request.getParameterValues(String.format("%s%s", maybePrefix, field.getName())));
            } else if (ReflectUtils.isSerdable(fieldType)) {
                ReflectUtils.getFieldSetter(toUseClass, field).invoke(t,
                        (ReflectUtils.parseString(
                                request.getParameter(String.format("%s%s", maybePrefix, field.getName())),
                                field.getType())));
            } else if (!fieldType.isInterface() && !fieldType.isArray()) {
                ReflectUtils.getFieldGetter(toUseClass, field).invoke(t,
                        this.getFromParameters(field.getType(), String.format("%s%s", maybePrefix)));
            }
        }
        return t;
    }
}
