package com.paranoid.mao.bbclearningenglish.data;

/**
 * Created by Paranoid on 17/9/10.
 */

public class VocabularyDefinition {

    private String mWord;
    private String mDefinition;
    private String mSymbol;
    private String mAudioHref;

    public VocabularyDefinition(String mWord) {
        this.mWord = mWord;
        this.mDefinition = "Nothing here";
        this.mSymbol = "";
        this.mAudioHref = "";
    }

    public void setDefinition(String mMean) {
        this.mDefinition = mMean;
    }

    public void setWord(String word) {
        mWord = word;
    }

    public void setSymbol(String mSymbol) {
        this.mSymbol = mSymbol;
    }

    public void setAudioHref(String mAudioHref) {
        this.mAudioHref = mAudioHref;
    }

    public String getWord() {
        return mWord;
    }

    public String getDefinition() {
        return mDefinition;
    }

    public String getSymbol() {
        return mSymbol;
    }

    public String getAudioHref() {
        return mAudioHref;
    }
}
