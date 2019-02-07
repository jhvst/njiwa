/*
 * Kasuku - Open Source eUICC Remote Subscription Management Server
 * 
 * 
 * Copyright (C) 2019 - , Digital Solutions Ltd. - http://www.dsmagic.com
 *
 * Paul Bagyenda <bagyenda@dsmagic.com>
 * 
 * This program is free software, distributed under the terms of
 * the GNU General Public License.
 */ 

package io.njiwa.common;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @brief This is the  configurations handler class. All configurations are loaded once at startup.
 */
public class Properties {

    public static final String BASE_DEPLOYMENT_URI = "base_deployment_uri";
    // Conf var names
    private static final String MYHOSTNAME = "myhostname";
    private static final String MYPORT = "myport";
    private static final String MAXIMUM_BATCH_QUEUE_SIZE = "maximum_batch_queue_size";
    private static final String MAX_THREADS = "max_threads";

    private static final String QUEUE_RUN_INTERVAL = "queue_run_interval";
    private static final String REDIS_SERVER_HOST = "redis_server_host";
    private static final String REDIS_SERVER_PORT = "redis_server_port";

    private static final String COUNTRY_CODE = "country_code";
    private static final String NETWORK_CODES = "network_codes";
    private static final String NUMBER_LENGTH = "number_length";
    private static final String DEFAULT_OTA_SPI_1 = "default_ota_spi1";
    private static final String DEFAULT_OTA_SPI_2 = "default_ota_spi2";
    private static final String MAXIMUM_RETRIES = "maximum_retries";
    private static final String MAXIMUM_SMS_RETRIES = "maximum_sms_retries";
    private static final String GEOMETRIC_BACKOFF = "geometric_backoff";
    private static final String RETRY_INTERVAL = "retry_interval";
    private static final String CASCADE_FAIL_TRANSACTIONS = "cascade_fail_transactions";
    private static final String EXPIRED_TRANSACTION_SMS = "expired_transaction_sms";

    private static final String ALWAYS_USE_DLR = "always_use_dlr";
    private static final String SMS_THROUGHPUT = "sms_throughput";
    private static final String USE_SSL = "use_ssl";
    private static final String SENDSMS_URL = "sendsmsUrl";
    private static final String VIRTUAL_SMSC_PORT = "virtual_smsc_port";
    private static final String VIRTUAL_SMSC_NUMBER = "virtual_smsc_number";
    private static final String VIRTUAL_SMSC_NUMBER_PREFIX = "virtual_smsc_number_prefix";
    private static final String VIRTUAL_SMSC_SHORTCODES = "virtual_smsc_shortcodes";
    private static final String SMS_THROUGHPUT1 = "sms_throughput";
    private static final String BIP_APN = "bip_apn";
    private static final String BIP_TITLE = "bip_title";
    private static final String BIP_ME_BUFFER = "bip_me_buffer";
    private static final String BIP_PORT = "bip_port";
    private static final String BIP_NETWORK_INTERFACE = "bip_network_interface";
    private static final String MAX_BIP_SEND_QUEUE = "max_bip_send_queue";
    private static final String MAX_BIP_DATA_FLAG_CACHE_INTERVAL = "max_bip_data_flag_cache_interval";
    private static final String HLR_GATEWAY_COMMAND = "hlr_gateway_command";
    private static final String IMSI_LOOKUP_COMMAND = "imsi_lookup_command";
    private static final String MAX_BIP_SEND_REQUESTS = "max_bip_send_requests";
    private static final String BIP_IDLE_TIMEOUT = "bip_idle_timeout";
    private static final String BIP_PUSH_RETRY_TIMEOUT = "bip_push_retry_timeout";
    private static final String MINIMUM_BIP_TRANSACTIONS = "minimum_bip_transactions";
    private static final String ALLOW_MULTIPLE_SAT_SESSIONS = "allow_multi";
    private static final String RAMHTTP_NUM_RETRIES = "ram_num_retries";
    private static final String RAM_OPEN_CHANNEL_RETRIES = "ram_open_channel_retries";
    private static final String RAMHTTP_ADMIN_PORT = "ram_admin_port";
    private static final String RAM_ADMIN_BACKLOG = "ram_admin_backlog";
    private static final String RAM_ADMIN_HTTP_KEEP_ALIVE_TIMEOUT = "ram_admin_http_keep_alive_timeout";
    private static final String RAM_ADMIN_MAX_HTTP_REQUESTS_PER_SESSION = "ram_admin_max_http_requests_per_session";
    private static final String RAM_RETRY_TIMEOUT = "ram_retry_timeout";
    private static final String RAM_MAX_SEND_REQUESTS = "ram_max_send_requests";
    private static final String RAM_IDLE_TIMEOUT = "ram_idle_timeout";
    private static final String RAM_USE_DEFAULT_CONFIG = "ram_use_default_config";
    private static final String RAM_POLLING_URI = "ram_polling_uri";
    private static final String MAX_EVENTS_HOURS = "max_events_hours";

