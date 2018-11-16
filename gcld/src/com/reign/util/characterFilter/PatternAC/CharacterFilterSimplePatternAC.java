package com.reign.util.characterFilter.PatternAC;

import java.util.concurrent.locks.*;
import org.apache.commons.logging.*;
import com.reign.util.characterFilter.*;
import java.io.*;

public class CharacterFilterSimplePatternAC extends CharacterFilterBase
{
    private static final Log logger;
    private TrieTree trieTree;
    private final ReentrantReadWriteLock lock;
    private final Lock readLock;
    private final Lock writeLock;
    
    static {
        logger = LogFactory.getLog(CharacterFilterSimplePatternAC.class);
    }
    
    public CharacterFilterSimplePatternAC() {
        this.lock = new ReentrantReadWriteLock();
        this.readLock = this.lock.readLock();
        this.writeLock = this.lock.writeLock();
        this.trieTree = new TrieTree();
    }
    
    public CharacterFilterSimplePatternAC(final IReplaceCharacterGetter replaceCharacterGetter) {
        super(replaceCharacterGetter);
        this.lock = new ReentrantReadWriteLock();
        this.readLock = this.lock.readLock();
        this.writeLock = this.lock.writeLock();
        this.trieTree = new TrieTree();
    }
    
    public CharacterFilterSimplePatternAC(final BufferedReader buff) {
        this.lock = new ReentrantReadWriteLock();
        this.readLock = this.lock.readLock();
        this.writeLock = this.lock.writeLock();
        this.trieTree = new TrieTree();
        this.buildFilterKeyWord(buff);
    }
    
    public CharacterFilterSimplePatternAC(final IReplaceCharacterGetter replaceCharacterGetter, final BufferedReader buff) {
        super(replaceCharacterGetter);
        this.lock = new ReentrantReadWriteLock();
        this.readLock = this.lock.readLock();
        this.writeLock = this.lock.writeLock();
        this.trieTree = new TrieTree();
        this.buildFilterKeyWord(buff);
    }
    
    @Override
    public String filter(final String str) {
        this.readLock.lock();
        try {
            return this.trieTree.filterStr(str, this.replaceCharacterGetter);
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public boolean isValid(final String str) {
        this.readLock.lock();
        try {
            return this.trieTree.isContrainsPattern(str);
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public void buildFilterKeyWord(final BufferedReader buff) {
        final TrieTree newTree = new TrieTree();
        String keyword = null;
        try {
            buff.read();
            while ((keyword = buff.readLine()) != null) {
                newTree.addPattern(keyword);
            }
        }
        catch (IOException e) {
            CharacterFilterSimplePatternAC.logger.error("IO\u5f02\u5e38!", e);
        }
        newTree.setFailNode();
        this.writeLock.lock();
        try {
            this.trieTree = newTree;
        }
        finally {
            this.writeLock.unlock();
        }
        this.writeLock.unlock();
    }
}
