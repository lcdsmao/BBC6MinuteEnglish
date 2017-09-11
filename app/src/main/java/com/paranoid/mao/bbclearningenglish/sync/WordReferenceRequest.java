package com.paranoid.mao.bbclearningenglish.sync;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.paranoid.mao.bbclearningenglish.data.VocabularyDefinition;
import com.paranoid.mao.bbclearningenglish.utilities.WordReferenceUtility;

import java.io.UnsupportedEncodingException;

/**
 * Created by Paranoid on 17/9/10.
 */

public class WordReferenceRequest extends Request<VocabularyDefinition> {

    private Response.Listener<VocabularyDefinition> mListener;
    private String mWord;

    public WordReferenceRequest(String word,
                                Response.Listener<VocabularyDefinition> listener,
                                Response.ErrorListener errorListener) {
        super(Method.GET, WordReferenceUtility.getWordUrl(word), errorListener);
        mListener = listener;
        mWord = word;
    }

    @Override
    protected Response<VocabularyDefinition> parseNetworkResponse(NetworkResponse response) {
        String html;
        try {
            html = new String(response.data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
        VocabularyDefinition vocab = WordReferenceUtility.getVocab(html, mWord);
        return Response.success(vocab, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(VocabularyDefinition response) {
        mListener.onResponse(response);
    }
}
