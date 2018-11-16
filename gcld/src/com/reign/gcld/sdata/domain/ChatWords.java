package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class ChatWords implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String lv;
    private String words;
    private String[] wordsArray;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public String getLv() {
        return this.lv;
    }
    
    public void setLv(final String lv) {
        this.lv = lv;
    }
    
    public String getWords() {
        return this.words;
    }
    
    public void setWords(final String words) {
        this.words = words;
    }
    
    public String[] getWordsArray() {
        return this.wordsArray;
    }
    
    public void setWordsArray(final String[] wordsArray) {
        this.wordsArray = wordsArray;
    }
}
