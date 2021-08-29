package com.metao.http.service;

import com.metao.http.exception.JsonException;
import com.metao.http.model.http.JsonArray;
import com.metao.http.model.http.JsonObject;

public class JsonTokenizer {
    private final String string;
    private int pos;

    public JsonTokenizer(String string) {
        // consume an optional byte order mark (BOM) if it exists
        if (string != null && string.startsWith("\ufeff")) {
            string = string.substring(1);
        }
        this.string = string;
    }

    public Object nextValue() throws JsonException {
        int c = nextCleanInternal();
        switch (c) {
            case -1:
                throw new JsonException("end of object");
            case '{':
                return readObject();
            case '[':
                return readArray();
            case '\'':
            case '"':
                return readString((char) c);
            default:
                pos--;
                return readLiteral();
        }
    }

    private int nextCleanInternal() {
        while (pos < string.length()) {
            char c = string.charAt(pos++);
            switch (c) {
                case '\t':
                case '\n':
                case ' ':
                case '\r':
                    continue;
                case '/':
                    if (pos == string.length()) {
                        return c;
                    }
                    char peek = string.charAt(pos);
                    switch (peek) {
                        case '*':
                            pos++;
                            int endCommentPos = string.indexOf("*/", pos);
                            if (endCommentPos == -1) {
                                throw new JsonException("unterminated comment found at: " + pos);
                            }
                            pos = endCommentPos + 2;
                            continue;
                        case '/':
                            pos++;
                            skipToEndOfLine();
                            continue;
                        default:
                            return c;
                    }
                default:
                    return c;
            }
        }
        return -1;
    }

    private void skipToEndOfLine() {
        while (pos < string.length()) {
            char c = string.charAt(pos++);
            if (c == '\r' || c == '\n') {
                break;
            }
        }
    }

    private Object readLiteral() throws JsonException {
        String number = literalSign();
        switch (number) {
            case "null":
                return JsonObject.NULL;
            case "true":
                return Boolean.TRUE;
            case "false":
                return Boolean.FALSE;
        }
        if (number.indexOf('.') != -1) {
            try {
                return Double.valueOf(number);
            } catch (NumberFormatException e) {
                throw new JsonException("number format exception happened at pos: " + pos + ",reason: " + e.getMessage());
            }
        } else if (number.indexOf('.') == -1) {
            int base = 10;
            if (number.startsWith("0x") || number.startsWith("0X")) {
                base = 16;
                number = number.substring(2);
            } else if (number.startsWith("0") && number.length() > 1) {
                base = 8;
                number = number.substring(1);
            }
            long parsedNumber = Long.parseLong(number, base);
            if (parsedNumber >= Integer.MIN_VALUE && parsedNumber <= Integer.MAX_VALUE) {
                return (int) parsedNumber;
            } else {
                return parsedNumber;
            }
        }
        return number;
    }

    private String literalSign() {
        String excludingSequence = "{}[]/\\:,=;# \t\f";
        int start = pos;
        for (; pos < string.length(); pos++) {
            char c = string.charAt(pos);
            if (c == '\r' || c == '\n' || excludingSequence.indexOf(c) != -1) {
                return string.substring(start, pos);
            }
        }
        return string.substring(start);
    }

    private String readString(char quote) {
        StringBuilder sb = null;
        int start = pos;
        while (pos < string.length()) {
            char c = string.charAt(pos++);
            if (c == quote) {
                if (sb == null) {
                    return string.substring(start, pos - 1);
                } else {
                    sb.append(string, start, pos - 1);
                    return sb.toString();
                }
            }
            if (c == '\\') {
                if (pos == string.length()) {
                    throw new JsonException("unterminated scape sequence");
                }
                if (sb == null) {
                    sb = new StringBuilder();
                }
                sb.append(string, start, pos - 1);
                sb.append(readEscapeChar());
                start = pos;
            }
        }
        throw new JsonException("unterminated string found at: " + pos);
    }

    private char readEscapeChar() throws JsonException {
        char escapedChar = string.charAt(pos++);
        switch (escapedChar) {
            case 'u':
                if (pos + 4 > string.length()) {
                    throw new JsonException("unterminated scape char found at: " + pos);
                }
                String hex = string.substring(pos, pos + 4);
                pos += 4;
                try {
                    return (char) Integer.parseInt(hex, 16);
                } catch (NumberFormatException e) {
                    throw new JsonException("Format exception of value. Invalid scape char " + hex + " at position: " + pos);
                }
            case 'b':
                return '\b';
            case 't':
                return '\t';
            case 'n':
                return '\n';
            case 'r':
                return '\r';
            case 'f':
                return '\f';
            case '\'':
            case '\\':
            case '"':
            default:
                return escapedChar;
        }
    }

    private JsonArray readArray() {
        return null;
    }

    private JsonObject readObject() {
        JsonObject jsonObject = new JsonObject();
        int i = nextCleanInternal();
        if (i == '}') {
            return jsonObject;
        } else if (i != -1) {
            pos--;
        }
        while (true) {
            Object objectName = nextValue();
            if (objectName == null) {
                throw new JsonException("names cannot be null.");
            } else if (!(objectName instanceof String)) {
                throw new JsonException("names type is not String and is type of " + objectName.getClass().getName());
            }
            int separator = nextCleanInternal();
            if (separator != ':') {
                throw new JsonException("':' is expected after " + objectName + " but " + separator + " were given,");
            }
            if (pos < string.length() && string.charAt(pos) == '>') {
                pos++;
            }
            jsonObject.put((String) objectName, nextValue());
            switch (nextCleanInternal()) {
                case '}':
                    return jsonObject;
                case ';':
                case ',':
                    continue;
                default:
                    throw new JsonException("Object terminated unexpectedly at position: " + pos + ", 10 chars sequence before incident: " + getStringBeforeIncident(pos) + "\n");
            }
        }
    }

    private String getStringBeforeIncident(int pos) {
        StringBuilder sb = new StringBuilder();
        for (int index = ((pos - 10) >= 0) ? pos - 10 : pos; index <= pos; index++) {
            sb.append(string.charAt(index));
        }
        return sb.toString();
    }
}
