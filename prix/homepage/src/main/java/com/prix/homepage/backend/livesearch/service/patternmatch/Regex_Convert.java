package com.prix.homepage.backend.livesearch.service.patternmatch;

public class Regex_Convert {

    public Regex_Convert() {
    }

    public static String[] PrositeToPerl(String[] inputPattern) {
        int inputPatternLen = inputPattern.length;
        String[] outputPattern = new String[inputPatternLen];

        for(int i = 0; i < inputPatternLen; ++i) {
            outputPattern[i] = inputPattern[i].trim();
            outputPattern[i] = outputPattern[i].replace('x', '.');
            outputPattern[i] = outputPattern[i].replace('X', '.');
            outputPattern[i] = outputPattern[i].replace("{", "[^");
            outputPattern[i] = outputPattern[i].replace('}', ']');
            outputPattern[i] = outputPattern[i].replace('(', '{');
            outputPattern[i] = outputPattern[i].replace(')', '}');
            outputPattern[i] = outputPattern[i].replace('<', '^');
            outputPattern[i] = outputPattern[i].replace('>', '$');
            outputPattern[i] = outputPattern[i].replace("-", "");
            outputPattern[i] = outputPattern[i].replace(" ", "");
        }

        return outputPattern;
    }

}
