package com.example.mao.bbc6minuteenglish.utilities;

import android.content.ContentValues;
import android.support.annotation.Nullable;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Created by MAO on 7/17/2017.
 */

public class BBCHtmlUtil {

    // Base url of bbc
    private static final String BBC_URL = "http://www.bbc.co.uk";

    // Url of 6 minute English home page
    private static final String BBC_6_MINUTE_ENGLISH_URL =
            "http://www.bbc.co.uk/learningenglish/english/features/6-minute-english";

    private BBCHtmlUtil(){}

    /**
     * Connect to bbc 6 minute English to get the newest document html.
     * Use Jsoup to parse the html to Elements which contains all contents.
     * @return list of all contents
     */
    @Nullable
    public static Elements getContentsList() {
        Elements elements;
        try{
            Document document = Jsoup.connect(BBC_6_MINUTE_ENGLISH_URL).get();
            elements = document.select(".widget-progress-enabled");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        Elements contents = new Elements();
        contents.add(elements.first());
        contents.addAll(elements.get(1).select("li"));
        return contents;
    }

    /**
     * Get content's title
     * @param content the Element inside getContentsList()
     * @return title
     */
    public static String getTitle(Element content) {
        Elements texts = content.select(".text a");
        return texts.first().text();
    }

    /**
     * Get content's content's hyper link
     * @param content the Element inside getContentsList()
     * @return content's hyper link
     */
    public static String getArticleHref(Element content) {
        Elements texts = content.select(".text a");
        return BBC_URL + texts.attr("href");
    }

    /**
     * Get content's thumbnail's hyper link
     * @param content the Element inside getContentsList()
     * @return thumbnail's hyper link
     */
    public static String getImageHref(Element content) {
        Elements img = content.select(".img img");
        return img.attr("src");
    }

    /**
     * Get content's time
     * @param content the Element inside getContentsList()
     * @return time
     */
    public static String getTime(Element content){
        Elements details = content.select(".details");
        return details.select("h3").text();
    }

    /**
     * Get content's short description
     * @param content the Element inside getContentsList()
     * @return description
     */
    public static String getDescription(Element content){
        Elements details = content.select(".details");
        return details.select("p").text();
    }

    @Nullable
    public static Document getSpecificContent(String url) {
        Document document;
        try {
            document = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return document;
    }

    /**
     * Parse the article form specific content document
     * @param document
     * @return article's html format string
     */
    public static String getArticleHtml(Document document) {
        Elements article = document.select(".widget.widget-richtext.6 .text").first().children();
        return article.toString().replaceAll("h3", "h1");
    }

    /**
     * Parse the mp3 link from specific content document
     * @param document
     * @return mp3 link string
     */
    public static String getMp3Href(Document document) {
        Elements mp3Href = document.select(".download.bbcle-download-extension-mp3");
        return mp3Href.attr("href");
    }
}
