package org.da477.SgwClientFree.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * FileHelper class
 * <p>
 * Utility class for handling file operations such as reading log files and saving responses.
 *
 * @author da477
 * @version 1.0
 * @since 5/19/24
 */
@Component
@Lazy(false)
public class FileHelper {

    public static final int CAPACITY = 100;

    @Value("${response.save.directory}")
    private String DIRECTORY_TO_SAVE_XML;
    private final Environment env;

    @Autowired
    public FileHelper(Environment env) {
        this.env = env;
    }

    public String getProperty(String key) {
        return env.getProperty(key);
    }

}
