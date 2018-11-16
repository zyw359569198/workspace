package com.reign.framework.jdbc;

public class DefaultNameStrategy implements NameStrategy
{
    @Override
    public String columnNameToPropertyName(final String colomnName) {
        final StringBuilder builder = new StringBuilder(colomnName.length());
        boolean capital = false;
        for (int i = 0; i < colomnName.length(); ++i) {
            final char ch = colomnName.charAt(i);
            switch (ch) {
                case '_': {
                    capital = true;
                    break;
                }
                default: {
                    if (capital) {
                        builder.append(Character.toUpperCase(ch));
                        capital = false;
                        break;
                    }
                    builder.append(ch);
                    break;
                }
            }
        }
        return builder.toString();
    }
    
    @Override
    public String propertyNameToColumnName(final String propertyName) {
        final StringBuilder builder = new StringBuilder(propertyName.length() + 1);
        boolean capital = false;
        boolean first = true;
        for (int i = 0; i < propertyName.length(); ++i) {
            final char ch = propertyName.charAt(i);
            if (ch >= 'A' && ch <= 'Z') {
                capital = true;
            }
            if (capital && !first) {
                builder.append("_").append(Character.toLowerCase(ch));
                capital = false;
            }
            else if (capital) {
                first = false;
                capital = false;
                builder.append(Character.toLowerCase(ch));
            }
            else {
                builder.append(ch);
            }
        }
        return builder.toString();
    }
}
