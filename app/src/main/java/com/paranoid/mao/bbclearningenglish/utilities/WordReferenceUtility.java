package com.paranoid.mao.bbclearningenglish.utilities;

import android.support.annotation.Nullable;

import com.paranoid.mao.bbclearningenglish.data.VocabularyDefinition;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by Paranoid on 17/9/11.
 */

public class WordReferenceUtility {

    private static final String WORD_REFERENCE =
            "http://www.wordreference.com";
    private static final String WORD_REFERENCE_DEFINITION =
            "http://www.wordreference.com/definition/";

    public static String getWordUrl(String word) {
        return WORD_REFERENCE_DEFINITION + word;
    }

    private static Document getDocument(String html) {
        return Jsoup.parse(html);
    }

    private static String getAudio(Document doc) {
        Elements elements = doc.select("#aud0 > source");
        if (elements.size() == 0) {
            return "";
        } else {
            return WORD_REFERENCE + elements.first().attr("src");
        }
    }

    @Nullable
    private static Element getTrans(Document doc) {
        Elements trans = doc.select("#article > .entryRH");
        if (trans.size() == 0) {
            return null;
        } else {
            return trans.first();
        }
    }

    private static String getWord(Element trans) {
        return trans.select(".rh_me").text();
    }

    private static String getSymbol(Element trans) {
        Element tooltip = trans.select(".tooltip").first();
        if (tooltip != null) {
            return tooltip.ownText();
        } else {
            return "";
        }
    }

    private static String getDefinition(Element trans) {
        Elements def = trans.select(".rh_def");
        String defStr = "";
        int maxNum = Math.min(def.size(), 2);
        for (int i = 0; i < maxNum; i++) {
            String d = def.get(i).ownText();
            int l = d.length() - 1;
            if (d.charAt(l) == ':') d = d.substring(0, l);
            defStr += (i + 1) + ". " + d;
            if (i < maxNum - 1) defStr += "\n\n";
        }
        return defStr;
    }

    public static VocabularyDefinition getVocab(String html, String defaultWord) {
        VocabularyDefinition vocab = new VocabularyDefinition(defaultWord);
        Document doc = getDocument(html);
        Element trans = getTrans(doc);
        if (trans != null) {
            vocab.setWord(getWord(trans));
            vocab.setSymbol(getSymbol(trans));
            vocab.setDefinition(getDefinition(trans));
        }
        vocab.setAudioHref(getAudio(doc));
        return vocab;
    }

}
