package com.reign.gcld.common.util.characterFilter;

public class ReplaceCharacterGetterDefault implements IReplaceCharacterGetter
{
    public static final char REPLACE_CHARACTER = '*';
    
    @Override
    public char getChar() {
        return '*';
    }
}
