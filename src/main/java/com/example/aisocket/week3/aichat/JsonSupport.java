package com.example.aisocket.week3.aichat;

final class JsonSupport {

    private JsonSupport() {
    }

    static String escape(String value) {
        StringBuilder escaped = new StringBuilder(value.length() + 16);
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            switch (ch) {
                case '"' -> escaped.append("\\\"");
                case '\\' -> escaped.append("\\\\");
                case '\b' -> escaped.append("\\b");
                case '\f' -> escaped.append("\\f");
                case '\n' -> escaped.append("\\n");
                case '\r' -> escaped.append("\\r");
                case '\t' -> escaped.append("\\t");
                default -> {
                    if (ch < 0x20) {
                        escaped.append("\\u%04x".formatted((int) ch));
                    } else {
                        escaped.append(ch);
                    }
                }
            }
        }
        return escaped.toString();
    }

    static String extractString(String json, String targetKey) {
        int index = skipWhitespace(json, 0);
        if (index >= json.length() || json.charAt(index) != '{') {
            return null;
        }
        index++;

        while (index < json.length()) {
            index = skipWhitespace(json, index);
            if (index < json.length() && json.charAt(index) == '}') {
                return null;
            }

            ParsedString key = parseString(json, index);
            if (key == null) {
                return null;
            }
            index = skipWhitespace(json, key.nextIndex());
            if (index >= json.length() || json.charAt(index) != ':') {
                return null;
            }
            index = skipWhitespace(json, index + 1);

            if (targetKey.equals(key.value())) {
                ParsedString value = parseString(json, index);
                return value == null ? null : value.value();
            }

            index = skipValue(json, index);
            if (index < 0) {
                return null;
            }
            index = skipWhitespace(json, index);
            if (index < json.length() && json.charAt(index) == ',') {
                index++;
            } else if (index < json.length() && json.charAt(index) == '}') {
                return null;
            } else {
                return null;
            }
        }
        return null;
    }

    private static ParsedString parseString(String json, int start) {
        if (start >= json.length() || json.charAt(start) != '"') {
            return null;
        }

        StringBuilder value = new StringBuilder();
        for (int i = start + 1; i < json.length(); i++) {
            char ch = json.charAt(i);
            if (ch == '"') {
                return new ParsedString(value.toString(), i + 1);
            }
            if (ch != '\\') {
                value.append(ch);
                continue;
            }
            if (++i >= json.length()) {
                return null;
            }
            char escaped = json.charAt(i);
            switch (escaped) {
                case '"' -> value.append('"');
                case '\\' -> value.append('\\');
                case '/' -> value.append('/');
                case 'b' -> value.append('\b');
                case 'f' -> value.append('\f');
                case 'n' -> value.append('\n');
                case 'r' -> value.append('\r');
                case 't' -> value.append('\t');
                case 'u' -> {
                    if (i + 4 >= json.length()) {
                        return null;
                    }
                    try {
                        value.append((char) Integer.parseInt(
                                json.substring(i + 1, i + 5),
                                16
                        ));
                    } catch (NumberFormatException e) {
                        return null;
                    }
                    i += 4;
                }
                default -> {
                    return null;
                }
            }
        }
        return null;
    }

    private static int skipValue(String json, int index) {
        if (index >= json.length()) {
            return -1;
        }
        if (json.charAt(index) == '"') {
            ParsedString value = parseString(json, index);
            return value == null ? -1 : value.nextIndex();
        }

        int objectDepth = 0;
        int arrayDepth = 0;
        boolean inString = false;
        boolean escaped = false;

        for (int i = index; i < json.length(); i++) {
            char ch = json.charAt(i);
            if (inString) {
                if (escaped) {
                    escaped = false;
                } else if (ch == '\\') {
                    escaped = true;
                } else if (ch == '"') {
                    inString = false;
                }
                continue;
            }

            switch (ch) {
                case '"' -> inString = true;
                case '{' -> objectDepth++;
                case '}' -> {
                    if (objectDepth == 0 && arrayDepth == 0) {
                        return i;
                    }
                    objectDepth--;
                }
                case '[' -> arrayDepth++;
                case ']' -> arrayDepth--;
                case ',' -> {
                    if (objectDepth == 0 && arrayDepth == 0) {
                        return i;
                    }
                }
                default -> {
                }
            }
        }
        return json.length();
    }

    private static int skipWhitespace(String value, int index) {
        while (index < value.length()
                && Character.isWhitespace(value.charAt(index))) {
            index++;
        }
        return index;
    }

    private record ParsedString(String value, int nextIndex) {
    }
}
