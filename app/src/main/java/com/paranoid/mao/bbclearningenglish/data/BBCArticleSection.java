package com.paranoid.mao.bbclearningenglish.data;

/**
 * Created by Paranoid on 17/8/5.
 */

public class BBCArticleSection {

    private String title;
    private String article;

    public BBCArticleSection(String title, String article) {
        this.title = title;
        this.article = article;
    }

    public String getTitle() {
        return title;
    }

    public String getArticle() {
        return article;
    }
}