    private static final String STATS_INTERVALS = "stats_intervals";

    private static final String PFILE = "njiwa.settings"; // The config file
    // private static final String SERVER_PRIVATE_KEY_ALIAS = "private_key-alias";

    // This stores all the config params, with their validators and current values
    private static Map<String, BaseValidator> configValidators = new ConcurrentHashMap<String, BaseValidator>() {
        {
            put(MYHOSTNAME, new BaseValidator("localhost"));
            put(MYPORT, new IntegerValuesValidator(8080));
            put(MAXIMUM_BATCH_QUEUE_SIZE, new IntegerValuesValidator(10));
            put(MAX_THREADS, new IntegerValuesValidator(1));

            put(QUEUE_RUN_INTERVAL, new RealValuesValidator(10)); // In seconds
            put(REDIS_SERVER_HOST, new BaseValidator("localhost"));
            put(REDIS_SERVER_PORT, new IntegerValuesValidator(6379));


            put(COUNTRY_CODE, new IntegerValuesValidator(86) {
                @Override
                Object value(Object val) throws Exception {
                    super.value(val); // Check it.
                    return val.toString(); // Then return it as a string.

                }
            });

            put(NETWORK_CODES, new StringListValidator(new String[]{"1",},
                    new IntegerValuesValidator(0)));

            put(NUMBER_LENGTH, new IntegerValuesValidator(12));

            put(DEFAULT_OTA_SPI_1, new IntegerValuesValidator(0x16)); // See Secion 2.4.3 of SGP-02-3-0
            put(DEFAULT_OTA_SPI_2, new IntegerValuesValidator(0x39));

            put(MAXIMUM_RETRIES, new IntegerValuesValidator(10));
            put(MAXIMUM_SMS_RETRIES, new IntegerValuesValidator(10));

            put(GEOMETRIC_BACKOFF, new BooleanValidator(false));


            put(RETRY_INTERVAL, new PositiveIntegerValuesValidator(3 * 60));
            put(CASCADE_FAIL_TRANSACTIONS, new BooleanValidator(false));

            put(EXPIRED_TRANSACTION_SMS, new BaseValidator(null));


            put(ALWAYS_USE_DLR, new BooleanValidator(false));

            put(SMS_THROUGHPUT, new IntegerValuesValidator(10));

            put(USE_SSL, new BooleanValidator(false));

            put(BASE_DEPLOYMENT_URI, new BaseValidator("/dstk"));

            put(SENDSMS_URL, new BaseValidator("http://localhost:13013/cgi-bin/sendsms?username=tester&password=foobar"));

            put(VIRTUAL_SMSC_PORT, new IntegerValuesValidator(8182));

            put(VIRTUAL_SMSC_NUMBER, new BaseValidator("1000"));
            put(VIRTUAL_SMSC_NUMBER_PREFIX, new BaseValidator("8000"));

            put(VIRTUAL_SMSC_SHORTCODES, new StringListValidator(new String[]{"1000", "+256772865416"},
                    new IntegerValuesValidator(0)));

            put(SMS_THROUGHPUT1, new IntegerValuesValidator(10));

            put(BIP_APN, new ByteArrayValidator("internet") {
                        @Override
                        protected byte[] getBytes(String value) throws Exception {
                            String[] xl = value.split("[.]");
                            String out = "";
                            ByteArrayOutputStream os = new ByteArrayOutputStream();
                            for (String s : xl)
                                try {
                                    os.write(s.length());
                                    os.write(s.getBytes("UTF-8"));
                                } catch (Exception ex) {
                                }
                            return os.toByteArray();
                        }
                    }
            );
            put(BIP_TITLE, new BaseValidator("Accept"));

            put(BIP_ME_BUFFER, new IntegerValuesValidator(512));
            put(BIP_PORT, new IntegerValuesValidator(2345));

            String ipAddress;
            try {
                InetAddress IP = InetAddress.getLocalHost();
                ipAddress = IP.getHostAddress();
            } catch (Exception ex) {
                ipAddress = "10.211.55.2";
            }
            put(BIP_NETWORK_INTERFACE, new InetInterfaceValidator(ipAddress));
            put(MAX_BIP_SEND_QUEUE, new PositiveIntegerValuesValidator(100));
            put(MAX_BIP_DATA_FLAG_CACHE_INTERVAL, new PositiveIntegerValuesValidator(3600 * 24));
            put(HLR_GATEWAY_COMMAND, new BaseValidator("/usr/local/bin/hlr_gw.sh"));
            put(IMSI_LOOKUP_COMMAND, new BaseValidator("/usr/local/bin/msisdn_map.sh"));

            put(MAX_BIP_SEND_REQUESTS, new PositiveIntegerValuesValidator(10));
            put(BIP_IDLE_TIMEOUT, new PositiveIntegerValuesValidator(120));
            put(BIP_PUSH_RETRY_TIMEOUT, new PositiveIntegerValuesValidator(60 * 4));
            put(MINIMUM_BIP_TRANSACTIONS, new PositiveIntegerValuesValidator(3));
            put(ALLOW_MULTIPLE_SAT_SESSIONS, new BooleanValidator(true));

            put(RAMHTTP_NUM_RETRIES, new PositiveIntegerValuesValidator(10)); // Default is no retries

            put(RAMHTTP_ADMIN_PORT, new PositiveIntegerValuesValidator(9443));
            put(RAM_ADMIN_BACKLOG, new PositiveIntegerValuesValidator(10));
            put(RAM_ADMIN_HTTP_KEEP_ALIVE_TIMEOUT, new PositiveIntegerValuesValidator(120)); // HTTP Connection considered dead after 120 seconds.
            put(RAM_ADMIN_MAX_HTTP_REQUESTS_PER_SESSION, new PositiveIntegerValuesValidator(100));

            put(RAM_RETRY_TIMEOUT, new PositiveIntegerValuesValidator(120));

            put(RAM_MAX_SEND_REQUESTS, new PositiveIntegerValuesValidator(10));

            put(RAM_IDLE_TIMEOUT, new PositiveIntegerValuesValidator(60)); // Idle after 60 seconds.
            put(RAM_USE_DEFAULT_CONFIG, new BooleanValidator(false));
            put(RAM_POLLING_URI, new BaseValidator("polling"));
            put(RAM_OPEN_CHANNEL_RETRIES, new PositiveIntegerValuesValidator(0));
            put(MAX_EVENTS_HOURS, new PositiveIntegerValuesValidator(1));
            put(STATS_INTERVALS, new PositiveIntegerListValidator(new int[]{5, 30, 60, 3600}));
        }
    };
    private static Map<String, Object> propertyValues = validateProps(loadProps()); // Validate them.


