package com.reign.util.characterFilter;

public abstract class CharacterFilterBase implements ICharacterFilter
{
    protected IReplaceCharacterGetter replaceCharacterGetter;
    
    public CharacterFilterBase() {
        this.replaceCharacterGetter = new ReplaceCharacterGetterDefault();
    }
    
    public CharacterFilterBase(final IReplaceCharacterGetter replaceCharacterGetter) {
        if (replaceCharacterGetter != null) {
            this.replaceCharacterGetter = replaceCharacterGetter;
        }
        else {
            this.replaceCharacterGetter = new ReplaceCharacterGetterDefault();
        }
    }
    
    @Override
    public void setReplaceCharacterGetter(final IReplaceCharacterGetter replaceCharacterGetter) {
        if (replaceCharacterGetter != null) {
            this.replaceCharacterGetter = replaceCharacterGetter;
        }
        else {
            this.replaceCharacterGetter = new ReplaceCharacterGetterDefault();
        }
    }
}
