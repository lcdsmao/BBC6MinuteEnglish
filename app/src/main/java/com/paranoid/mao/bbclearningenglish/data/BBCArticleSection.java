package com.paranoid.mao.bbclearningenglish.data;
/**
 * Created by Paranoid on 17/8/5.
 */

public class BBCArticleSection {

    private String title;
    private String article;

    public BBCArticleSection() {
        this.title = "";
        this.article = "";
    }

    public BBCArticleSection(String title, String article) {
        this.title = title;
        this.article = article;
    }

    public BBCArticleSection setTitle(String title) {
        this.title = title;
        return this;
    }

    public BBCArticleSection setArticle(String article) {
        this.article = article;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public String getArticle() {
        return article;
    }
}