    public static int[] getStatsIntervals() {
        return (int[]) propertyValues.get(STATS_INTERVALS);
    }

    public static int getRamOpenChannelRetries() {
        return (Integer) propertyValues.get(RAM_OPEN_CHANNEL_RETRIES);
    }

    public static String getRamPollingUri() {
        return (String) propertyValues.get(RAM_POLLING_URI);
    }

    public static boolean getRamUseDefaultConfig() {
        return (Boolean) propertyValues.get(RAM_USE_DEFAULT_CONFIG);
    }

    public static int getRamPushRetryTimeOut() {
        return (Integer) propertyValues.get(RAM_RETRY_TIMEOUT);
    }

    public static int getRamMaxSendRequests() {
        return (Integer) propertyValues.get(RAM_MAX_SEND_REQUESTS);
    }

    public static int getNumThreads() {
        return (Integer) propertyValues.get(MAX_THREADS);
    }

    public static double getQueueRunInterval() {
        return (Double) propertyValues.get(QUEUE_RUN_INTERVAL);
    }

    public static String getRedis_server() {
        return (String) propertyValues.get(REDIS_SERVER_HOST);
    }

    public static int getRedis_port() {
        return (Integer) propertyValues.get(REDIS_SERVER_PORT);
    }


    public static String getCountry_code() {
        return (String) propertyValues.get(COUNTRY_CODE);
    }

