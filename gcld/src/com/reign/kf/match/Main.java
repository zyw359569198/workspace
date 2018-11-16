package com.reign.kf.match;

import com.reign.framework.*;

public class Main
{
    public static void main(final String[] args) {
        final ServletBootstrap bootstrap = new ServletBootstrap();
        try {
            bootstrap.startup();
        }
        catch (InstantiationException e) {
            e.printStackTrace();
        }
        catch (IllegalAccessException e2) {
            e2.printStackTrace();
        }
        catch (Exception e3) {
            e3.printStackTrace();
        }
    }
}
