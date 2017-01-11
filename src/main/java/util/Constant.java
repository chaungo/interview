package util;

public class Constant {

    public static final long EXPIRE_TIME = 24 * 60 * 60 * 1000L;


    public static final int CONNECTION_TIMEOUT = 30 * 1000;
    public static final int CONNECTION_TIMEOUT_FOR_GET_STATISTIC = 10 * 60 * 1000;
    public static final Long EXPIRE_TIME_30DAYS = 30 * 24 * 60 * 60 * 1000L;
    //public static final int MAX_AGE = 30 * 24 * 60 * 60 * 1000;
    //public static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    //public static final String DB_URL = "jdbc:mysql://localhost/newdashboard?useSSL=false";

    public static final String USERNAME_LOGIN_KEY = "os_username";
    public static final String PASSWORD_LOGIN_KEY = "os_password";
    public static final String REMEMBER_LOGIN_KEY = "os_cookie";
    public static final String LOGININFO_INVALID = "anonymous";

    public static final int TIMEOUT = 60 * 1000;

    //
    public static final String LOGIN_LINK = "http://tiger.in.alcatel-lucent.com:8091/login.jsp";
    public static final String LINK_CRUCIBLE = "http://tiger.in.alcatel-lucent.com:8060";
    public static final String LINK_GET_CRU_PROJECTS = "http://tiger.in.alcatel-lucent.com:8060/json/cru/projectFinder.do?limit=99999&q=";
    public static final String LINK_GET_CRU_USERS = "http://tiger.in.alcatel-lucent.com:8060/json/fe/activeUserFinder.do?limit=99999&q=";
    public static final String LINK_GET_ODREVIEW_REPORTS = "http://tiger.in.alcatel-lucent.com:8060/rest-service/reviews-v1/filter/details?creator=&project=%s&states=Review";

    public static final String LINK_GET_JIRA_PERIODS = "http://bamboo.in.alcatel-lucent.com:8085/api/properties?format=json";
    public static final String LINK_GET_SONAR_STATISTIC = "http://bamboo.in.alcatel-lucent.com:8085/api/resources?format=json&metrics=%s&includetrends=true&resource=%s";
    public static final String LINK_GET_JIRA_USER_INFO = "http://tiger.in.alcatel-lucent.com:8091/rest/api/2/user?username=%s&expand=groups";
    public static final String LINK_GET_JIRA_PROJECTS = "http://tiger.in.alcatel-lucent.com:8091/rest/api/2/project";
    public static final String LINK_GET_JIRA_GROUPS = "http://tiger.in.alcatel-lucent.com:8091/rest/api/2/groups/picker?maxResults=10000";
    public static final String LINK_GET_JIRA_USERS_OF_GROUP = "http://tiger.in.alcatel-lucent.com:8091/rest/api/2/group?groupname=%s&expand=users";
    public static final String RESOURCE_BUNLE_HOST = "resourcebundle.host";
    public static final String RESOURCE_BUNLE_PATH = "resourcebundle.execution.path";
    public static final String RESOURCE_BUNLE_HOST_TYPE = "resourcebundle.host.type";
    public static final String RESOURCE_BUNLE_PROXY_IP = "resourcebundle.proxy.ip";
    public static final String RESOURCE_BUNLE_PROXY_PORT = "resourcebundle.proxy.port";
    public static final String RESOURCE_BUNLE_PROJECT_PATH = "resourcebundle.project.path";
    public static final String RESOURCE_BUNLE_LOGIN_PATH = "resourcebundle.login.path";
    public static final String RESOURCE_BUNLE_SEARCH_PATH = "resourcebundle.search.path";
    public static final String RESOURCE_BUNLE_SEARCH_MAXRECORDS = "resourcebundle.search.maxrecords";
    public static final String RESOURCE_BUNLE_SEARCH_MAXRECORDS_DEFAULT = "10000";
    public static final String RESOURCE_BUNLE_ISSUE_PATH = "resourcebundle.issue.path";
    //constant query
    public static final String PARAMERTER_ZQL_QUERY = "zqlQuery";
    public static final String PARAMERTER_JQL_QUERY = "jql";
    public static final String PARAMERTER_MAXRECORDS = "maxRecords";
    public static final String PARAMERTER_MAXRESULTS = "maxResults";
    public static final String PARAMERTER_TIMEOUT = "resourcebundle.project.timeout";
    public static final String PARAMERTER_OFFSET = "offset";
    public static final String AND = " AND ";
    public static final String OR = " OR ";
    public static final String OPEN_BRACKET = "(";
    public static final String CLOSE_BRACKET = ")";
    public static final String MAIN_PROJECT = "FNMS 557x";
    //database
    public static final String DATABASE_SCHEMA = "DATABASE_SCHEMA";
    public static final String DATABASE_HOST = "DATABASE_HOST";
    public static final String DATABASE_PORT = "DATABASE_PORT";
    public static final String DASHBOAR_ID = "dashboardId";
    public static final String DASHBOAR_GADGET_COLECCTION = "DashboardGadget";
    //internal conf
    public static final String CONCURRENT_THREAD = "internal.conf.concurrent_thread";
    public static final String CLEAN_CACHE_TIME = "internal.conf.clearcache.time";
    public static final String API_SESSION_INFO = "APICookies";
    public static final String API_SESSION_INFO_INTERNAL = "APICookiesInternal";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String ROLE = "role";
    public static final String GROUPS = "groups";
    public static final String NAME = "name";

    public static String DisplayName = "displayName";
    public static String alias = "alias";
    public static String GroupsItems = "items";


    public static String MetricKey[] = {"coverage", "critical_violations", "new_coverage", "test_errors",
            "test_execution_time", "test_failures"};

}
