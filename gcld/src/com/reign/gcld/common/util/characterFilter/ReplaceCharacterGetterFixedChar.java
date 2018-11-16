package com.reign.gcld.common.util.characterFilter;

public class ReplaceCharacterGetterFixedChar implements IReplaceCharacterGetter
{
    private char replaceChar;
    
    public ReplaceCharacterGetterFixedChar(final char replaceChar) {
        this.replaceChar = replaceChar;
    }
    
    @Override
    public char getChar() {
        return this.replaceChar;
    }
}
