package com.example.aisocket.week1.tokenizer;

public class SimpleTokenizer implements Tokenizer {

    @Override
    public int countTokens(String text) {

        if (text == null || text.isBlank()) {
            return 0;
        }

        int tokenCount = 0;
        int alphaNumericLength = 0;

        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);

            if (Character.isWhitespace(ch)) {
                tokenCount += calculateAlphaNumericTokens(alphaNumericLength);
                alphaNumericLength = 0;
                continue;
            }

            if (isKorean(ch)) {
                tokenCount += calculateAlphaNumericTokens(alphaNumericLength);
                alphaNumericLength = 0;

                tokenCount += 1;
                continue;
            }

            if (Character.isLetterOrDigit(ch)) {
                alphaNumericLength++;
                continue;
            }

            tokenCount += calculateAlphaNumericTokens(alphaNumericLength);
            alphaNumericLength = 0;

            tokenCount += 1;
        }

        tokenCount += calculateAlphaNumericTokens(alphaNumericLength);

        return tokenCount;
    }

    private int calculateAlphaNumericTokens(int length) {
        if (length == 0) {
            return 0;
        }

        return (length + 3) / 4;
    }

    private boolean isKorean(char ch) {
        return ch >= '가' && ch <= '힣';
    }
}