    public static String[] getNetwork_codes() {
        return (String[]) propertyValues.get(NETWORK_CODES);
    }

    public static int getNumber_length() {
        return (Integer) propertyValues.get(NUMBER_LENGTH);
    }

    public static int getDefault_ota_spi1() {
        return (Integer) propertyValues.get(DEFAULT_OTA_SPI_1);
    }

    public static int getDefault_ota_spi2() {
        return (Integer) propertyValues.get(DEFAULT_OTA_SPI_2);
    }

    public static int getMaxRetries() {
        return (Integer) propertyValues.get(MAXIMUM_RETRIES);
    }

    public static boolean isGeometricBackOff() {
        return (Boolean) propertyValues.get(GEOMETRIC_BACKOFF);
    }

    public static int getRetryInterval() {
        return (Integer) propertyValues.get(RETRY_INTERVAL);
    }

    public static boolean isAlwaysUseDlr() {
        return (Boolean) propertyValues.get(ALWAYS_USE_DLR);
    }

    public static int getSmsThroughput() {
        return (Integer) propertyValues.get(SMS_THROUGHPUT);
    }

    public static String getMyhostname() {
        return (String) propertyValues.get(MYHOSTNAME);
    }

    public static int getMyport() {
        return (Integer) propertyValues.get(MYPORT);
    }

    public static boolean isUseSSL() {
        return (Boolean) propertyValues.get(USE_SSL);
    }

    public static String getDlrUri() {
        // return (String) propertyValues.get(DLR_URI);
        return propertyValues.get(BASE_DEPLOYMENT_URI) + "/" + Constants.DLR_URI;
    }


    public static String getSendSmsUrl() {
        return (String) propertyValues.get(SENDSMS_URL);
    }

    public static int getVsmscPort() {
        return (Integer) propertyValues.get(VIRTUAL_SMSC_PORT);
    }

    public static String getVsmsc_number() {
        return (String) propertyValues.get(VIRTUAL_SMSC_NUMBER);
    }

    public static String getVsmscnumberPrefix() {
        return (String) propertyValues.get(VIRTUAL_SMSC_NUMBER_PREFIX);
    }

    public static byte[] getBip_apn() {
        return (byte[]) propertyValues.get(BIP_APN);
    }

    public static String getBip_title() {
        return (String) propertyValues.get(BIP_TITLE);
    }

    public static int getBip_me_buffer_size() {
        return (Integer) propertyValues.get(BIP_ME_BUFFER);
    }

    public static int getCat_tp_port() {
        return (Integer) propertyValues.get(BIP_PORT);
    }

    public static byte[] getBip_network_interface() {
        Object xv = propertyValues.get(BIP_NETWORK_INTERFACE);
        return (byte[]) xv;
    }

    public static int getMax_bip_send_queue() {
        return (Integer) propertyValues.get(MAX_BIP_SEND_QUEUE);
    }

    public static long getMax_bip_data_flag_cache_interval() {
        return (Integer) propertyValues.get(MAX_BIP_DATA_FLAG_CACHE_INTERVAL);
    }

    public static String getHlr_gateway_command() {
        return (String) propertyValues.get(HLR_GATEWAY_COMMAND);
    }

    public static int getMax_bip_send_requests() {
        return (Integer) propertyValues.get(MAX_BIP_SEND_REQUESTS);
    }

    public static int getBip_idle_timeout() {
        return (Integer) propertyValues.get(BIP_IDLE_TIMEOUT);
    }

    public static long getBip_push_retry_timeout() {
        return (Integer) propertyValues.get(BIP_PUSH_RETRY_TIMEOUT);
    }

    public static int getScWsNumberOfRetries() {
        return (Integer) propertyValues.get(RAMHTTP_NUM_RETRIES);
    }

    /**
     * @brief Get the HTTP TCP Port number
     * @return
     */
    public static int getRamhttpAdminPort() {
        return (Integer) propertyValues.get(RAMHTTP_ADMIN_PORT);
    }

