package service;

import models.Components;
import models.Sonar;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SonarStatisticsServiceImpl implements SonarStatisticsService {

    final static Logger logger = Logger.getLogger(SonarStatisticsServiceImpl.class);


    public Components getComponent(String iaName, String urlStr) {
        Components component = new Components();
        List<String> sonarKeys = new ArrayList<>();
        URL url;
        BufferedReader br = null;
        try {
            url = new URL(urlStr);
            URLConnection conn = url.openConnection();
            br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;

            while ((inputLine = br.readLine()) != null) {
                String[] arr = inputLine.split(",");
                if (arr[0].equals(iaName)) {
                    component.setIaName(iaName);
                    for (int i = 1; i < arr.length; i++) {
                        sonarKeys.add(arr[i]);
                    }
                    component.setSonarKeys(sonarKeys);
                }
                continue;
            }
        } catch (MalformedURLException e) {
            logger.error("MALFORMEDURLEXCEPTION " + e);
        } catch (IOException e) {
            logger.error("IOEXCEPTION " + e);
        } catch (Exception e) {
            logger.error("EXCEPTION " + e);
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        logger.info("COMPONENT " + component);
        return component;
    }

    @Override
    public Map<String, Sonar> getSonarStatistic(String iaNames, String metric, String url, String period) {
        return null;
    }

    @SuppressWarnings("unchecked")


    @Override
    public Map<String, Object> getPeriods() {
//        Map<String, Object> periods = new TreeMap<>();
//        Document doc = LinkUtil.getConnectionWithProxy(Constant.LINK_GET_JIRA_PERIODS, Constant.PROXY_IP, Constant.PROXY_PORT);
//        String json = doc.body().text();
//        JSONParser parser = new JSONParser();
//        try {
//            Object object = parser.parse(json);
//            JSONArray array = (JSONArray) object;
//            JSONObject jsonObject = null;
//            for (int i = 0; i < array.size(); i++) {
//                jsonObject = (JSONObject) array.get(i);
//                if (jsonObject.get("key").equals("sonar.timemachine.period1")) {
//                    periods.put("period1", jsonObject.get("value"));
//                }
//                if (jsonObject.get("key").equals("sonar.timemachine.period2")) {
//                    periods.put("period2", jsonObject.get("value"));
//                }
//                if (jsonObject.get("key").equals("sonar.timemachine.period3")) {
//                    periods.put("period3", jsonObject.get("value"));
//                }
//            }
//        } catch (ParseException e) {
//            logger.error("PARSEEXCEPTION " + e.getMessage());
//        }

        return null;
    }
}
