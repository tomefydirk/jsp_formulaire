package mg.dirk.servlet.utils;

import java.io.InvalidClassException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;

import Affichage.Deroulante;
import mg.dirk.csv.annotations.SkipDeserialization;
import mg.dirk.reflections.ReflectUtils;

public class RequestParser {
    final HttpServletRequest request;

    public static final String deroulanteParseFunc = "deroulanteParseFunc";

    public static Method getDeroulanteParseFunc(Class<? extends Object> toFind)
            throws NoSuchMethodException, SecurityException {
        return toFind.getMethod(deroulanteParseFunc, String.class);
    }

    public static boolean hasDeroulanteParseFunc(Class<? extends Object> toFind) {
        try {
            getDeroulanteParseFunc(toFind);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

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
            if (hasDeroulanteParseFunc(fieldType) && Deroulante.class.isAssignableFrom(fieldType)) {
                Method parseFunc = getDeroulanteParseFunc(fieldType);
                Object fieldObj = fieldType.getConstructor().newInstance();
                parseFunc.invoke(fieldObj, request.getParameter(String.format("%s%s", maybePrefix, field.getName())));
                ReflectUtils.getFieldSetter(toUseClass, field).invoke(t, fieldObj);
            } else if (fieldType.equals(String[].class)) {
                ReflectUtils.getFieldSetter(toUseClass, field).invoke(t,
                        (Object) request.getParameterValues(String.format("%s%s", maybePrefix, field.getName())));
            } else if (ReflectUtils.isSerdable(fieldType)) {
                ReflectUtils.getFieldSetter(toUseClass, field).invoke(t,
                        (ReflectUtils.parseString(
                                request.getParameter(String.format("%s%s", maybePrefix, field.getName())),
                                field.getType())));
            } else if (!fieldType.isInterface() && !fieldType.isArray()) {
                ReflectUtils.getFieldSetter(toUseClass, field).invoke(t,
                        this.getFromParameters(field.getType(), String.format("%s%s", maybePrefix, field.getName())));
            }
        }
        return t;
    }
}