    /**
     * @brief Get the HTTP Port backlog
     * @return
     */
    public static int getRamAdminBackLog() {
        return (Integer) propertyValues.get(RAM_ADMIN_BACKLOG);
    }

    public static int getMaxEventsHours() {
        return (Integer) propertyValues.get(MAX_EVENTS_HOURS);
    }

    /**
     * @brief Get the Keep Alive Timeout
     * @return
     */
    public static int getRAMAdminHttpKeepAliveTimeOut() {
        return (Integer) propertyValues.get(RAM_ADMIN_HTTP_KEEP_ALIVE_TIMEOUT);
    }

    public static int getRAMAdminHttpMaxRequests() {
        return (Integer) propertyValues.get(RAM_ADMIN_MAX_HTTP_REQUESTS_PER_SESSION);
    }

    private static java.util.Properties loadProps() {
        java.util.Properties p = new java.util.Properties();
        // ClassLoader loader = Properties.class.getClassLoader();

        try {
            InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(PFILE);
            p.load(in);
            Utils.lg.info(String.format("%s configs loaded", Constants.serverName));
            return p;
        } catch (Exception ex) {
            Utils.lg.error(String.format("Failed to load application properties: %s", ex));
        }
        return null;
    }

    private static Map<String, Object> validateProps(java.util.Properties p) {

        Set<Object> keys = p == null ? new HashSet<Object>() : p.keySet();

        Map<String, Object> vals = new ConcurrentHashMap<String, Object>();
        for (Object k : keys)
            try {
                BaseValidator validator = configValidators.get(k);
                Object v = p.get(k);

                Object nvalue = validator.value(v.toString());

                if (nvalue != null)
                    vals.put(k.toString(), nvalue);
            } catch (Exception ex) {
            }

        // Then deal with those that were not given.
        Set<String> vkeys = configValidators.keySet();
        for (String k : vkeys)

            if (!keys.contains(k))
                try {
                    BaseValidator validator = configValidators.get(k);
                    Object xvalue = validator.getDefault();
                    Object nvalue = validator.value(xvalue);
                    if (nvalue != null)
                        vals.put(k, nvalue);
                } catch (Exception ex) {
                    Utils.lg.error(String.format("Properties Load: Error validating [%s]: %s", k, ex));
                }

        return vals;
    }


    /**
     * The basic validator of a configuration parameter.
     */
    private static class BaseValidator {
        /**
         * The default value for a parameter if none is given
         */
        protected Object default_value = null;

        public BaseValidator(Object defaultVal) {
            default_value = defaultVal;
        }

        /**
         *
         * @param val - The value read from the conf file
         * @return - The value, validated and cleaned up
         * @throws Exception - exception is thrown if value is of the wrong type
         */
        Object value(Object val) throws Exception {
            return val;
        }

        /**
         * @brief Get the default value, to be used if none was supplied in the configuration file
         * @return The default value
         */
        final Object getDefault() {
            return default_value;
        }
    }

    /**
     * Validator for Integer property values
     */
    private static class IntegerValuesValidator extends BaseValidator {
        public IntegerValuesValidator(int defaultVal) {
            super(defaultVal);
        }

        @Override
        Object value(Object val) throws Exception {
            if (val instanceof Integer)
                return val;
            else if (val instanceof String)
                return Integer.parseInt(val.toString());

            throw new Exception(String.format("Must be a number [default: %s]", default_value != null ? default_value : "n/a"));
        }
    }

    /**
     * Validator for non-negative Integer property values
     */
    private static class PositiveIntegerValuesValidator extends BaseValidator {
        public PositiveIntegerValuesValidator(int defaultVal) {
            super(defaultVal);
        }

        @Override
        Object value(Object val) throws Exception {
            if (val instanceof Integer)
                return val;
            else if (val instanceof String) {
                int x = Integer.parseInt(val.toString());
                return x < 0 ? default_value : x;
            }
            throw new Exception(String.format("Must be a number [default: %s]", default_value != null ? default_value : "n/a"));
        }
    }

    /**
     * Validator for floating point values
     */
    private static class RealValuesValidator extends BaseValidator {
        public RealValuesValidator(double defaultVal) {
            super(defaultVal);
        }

