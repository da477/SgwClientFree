package org.da477.SgwClientFree.utils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * DateTimeHelper class
 * <p>
 * Utility class for handling date and time operations such as formatting and parsing dates.
 *
 * @author da477
 * @version 1.0
 * @since 5/19/24
 */
@Slf4j
@Lazy(false)
@Component
public class DateTimeHelper {

    private static final SimpleDateFormat DATE_HEADER_FORMATTER = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
    private static final DateTimeFormatter MSG_ID_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
    private static final DateTimeFormatter CRE_DT_TM_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ssXXX");
    public static final DateTimeFormatter VIEW_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm:ss");
    public static final String ZONE_NAME = getPropertiesFile("spring.jackson.time-zone");
    public static final ZoneId ZONE_ID = ZoneId.of(ZONE_NAME);
    private static final String CALENDAR_JSON = "BusinessCalendar.json";

    private static final String PROPERTIES_FILE = "application.properties";
    private static Properties properties;

    private static BusinessCalendar businessCalendar;

    public static synchronized Properties getProperties() {
        if (properties == null) {
            properties = new Properties();
            try {
                properties.load(FileHelper.class
                        .getClassLoader()
                        .getResourceAsStream(PROPERTIES_FILE));
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        return properties;
    }

    public static String getPropertiesFile(String key) {
        return getProperties().getProperty(key);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    private static class BusinessCalendar {
        @JsonProperty("State")
        public String state;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        @JsonProperty("UpdatedDateTime")
        public LocalDateTime updatedDateTime;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        @JsonProperty("NotWorkingWeekDays")
        public List<LocalDate> notWorkingWeekDays;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        @JsonProperty("WorkingWeekends")
        public List<LocalDate> workingWeekends;
    }

    @PostConstruct
    private void init() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        Resource resource = new ClassPathResource(CALENDAR_JSON);
        try (InputStream is = resource.getInputStream()) {
            businessCalendar = mapper.readValue(is, BusinessCalendar.class);
            log.info("Data loaded successfully from {}.", CALENDAR_JSON);
        } catch (IOException e) {
            log.error("Failed to load data from {}: {}", CALENDAR_JSON, e.getMessage());
        }
    }


}
