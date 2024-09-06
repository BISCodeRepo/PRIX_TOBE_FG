package com.prix.homepage.backend.livesearch.service.patternmatch;

import java.util.Arrays;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regex_Match
{

    private int findProtein = 0;
    private int findPattern = 0;

    public Regex_Match() {
    }

    public int[] getFindNumber() {
        int[] var1 = new int[]{this.findProtein, this.findPattern};
        return var1;
    }

    public String Match(String var1, String[] var2) {
        boolean var3 = false;
        int var4 = var2.length;
        int var5 = var1.length();
        Pattern var6 = null;
        Matcher var7 = null;
        Vector var8 = new Vector();
        Vector var9 = new Vector();
        ++this.findProtein;

        int var12;
        for(var12 = 0; var12 < var4; ++var12) {
            var6 = Pattern.compile(var2[var12], 2);
            var7 = var6.matcher(var1);
            int var16 = 0;

            while(var7.find(var16)) {
                var8.add(var7.start());
                var9.add(var7.end());
                var16 = var7.start() + 1;
                ++this.findPattern;
                if (var16 > var5) {
                    break;
                }
            }
        }

        Integer[] var10 = (Integer[])var8.toArray(new Integer[var8.size()]);
        Integer[] var11 = (Integer[])var9.toArray(new Integer[var9.size()]);
        Arrays.sort(var10);
        Arrays.sort(var11);
        var12 = var10.length;

        int var14;
        for(int var13 = var12 - 1; var13 >= 0; var13 = var14 - 1) {
            StringBuffer var15 = new StringBuffer();

            for(var14 = var13; var14 >= 1 && var10[var14] <= var11[var14 - 1]; --var14) {
            }

            var15.append(var1.substring(0, var10[var14]));
            var15.append("<font color=blue><b>");
            var15.append(var1.substring(var10[var14], var11[var13]));
            var15.append("</b></font color=blue>");
            var15.append(var1.substring(var11[var13], var1.length()));
            var1 = var15.toString();
        }

        return var1;
    }

    public void Count(String var1, String[] var2) {
        boolean var3 = false;
        int var4 = var2.length;
        int var5 = var1.length();
        Pattern var6 = null;
        Matcher var7 = null;
        ++this.findProtein;

        for(int var8 = 0; var8 < var4; ++var8) {
            var6 = Pattern.compile(var2[var8], 2);
            var7 = var6.matcher(var1);
            int var9 = 0;

            while(var7.find(var9)) {
                var9 = var7.start() + 1;
                ++this.findPattern;
                if (var9 > var5) {
                    break;
                }
            }
        }

    }
}
