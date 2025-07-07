package mg.dirk.reflections;

import java.io.InvalidClassException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReflectUtils {
    public static final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");

    public static String makeFirstUpperCase(String input) {
        char first = Character.toUpperCase(input.charAt(0));
        return String.format("%s%s", first, input.substring(1, input.length()));
    }

    public static String getFieldSetterName(Field field) {
        return String.format("set%s", makeFirstUpperCase(field.getName()));
    }

    public static Method getFieldSetter(Class<? extends Object> targetClass, Field field)
            throws NoSuchMethodException, SecurityException {
        return targetClass.getMethod(getFieldSetterName(field), field.getType());
    }

    public static boolean isAnnotationsPresent(Field field,
            @SuppressWarnings("unchecked") Class<? extends Annotation>... excludedAnnotations) {
        for (Class<? extends Annotation> class1 : excludedAnnotations) {
            if (field.isAnnotationPresent(class1)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAnnotationsPresent(Method field,
            @SuppressWarnings("unchecked") Class<? extends Annotation>... excludedAnnotations) {
        for (Class<? extends Annotation> class1 : excludedAnnotations) {
            if (field.isAnnotationPresent(class1)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAnnotationsPresent(Class<? extends Object> field,
            @SuppressWarnings("unchecked") Class<? extends Annotation>... excludedAnnotations) {
        for (Class<? extends Annotation> class1 : excludedAnnotations) {
            if (field.isAnnotationPresent(class1)) {
                return true;
            }
        }
        return false;
    }

    public static Field[] getFieldsWithExcludedAnnotations(Class<? extends Object> targetClass,
            @SuppressWarnings("unchecked") Class<? extends Annotation>... excludedAnnotations) {
        List<Field> names = new ArrayList<>();
        for (Field field : targetClass.getDeclaredFields()) {
            if (isAnnotationsPresent(field, excludedAnnotations)) {
                continue;
            }
            names.add(field);
        }
        return names.toArray(new Field[names.size()]);
    }

    public static String[] getFieldNames(Class<? extends Object> targetClass,
            @SuppressWarnings("unchecked") Class<? extends Annotation>... excludedAnnotations) {
        List<String> names = new ArrayList<>();
        for (Field field : getFieldsWithExcludedAnnotations(targetClass, excludedAnnotations)) {
            names.add(field.getName());
        }
        return names.toArray(new String[names.size()]);
    }

    public static String getFieldGetterName(Field field) {
        return String.format("get%s", makeFirstUpperCase(field.getName()));
    }

    public static Method getFieldGetter(Class<? extends Object> targetClass, Field field)
            throws NoSuchMethodException, SecurityException {
        return targetClass.getMethod(getFieldGetterName(field));
    }

    public static Object[] getFieldValues(Object object,
            @SuppressWarnings("unchecked") Class<? extends Annotation>... excludedAnnotations)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
        List<Object> list = new ArrayList<>();
        Class<? extends Object> obj_class = object.getClass();
        Field[] fields = getFieldsWithExcludedAnnotations(obj_class, excludedAnnotations);

        for (Field field : fields) {
            list.add(formatToString(getFieldGetter(obj_class, field).invoke(object)));
        }
        return list.toArray(new Object[list.size()]);
    }

    public static Object parseString(String value, Class<? extends Object> target)
            throws InvalidClassException, ParseException {
        if (target == String.class) {
            return value;
        } else if (target == Integer.class || target == int.class) {
            return Integer.parseInt(value);
        } else if (target == Float.class || target == float.class) {
            return Float.parseFloat(value);
        } else if (target == Double.class || target == double.class) {
            return Double.parseDouble(value);
        } else if (target == Short.class || target == short.class) {
            return Short.parseShort(value);
        } else if (target == Boolean.class || target == boolean.class) {
            return Boolean.parseBoolean(value);
        } else if (target == Long.class || target == long.class) {
            return Long.parseLong(value);
        } else if (target == Date.class) {
            return dateFormatter.parse(value);
        } else {
            throw new InvalidClassException(String.format("Cannot parse a string to %s", target.getName()));
        }
    }

    public static String formatToString(Object target) {
        if (target instanceof Date) {
            return dateFormatter.format(target);
        } else {
            return String.valueOf(target);
        }
    }

    public static boolean isSerdable(Class<? extends Object> class1) {
        return (class1.isPrimitive() || class1.equals(String.class) || class1.equals(Date.class)
                || class1.equals(Integer.class) || class1.equals(Double.class) || class1.equals(Float.class)
                || class1.equals(Short.class) || class1.equals(Boolean.class) || class1.equals(Long.class));
    }
}
