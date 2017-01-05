//package service;
//
//import models.IA;
//import org.apache.log4j.Logger;
//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;
//import org.json.simple.parser.ParseException;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//import util.Constant;
//import util.LinkUtil;
//
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.util.*;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//public class ODReviewsServiceImpl implements ODReviewsService {
//
//    final static Logger logger = Logger.getLogger(ODReviewsServiceImpl.class);
//
//    public IA getODReview(String iaName, String project) {
//        IA ia;
//        Document doc = LinkUtil.getConnection(String.format(Constant.LINK_GET_ODREVIEW_REPORTS, iaName, project),
//                Constant.TOKEN);
//        logger.info("GETODREVIEW " + String.format(Constant.LINK_GET_ODREVIEW_REPORTS, iaName, project));
//        Elements list = doc.select("detailedReviewData > createDate");
//        int lessThanFive = 0;
//        int moreThanFiveLess10 = 0;
//        int wayTooLate = 0;
//        Date currentDate = new Date();
//        for (Element item : list) {
//            if ((countDay(currentDate, convertStringToDate(item.text()))) > 10) {
//                wayTooLate++;
//            } else {
//                if ((countDay(currentDate, convertStringToDate(item.text()))) < 5) {
//                    lessThanFive++;
//                } else {
//                    moreThanFiveLess10++;
//                }
//            }
//        }
//        ia = new IA();
//        Document doc2 = LinkUtil.getConnection(String.format(Constant.LINK_GET_JIRA_USER_INFO, iaName), Constant.TOKEN);
//        logger.info("CHANGE SHORTNAME: " + iaName + " TO ALIAS: " + (doc2 != null));
//        if (doc2 != null) {
//            String json = doc2.body().text();
//            JSONParser parser = new JSONParser();
//            try {
//                JSONObject jsonObject = (JSONObject) parser.parse(json);
//                iaName = jsonObject.get("displayName").toString();
//            } catch (ParseException e) {
//                logger.error(String.format("GET_USER_INFO OF %s ERROR ", iaName, e));
//            }
//        }
//
//        ia.setIaName(iaName);
//        ia.setLessThanFive(lessThanFive);
//        ia.setMoreThanFiveLess10(moreThanFiveLess10);
//        ia.setWayTooLate(wayTooLate);
//        return ia;
//    }
//
//    private Date convertStringToDate(String strDate) {
//        Date date = new Date();
//        strDate = strDate.substring(0, 10);
//        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//        try {
//            return date = (Date) formatter.parse(strDate);
//        } catch (java.text.ParseException e) {
//            e.printStackTrace();
//        }
//        return date;
//    }
//
//    private long countDay(Date currentDate, Date startDate) {
//        return ((currentDate.getTime() - startDate.getTime()) / (60 * 60 * 1000 * 24));
//    }
//
//    @Override
//    public List<IA> getODReviews(String iaName, String project) {
//        Set<IA> setIA = new HashSet<IA>();
//        List<IA> listIA = new ArrayList<IA>();
//        if (!iaName.equals("") || !project.equals("")) {
//            IA ia = new IA();
//            Document doc = LinkUtil.getConnection(String.format(Constant.LINK_GET_ODREVIEW_REPORTS, iaName, project),
//                    Constant.TOKEN);
//            if (doc != null) {
//                Elements list = doc.select("detailedReviewData > creator > userName");
//                for (Element item : list) {
//                    ia = getODReview(item.text(), project);
//                    listIA.add(ia);
//                }
//                setIA.addAll(listIA);
//                listIA.clear();
//                listIA.addAll(setIA);
//            }
//        }
//        Comparator<IA> comparator = Comparator.comparing(ia -> ia.m_wayTooLate);
//        comparator = comparator.thenComparing(Comparator.comparing(ia -> ia.m_moreThanFiveLess10));
//        comparator = comparator.thenComparing(Comparator.comparing(ia -> ia.m_lessThanFive));
//
//        // Sort the stream:
//        Stream<IA> iaStream = listIA.stream().sorted(comparator.reversed());
//
//        // Make sure that the output is as expected:
//        List<IA> sortedListIA = iaStream.collect(Collectors.toList());
//        return sortedListIA;
//    }
//
//}
