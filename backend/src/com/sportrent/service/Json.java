package com.sportrent.service;
import java.util.*;

/** Minimal hand-rolled JSON parser + serializer. Enough for our flat record models. */
public class Json {

    // ---------- Serializer ----------
    public static String stringify(Object o) {
        StringBuilder sb = new StringBuilder();
        write(sb, o);
        return sb.toString();
    }
    @SuppressWarnings("unchecked")
    private static void write(StringBuilder sb, Object o) {
        if (o == null) { sb.append("null"); return; }
        if (o instanceof String s) { sb.append(str(s)); return; }
        if (o instanceof Boolean || o instanceof Number) { sb.append(o); return; }
        if (o instanceof Map<?,?> m) {
            sb.append("{"); boolean first = true;
            for (var e : m.entrySet()) {
                if (!first) sb.append(",");
                sb.append(str(e.getKey().toString())).append(":");
                write(sb, e.getValue()); first = false;
            }
            sb.append("}"); return;
        }
        if (o instanceof List<?> l) {
            sb.append("["); boolean first = true;
            for (var v : l) { if (!first) sb.append(","); write(sb, v); first = false; }
            sb.append("]"); return;
        }
        // fallback: treat as string
        sb.append(str(o.toString()));
    }
    public static String str(String s) {
        StringBuilder sb = new StringBuilder("\"");
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < 0x20) sb.append(String.format("\\u%04x", (int) c));
                    else sb.append(c);
            }
        }
        return sb.append("\"").toString();
    }

    // ---------- Parser ----------
    public static Object parse(String s) {
        Parser p = new Parser(s); p.skip();
        Object v = p.value(); p.skip();
        return v;
    }
    @SuppressWarnings("unchecked")
    public static Map<String,Object> obj(String s) { return (Map<String,Object>) parse(s); }
    @SuppressWarnings("unchecked")
    public static List<Object> arr(String s) { return (List<Object>) parse(s); }

    private static class Parser {
        final String s; int i = 0;
        Parser(String s) { this.s = s; }
        void skip() { while (i < s.length() && Character.isWhitespace(s.charAt(i))) i++; }
        Object value() {
            skip();
            if (i >= s.length()) return null;
            char c = s.charAt(i);
            if (c == '{') return object();
            if (c == '[') return array();
            if (c == '"') return string();
            if (c == 't' || c == 'f') return bool();
            if (c == 'n') { i += 4; return null; }
            return number();
        }
        Map<String,Object> object() {
            Map<String,Object> m = new LinkedHashMap<>();
            i++; skip();
            if (i < s.length() && s.charAt(i) == '}') { i++; return m; }
            while (i < s.length()) {
                skip(); String k = string(); skip();
                if (s.charAt(i) == ':') i++;
                Object v = value(); m.put(k, v); skip();
                if (i < s.length() && s.charAt(i) == ',') { i++; continue; }
                if (i < s.length() && s.charAt(i) == '}') { i++; break; }
            }
            return m;
        }
        List<Object> array() {
            List<Object> a = new ArrayList<>();
            i++; skip();
            if (i < s.length() && s.charAt(i) == ']') { i++; return a; }
            while (i < s.length()) {
                a.add(value()); skip();
                if (i < s.length() && s.charAt(i) == ',') { i++; continue; }
                if (i < s.length() && s.charAt(i) == ']') { i++; break; }
            }
            return a;
        }
        String string() {
            StringBuilder sb = new StringBuilder();
            i++; // opening "
            while (i < s.length()) {
                char c = s.charAt(i++);
                if (c == '"') return sb.toString();
                if (c == '\\' && i < s.length()) {
                    char e = s.charAt(i++);
                    switch (e) {
                        case '"': sb.append('"'); break;
                        case '\\': sb.append('\\'); break;
                        case '/': sb.append('/'); break;
                        case 'n': sb.append('\n'); break;
                        case 'r': sb.append('\r'); break;
                        case 't': sb.append('\t'); break;
                        case 'u':
                            sb.append((char) Integer.parseInt(s.substring(i, i + 4), 16));
                            i += 4; break;
                        default: sb.append(e);
                    }
                } else sb.append(c);
            }
            return sb.toString();
        }
        Boolean bool() {
            if (s.charAt(i) == 't') { i += 4; return true; }
            i += 5; return false;
        }
        Object number() {
            int start = i;
            if (s.charAt(i) == '-') i++;
            boolean dec = false;
            while (i < s.length() && "0123456789.eE+-".indexOf(s.charAt(i)) >= 0) {
                if (s.charAt(i) == '.' || s.charAt(i) == 'e' || s.charAt(i) == 'E') dec = true;
                i++;
            }
            String n = s.substring(start, i);
            return dec ? (Object) Double.parseDouble(n) : (Object) Long.parseLong(n);
        }
    }
}
