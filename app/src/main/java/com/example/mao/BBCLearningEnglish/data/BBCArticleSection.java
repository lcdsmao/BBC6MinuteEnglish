package com.example.mao.BBCLearningEnglish.data;

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

    public void setTitle(String title) {
        this.title = title;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public String getTitle() {
        return title;
    }

    public String getArticle() {
        return article;
    }
}
