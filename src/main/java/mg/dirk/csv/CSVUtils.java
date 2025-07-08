package mg.dirk.csv;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.Reader;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import mg.dirk.csv.annotations.SkipDeserialization;
import mg.dirk.csv.annotations.SkipSerialization;
import mg.dirk.reflections.ReflectUtils;

public class CSVUtils {
    public static final CSVFormat.Builder defaultFormatBuilder = CSVFormat.DEFAULT.builder().setDelimiter(";");

    public static <T extends Object> List<String> getClassCSVHeader(Class<T> class1,
            @SuppressWarnings("unchecked") Class<? extends Annotation>... excludedAnnotations) {
        return getClassCSVHeader(class1, null, excludedAnnotations);
    }

    public static <T extends Object> List<String> getClassCSVHeader(Class<T> class1, String prefix,
            @SuppressWarnings("unchecked") Class<? extends Annotation>... excludedAnnotations) {
        boolean isPrefixUnvalid = prefix == null || prefix.trim().isEmpty();
        List<String> names = new ArrayList<>();
        if (class1.isArray() || class1.isInterface()) {
            return names;
        } else {
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
            for (Field field : ReflectUtils.getFieldsWithExcludedAnnotations(class1, excludedAnnotations)) {
                if (ReflectUtils.isSerdable(field.getType())) {
                    names.add(String.format("%s%s", maybePrefix, field.getName()));
                } else {
                    names.addAll(getClassCSVHeader(field.getType(), String.format("%s%s", maybePrefix, field.getName()),
                            excludedAnnotations));
                }
            }
        }
        return names;
    }

    public static <T extends Object> CSVFormat getGetClassCSVFormat(Class<T> class1) {
        throw new UnsupportedOperationException();
    }

    public static <T extends Object> List<String> getObjectValues(T object)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
        List<String> values = new ArrayList<>();
        @SuppressWarnings("unchecked")
        Class<T> objectClass = (Class<T>) object.getClass();
        @SuppressWarnings("unchecked")
        Field[] fields = ReflectUtils.getFieldsWithExcludedAnnotations(objectClass, SkipSerialization.class);

        for (Field field : fields) {
            Object fieldData = ReflectUtils.getFieldGetter(objectClass, field).invoke(object);
            if (ReflectUtils.isSerdable(field.getType())) {
                values.add(ReflectUtils.formatToString(fieldData));
            } else if (!fieldData.getClass().isArray()) {
                values.addAll(getObjectValues(fieldData));
            }
        }
        return values;
    }

    public static <T extends Object> void printObjectToCSVPrinter(CSVPrinter printer, T object)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException,
            IOException {
        printer.printRecord(getObjectValues(object));
    }

    public static <T extends Object> void saveToFile(Iterable<T> objects, String filePath) throws IOException,
            IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
        File file = new File(filePath);
        try (FileWriter writer = new FileWriter(file);) {
            saveToWriter(objects, writer);
        }
    }

    public static <T extends Object> void saveToWriter(Iterable<T> objects, Writer writer) throws IOException,
            IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
        CSVFormat csvFormat = null;
        CSVPrinter csvPrinter = null;
        try (BufferedWriter bufWriter = new BufferedWriter(writer);) {
            boolean isFirstSetup = true;

            for (T t : objects) {
                if (isFirstSetup) {
                    isFirstSetup = false;
                    @SuppressWarnings("unchecked")
                    List<String> headers = getClassCSVHeader(t.getClass(), SkipSerialization.class);
                    csvFormat = defaultFormatBuilder.setHeader(headers.toArray(new String[headers.size()])).get();
                    csvPrinter = new CSVPrinter(bufWriter, csvFormat);
                }
                csvPrinter.printRecord(getObjectValues(t));
            }
            csvPrinter.flush();
        } finally {
            if (csvPrinter != null) {
                csvPrinter.close();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Object> T deserializeCSVRecord(Class<T> class1, CSVRecord record, String prefix)
            throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, InvalidClassException, ParseException {
        T t = class1.getConstructor().newInstance();
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
        for (Field field : ReflectUtils.getFieldsWithExcludedAnnotations(class1, SkipDeserialization.class)) {
            if (ReflectUtils.isSerdable(field.getType())) {
                ReflectUtils.getFieldSetter(class1, field).invoke(t, ReflectUtils
                        .parseString(record.get(String.format("%s%s", maybePrefix, field.getName())), field.getType()));
            } else if (!field.getType().isArray()) {
                ReflectUtils.getFieldSetter(class1, field).invoke(t, deserializeCSVRecord(field.getType(), record,
                        String.format("%s%s", maybePrefix, field.getName())));
            }
        }
        return t;
    }

    public static <T extends Object> T deserializeCSVRecord(Class<T> class1, CSVRecord record)
            throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, InvalidClassException, ParseException {
        return deserializeCSVRecord(class1, record, null);
    }

    public static <T extends Object> List<T> deserializeFile(Class<T> class1, String file)
            throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchMethodException, SecurityException, ParseException {

        try (
                FileReader file_reader = new FileReader(file);) {
            return deserializeFromReader(class1, file_reader);
        }
    }

    public static <T extends Object> List<T> deserializeFromReader(Class<T> class1, Reader reader1)
            throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchMethodException, SecurityException, ParseException {
        List<T> datas = new ArrayList<>();
        defaultFormatBuilder.setHeader();

        try (
                BufferedReader reader = new BufferedReader(reader1);
                CSVParser parser = defaultFormatBuilder.setHeader().setIgnoreEmptyLines(true).get().parse(reader);) {
            for (CSVRecord csvRecord : parser) {
                datas.add(deserializeCSVRecord(class1, csvRecord));
            }
        }
        return datas;
    }
}
