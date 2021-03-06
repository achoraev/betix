package betix.core.config;

import betix.core.data.AccountInfo;
import betix.core.logger.Logger;
import betix.core.logger.LoggerFactory;
import org.ho.yaml.Yaml;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Configuration {

    private final static Logger logger = LoggerFactory.getLogger(Configuration.class);
    private final static File CONFIG_FILE = new File("config.yml");
    public final static File CONFIG_ACCOUNT_SPECIFIC_FILE = new File("config_account_specific.yml");
    public final static File STAKES_FILE = new File("stakes.yml");

    private final Map<String, Object> config;
    private final File file;

    public static final Configuration configuration = new Configuration();

    public static Configuration getDefaultConfig() {
        return configuration;
    }

    private Configuration() {
        this(CONFIG_FILE);
    }

    public Configuration(File file) {
        this.file = file;

        Map<String, Object> configTemp;
        try {
            configTemp = Yaml.loadType(file, LinkedHashMap.class);
        } catch (Exception e) {
            configTemp = new HashMap<>();
            if (file.equals(CONFIG_FILE)) {
                configTemp.put(ConfigKey.browser.name(), "firefox");
                configTemp.put(ConfigKey.siteUrl.name(), "http://www.788-sb.com");
                configTemp.put(ConfigKey.imageDir.name(), "img");
                configTemp.put(ConfigKey.siteName.name(), "bet365.com");
                configTemp.put(ConfigKey.teamImageDir.name(), "team");
                configTemp.put(ConfigKey.imageExt.name(), ".png");
                logger.error("can't load config", e);

                try {
                    logger.info("Creating default config");
                    Yaml.dump(configTemp, file);
                } catch (FileNotFoundException ee) {
                    logger.warn("Error saving config", ee);
                }
            }
        }
        config = configTemp;
    }

    public AccountInfo getAccountInfo() {
        Object accInfo = getConfig(ConfigKey.accountInfo);
        if (accInfo != null && accInfo instanceof AccountInfo) {
            return (AccountInfo) accInfo;
        } else {
            return new AccountInfo();
        }
    }

    public void addConfig(ConfigKey key, Object value) {
        addConfig(key, value, false);
    }

    public void addConfig(ConfigKey key, Object value, boolean persist) {
        config.put(key.name(), value);

        if (persist) {
            saveConfig();
        }
    }

    public Object getConfig(ConfigKey key) {
        return config.get(key.name());
    }

    public String getConfigAsString(ConfigKey key) {
        return (String) config.get(key.name());
    }

    public Integer getConfigAsInteger(ConfigKey key) {
        return (Integer) config.get(key.name());
    }

    public Double getConfigAsDouble(ConfigKey key) {
        Object value = config.get(key.name());
        if (value instanceof Double) {
            return (Double) config.get(key.name());
        }
        return Double.valueOf(value.toString());
    }

    public Boolean getConfigAsBoolean(ConfigKey key) {
        return (Boolean) config.get(key.name());
    }

    public void saveConfig() {
        try {
            Yaml.dump(config, file);
        } catch (FileNotFoundException e) {
            logger.warn("Error saving config", e);
        }
    }
}