        @Override
        Object value(Object val) throws Exception {
            if (val instanceof Double)
                return val;
            else if (val instanceof String)
                return Double.parseDouble(val.toString());

            throw new Exception(String.format("Must be a number [default: %s]", default_value != null ? default_value : "n/a"));
        }
    }

    /**
     * IP addresses validator
     */
    private static class InetInterfaceValidator extends BaseValidator {
        public InetInterfaceValidator(String address) {
            super(address);
        }

        @Override
        Object value(Object val) throws Exception {
            if (val instanceof String)
                return InetAddress.getByName(val.toString()).getAddress(); // Return as byte array
            else if (val instanceof InetAddress)
                return ((InetAddress) val).getAddress();

            throw new Exception(String.format("Must be a hostname [default: %s]", default_value != null ? default_value : "n/a"));
        }
    }

    /**
     * Boolean values validator
     */
    private static class BooleanValidator extends BaseValidator {
        public BooleanValidator(boolean value) {
            super(value);
        }

        @Override
        Object value(Object val) throws Exception {
            if (val instanceof Boolean)
                return val;

            else if (val instanceof String)
                return Boolean.parseBoolean(val.toString());
            else if (val instanceof Integer)
                return 0 != (Integer) val;
            throw new Exception(String.format("Must be a boolean value \"true\" or \"false\" [default: %s]", default_value != null ? default_value : "n/a"));
        }
    }

    /**
     * String lists validator
     */
    private static class StringListValidator extends BaseValidator {
        private BaseValidator elementValidator = null;

        public StringListValidator(String[] defaultval, BaseValidator elementValidator) {
            super(defaultval);

            this.elementValidator = elementValidator;
        }

        @Override
        Object value(Object val) throws Exception {
            if (val instanceof String) {
                String[] vals = val.toString().split("[,]");

                if (elementValidator != null)
                    for (String xv : vals)
                        elementValidator.value(xv); // Validate and hope for the best
                return vals;
            } else if (val instanceof String[])
                return val;

            throw new Exception(String.format("Must be a  comma-separated list of strings [default: %s]", default_value != null ? default_value : "n/a"));
        }
    }


    /**
     * +ve integer lists validator
     */
    private static class PositiveIntegerListValidator extends BaseValidator {

        public PositiveIntegerListValidator(int[] defaultval) {
            super(defaultval);
        }

        @Override
        Object value(Object val) throws Exception {
            if (val instanceof String) {
                String[] vals = val.toString().split("[,]");
                List<Integer> l = new ArrayList<>();
                for (String xv : vals)
                    try {
                        int x = Integer.parseInt(xv);
                        if (x <= 0)
                            throw new Exception("expected positive integer");
                        l.add(x);
                    } catch (Exception ex) {
                        throw new Exception("Invalid item [" + xv + "] in +ve integer list: " + ex.getMessage());
                    }
                return l.stream().mapToInt(i -> i).toArray();
            } else if (val instanceof int[])
                return val;

            throw new Exception(String.format("Must be a  comma-separated list of integers [default: %s]", default_value
                    != null ? default_value : "n/a"));
        }
    }

    /**
     * Byte arrays valiator
     */
    private static class ByteArrayValidator extends BaseValidator {
        public ByteArrayValidator(String val) {
            super(val);
        }

        @Override
        Object value(Object val) throws Exception {
            if (val instanceof String)
                return getBytes(val.toString());

            throw new Exception(String.format("Value must be a string, default: %s", default_value != null ? default_value : "n/a"));
        }

        protected byte[] getBytes(String value) throws Exception {
            return value.getBytes("UTF-8");
        }
    }


    /**
     * @brief These are constants inside the system properties. No change to them, naturally.
     */
    public static class Constants {
        public static final String REST_ENDPOINT = "/rest";
        public static final String DLR_URI = "/dlr"; //!< The DLR partial URL
        public static final String version = "1.0";
        public static final String build = "20190207";
        public static final String release = String.format("v%s (Build %s)", version, build);
        public static final String serverName = String.format("eUICC Remote Subscription Management Server %s", release);

        public static final int DEFAULT_VALIDITY = 3600 * 24;

    }
}
/**
 * @}
 */