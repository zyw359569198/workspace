package com.reign.gcld.common.util.characterFilter;

import java.util.*;

public class ReplaceCharacterGetterRandom implements IReplaceCharacterGetter
{
    private List<Character> charList;
    private final Random random;
    
    public ReplaceCharacterGetterRandom() {
        this.random = new Random();
        (this.charList = new ArrayList<Character>()).add('@');
        this.charList.add('%');
        this.charList.add('&');
        this.charList.add('*');
        this.charList.add('$');
        this.charList.add('#');
    }
    
    @Override
    public char getChar() {
        return this.charList.get(this.random.nextInt(6));
    }
}
