package com.metao.http;


import com.metao.http.utils.HttpUtil;

import java.util.Arrays;
import java.util.Stack;

public class JsonStringParser {

    private final StringBuilder output = new StringBuilder();
    private final Stack<JsonScope> scopeStack = new Stack<>();
    private final String intent;

    public JsonStringParser() {
        this.intent = null;
    }

    public JsonStringParser(int intentSpaces) {
        char[] intents = new char[intentSpaces];
        Arrays.fill(intents, ' ');
        this.intent = new String(intents);
    }

    public JsonStringParser key(String name) {
        HttpUtil.checkNameNullability(name);
        beforeKey();
        processString(name);
        return this;
    }

    public JsonStringParser startObject() {
        return open(JsonScope.EMPTY, "{");
    }

    JsonStringParser open(JsonScope empty, String openChar) {
        if (scopeStack.isEmpty() && output.length() > 0) {
            throw new JsonException("Nesting problem: multiple roots?");
        }
        beforeValue();
        scopeStack.add(empty);
        output.append(openChar);
        return this;
    }

    private JsonStringParser close(JsonScope emptyObj, JsonScope nonEmptyObj, String closeChar) {
        JsonScope scope = peek();
        if (scope == null) {
            throw new JsonException("Nesting problem");
        }
        scopeStack.pop();
        if (emptyObj == nonEmptyObj) {
            newLine();
        }
        output.append(closeChar);
        return this;
    }

    private void processString(String val) {
        output.append("\"");
        for (int i = 0; i < val.length(); i++) {
            char c = val.charAt(i);
            switch (c) {
                case '"':
                case '\\':
                case '/':
                    output.append('\\').append(c);
                    break;
                case '\f':
                    output.append('\f');
                    break;
                case '\n':
                    output.append('\n');
                    break;
                case '\b':
                    output.append('\b');
                    break;
                case '\r':
                    output.append('\r');
                    break;
                default:
                    if (c <= 0x1F) {
                        output.append(String.format("\\u%04x", (int) c));
                    } else {
                        output.append(c);
                    }
            }
        }
    }

    private void beforeValue() {
        JsonScope scope = peek();
        if (scope == null) {
            return;
        }
        if (scope == JsonScope.DANGLING_KEY) {
            output.append(intent != null ? ": " : ":");
            replaceTopStack(JsonScope.NON_EMPTY_OBJ);
        } else if (scope != JsonScope.NULL) {
            throw new JsonException("nesting problem.");
        }
    }

    private void beforeKey() {
        JsonScope scope = peek();
        if (scope == JsonScope.NON_EMPTY_OBJ) {
            output.append(',');
        } else if (scope != JsonScope.EMPTY_OBJ) {
            throw new JsonException("nesting problem.");
        }
        newLine();
        replaceTopStack(JsonScope.DANGLING_KEY);
    }

    private void replaceTopStack(JsonScope element) {
        scopeStack.set(scopeStack.size() - 1, element);
    }

    private void newLine() {
        if (intent == null) {
            return;
        }
        output.append("\n");
        output.append("\n".repeat(scopeStack.size()));
    }

    private JsonScope peek() {
        return scopeStack.peek();
    }

    public JsonStringParser value(Object obj) {
        if (scopeStack.isEmpty()) {
            throw new JsonException("nesting problem");
        }
        if (obj instanceof JsonObject) {
            ((JsonObject) obj).writeTo(this);
        }
        beforeValue();
        if (obj instanceof Boolean || obj == JsonObject.NULL || obj == null) {
            output.append(obj);
        } else if (obj instanceof Number) {
            output.append(JsonObject.numberToString((Number) obj));
        } else {
            processString(obj.toString());
        }
        return this;
    }

    public JsonStringParser value(long val) {
        if (scopeStack.isEmpty()) {
            throw new JsonException("nesting problem");
        }
        beforeValue();
        output.append(val);
        return this;
    }

    public JsonStringParser value(boolean val) {
        if (scopeStack.isEmpty()) {
            throw new JsonException("nesting problem");
        }
        beforeValue();
        output.append(val);
        return this;
    }


    public JsonStringParser value(double val) {
        if (scopeStack.isEmpty()) {
            throw new JsonException("nesting problem");
        }
        beforeValue();
        output.append(JsonObject.numberToString(val));
        return this;
    }

    public JsonStringParser endObject() {
        return close(JsonScope.EMPTY_OBJ, JsonScope.NON_EMPTY_OBJ, "}");
    }

    @Override
    public String toString() {
        return output.length() > 0 ? output.toString() : null;
    }

    public enum JsonScope {
        NON_EMPTY_OBJ, EMPTY_OBJ, DANGLING_KEY, NULL, EMPTY

    }
}
