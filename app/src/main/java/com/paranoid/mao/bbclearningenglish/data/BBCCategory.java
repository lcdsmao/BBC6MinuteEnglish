package com.paranoid.mao.bbclearningenglish.data;

import android.util.SparseArray;

import com.paranoid.mao.bbclearningenglish.R;
import com.paranoid.mao.bbclearningenglish.utilities.BBCHtmlUtility;

import java.util.HashMap;


/**
 * Created by Paranoid on 17/8/8.
 */

public class BBCCategory {

    public static final String CATEGORY_6_MINUTE_ENGLISH = "6Minute";
    public static final String CATEGORY_THE_ENGLISH_WE_SPEAK = "weSpeak";
    public static final String CATEGORY_NEWS_REPORT = "newsReport";
    public static final String CATEGORY_ENGLISH_AT_UNIVERSITY = "atUniversity";
    public static final String CATEGORY_LINGO_HACK = "lingoHack";

    public static final String[] ALL_CATEGORY = {
            CATEGORY_6_MINUTE_ENGLISH,
            CATEGORY_ENGLISH_AT_UNIVERSITY,
            CATEGORY_LINGO_HACK,
            CATEGORY_NEWS_REPORT,
            CATEGORY_THE_ENGLISH_WE_SPEAK
    };


    private static final HashMap<String, String> sCategoryUrlMap = createCategoryUrlMap();
    private static final HashMap<String, Integer> sCategoryItemIdMap = createCategoryItemIdMap();
    private static final HashMap<String, Integer> sCategoryStringResourceMap = createCategoryStringResourceMap();
    private static final SparseArray<String> sCategoryItemIdMapInverse = createCategoryItemIdMapInverse();

    private static HashMap<String, Integer> createCategoryStringResourceMap() {
        HashMap<String, Integer> map = new HashMap<>();
        map.put(CATEGORY_6_MINUTE_ENGLISH, R.string.category_6_minute_english);
        map.put(CATEGORY_ENGLISH_AT_UNIVERSITY, R.string.category_english_at_university);
        map.put(CATEGORY_NEWS_REPORT, R.string.category_news_report);
        map.put(CATEGORY_THE_ENGLISH_WE_SPEAK, R.string.category_the_english_we_speak);
        map.put(CATEGORY_LINGO_HACK, R.string.category_lingo_hack);
        return map;
    }

    private static HashMap<String, String> createCategoryUrlMap() {
        final HashMap<String, String> map = new HashMap<>();
        map.put(BBCHtmlUtility.BBC_6_MINUTE_ENGLISH_URL, CATEGORY_6_MINUTE_ENGLISH);
        map.put(BBCHtmlUtility.BBC_ENGLISH_AT_UNIVERSITY_URL, CATEGORY_ENGLISH_AT_UNIVERSITY);
        map.put(BBCHtmlUtility.BBC_NEWS_REPORT_URL, CATEGORY_NEWS_REPORT);
        map.put(BBCHtmlUtility.BBC_THE_ENGLISH_WE_SPEAK_URL, CATEGORY_THE_ENGLISH_WE_SPEAK);
        map.put(BBCHtmlUtility.BBC_LINGO_HACK_URL, CATEGORY_LINGO_HACK);

        map.put(CATEGORY_6_MINUTE_ENGLISH, BBCHtmlUtility.BBC_6_MINUTE_ENGLISH_URL);
        map.put(CATEGORY_ENGLISH_AT_UNIVERSITY, BBCHtmlUtility.BBC_ENGLISH_AT_UNIVERSITY_URL);
        map.put(CATEGORY_NEWS_REPORT, BBCHtmlUtility.BBC_NEWS_REPORT_URL);
        map.put(CATEGORY_THE_ENGLISH_WE_SPEAK, BBCHtmlUtility.BBC_THE_ENGLISH_WE_SPEAK_URL);
        map.put(CATEGORY_LINGO_HACK, BBCHtmlUtility.BBC_LINGO_HACK_URL);

        return map;
    }

    private static HashMap<String, Integer> createCategoryItemIdMap() {
        HashMap<String, Integer> map = new HashMap<>();
        map.put(CATEGORY_6_MINUTE_ENGLISH, R.id.category_six);
        map.put(CATEGORY_ENGLISH_AT_UNIVERSITY, R.id.category_university);
        map.put(CATEGORY_NEWS_REPORT, R.id.category_news_report);
        map.put(CATEGORY_THE_ENGLISH_WE_SPEAK, R.id.category_we_speak);
        map.put(CATEGORY_LINGO_HACK, R.id.category_lingo_hack);
        return map;
    }

    private static SparseArray<String> createCategoryItemIdMapInverse() {
        SparseArray<String> map = new SparseArray<>();
        map.put(R.id.category_six, CATEGORY_6_MINUTE_ENGLISH);
        map.put(R.id.category_university, CATEGORY_ENGLISH_AT_UNIVERSITY);
        map.put(R.id.category_news_report, CATEGORY_NEWS_REPORT);
        map.put(R.id.category_we_speak, CATEGORY_THE_ENGLISH_WE_SPEAK);
        map.put(R.id.category_lingo_hack, CATEGORY_LINGO_HACK);
        return map;
    }

    public static int getCategoryStringRecourse(String category) {
        if (!sCategoryStringResourceMap.containsKey(category)) return -1;
        return sCategoryStringResourceMap.get(category);
    }

    public static String getCategoryUrl(String category) {
        return sCategoryUrlMap.get(category);
    }

    public static String getUrlCategory(String url) {
        return sCategoryUrlMap.get(url);
    }

    public static int getCategoryItemId(String category) {
        if (!sCategoryItemIdMap.containsKey(category)) return -1;
        return sCategoryItemIdMap.get(category);
    }

    public static String getItemIdCategory(int itemId) {
        return sCategoryItemIdMapInverse.get(itemId);
    }
}
