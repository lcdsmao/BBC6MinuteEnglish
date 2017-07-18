package com.example.mao.bbc6minuteenglish.utilities;

import android.content.ContentValues;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Created by MAO on 7/17/2017.
 */

public class BBCHtmlUtil {

    private static final String BBC_URL = "http://www.bbc.co.uk";

    private static final String BBC_6_MINUTE_ENGLISH_URL =
            "http://www.bbc.co.uk/learningenglish/english/features/6-minute-english";

    private static final String ALL_CONTENT_CLASS = ".widget-progress-enabled";

    public static Elements sAllContents;

    public static void updateDocument() throws IOException{
        Document document = Jsoup.connect(BBC_6_MINUTE_ENGLISH_URL).get();
        sAllContents = document.select(ALL_CONTENT_CLASS);
    }

    private BBCHtmlUtil(){}

    public static Elements getContentsList() {
        if (sAllContents == null) return null;

        Elements contents = new Elements();
        contents.add(sAllContents.first());
        contents.addAll(sAllContents.get(1).select("li"));
        return contents;
    }

    public static String getTitle(Element content) {
        Elements texts = content.select(".text a");
        return texts.first().text();
    }

    public static String getHref(Element content) {
        Elements texts = content.select(".text a");
        return BBC_URL + texts.attr("href");
    }

    public static String getImg(Element content) {
        Elements img = content.select(".img img");
        return img.attr("src");
    }

    public static String getTime(Element content){
        Elements details = content.select(".details");
        return details.select("h3").text();
    }

    public static String getDescription(Element content){
        Elements details = content.select(".details");
        return details.select("p").text();
    }
}
