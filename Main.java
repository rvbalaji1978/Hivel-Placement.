import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.*;
public class Main {
    static BigInteger fromBase(String s, int base) {
        s = s.toLowerCase(Locale.ROOT);
        BigInteger b = BigInteger.valueOf(base);
        BigInteger res = BigInteger.ZERO;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            int v;
            if (c >= '0' && c <= '9') v = c - '0';
            else v = 10 + (c - 'a');
            res = res.multiply(b).add(BigInteger.valueOf(v));
        }
        return res;
    }
    static String readFile(String path) throws Exception {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
        }
        return sb.toString();
    }
    static String strip(String s) {
        return s.trim();
    }
    public static void main(String[] args) throws Exception {
        String json = readFile(args[0]);
        json = json.trim();
        if (json.startsWith("{")) json = json.substring(1);
        if (json.endsWith("}")) json = json.substring(0, json.length() - 1);

        Map<String,String> fields = new HashMap<>();
        int depth = 0;
        int start = 0;
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '{') depth++;
            else if (c == '}') depth--;
            else if (c == ',' && depth == 0) {
                String part = json.substring(start, i);
                int colon = part.indexOf(':');
                String key = part.substring(0, colon).trim();
                String val = part.substring(colon + 1).trim();
                fields.put(key, val);
                start = i + 1;
            }
        }
        String last = json.substring(start);
        int colon = last.indexOf(':');
        String keyLast = last.substring(0, colon).trim();
        String valLast = last.substring(colon + 1).trim();
        fields.put(keyLast, valLast);
        String keysObj = fields.get("\"keys\"");
        keysObj = keysObj.trim();
        if (keysObj.startsWith("{")) keysObj = keysObj.substring(1);
        if (keysObj.endsWith("}")) keysObj = keysObj.substring(0, keysObj.length() - 1);
        Map<String,String> keysFields = new HashMap<>();
        depth = 0;
        start = 0;
        for (int i = 0; i < keysObj.length(); i++) {
            char c = keysObj.charAt(i);
            if (c == '{') depth++;
            else if (c == '}') depth--;
            else if (c == ',' && depth == 0) {
                String part = keysObj.substring(start, i);
                int cidx = part.indexOf(':');
                String k = part.substring(0, cidx).trim();
                String v = part.substring(cidx + 1).trim();
                keysFields.put(k, v);
                start = i + 1;
            }
        }
        String lastK = keysObj.substring(start);
        int cidx2 = lastK.indexOf(':');
        String k2 = lastK.substring(0, cidx2).trim();
        String v2 = lastK.substring(cidx2 + 1).trim();
        keysFields.put(k2, v2);
        int n = Integer.parseInt(keysFields.get("\"n\""));
        int kVal = Integer.parseInt(keysFields.get("\"k\""));
        List<BigInteger> xs = new ArrayList<>();
        List<BigInteger> ys = new ArrayList<>();
        for (int i = 1; i <= n && xs.size() < kVal; i++) {
            String idxKey = "\"" + i + "\"";
            if (!fields.containsKey(idxKey)) continue;
            String obj = fields.get(idxKey).trim();
            if (obj.startsWith("{")) obj = obj.substring(1);
            if (obj.endsWith("}")) obj = obj.substring(0, obj.length() - 1);
            Map<String,String> kv = new HashMap<>();
            depth = 0;
            start = 0;
            for (int j = 0; j < obj.length(); j++) {
                char c = obj.charAt(j);
                if (c == '{') depth++;
                else if (c == '}') depth--;
                else if (c == ',' && depth == 0) {
                    String part = obj.substring(start, j);
                    int ci = part.indexOf(':');
                    String kk = part.substring(0, ci).trim();
                    String vv = part.substring(ci + 1).trim();
                    kv.put(kk, vv);
                    start = j + 1;
                }
            }
            String lastP = obj.substring(start);
            int ci2 = lastP.indexOf(':');
            String kk2 = lastP.substring(0, ci2).trim();
            String vv2 = lastP.substring(ci2 + 1).trim();
            kv.put(kk2, vv2);
            String baseStr = kv.get("\"base\"").trim();
            if (baseStr.startsWith("\"")) baseStr = baseStr.substring(1, baseStr.length() - 1);
            String valStr = kv.get("\"value\"").trim();
            if (valStr.startsWith("\"")) valStr = valStr.substring(1, valStr.length() - 1);
            int base = Integer.parseInt(baseStr);
            BigInteger x = BigInteger.valueOf(i);
            BigInteger y = fromBase(valStr, base);
            xs.add(x);
            ys.add(y);
        }
        int m = xs.size();
        BigInteger num = BigInteger.ZERO;
        BigInteger den = BigInteger.ONE;
        for (int i = 0; i < m; i++) {
            BigInteger xi = xs.get(i);
            BigInteger yi = ys.get(i);
            BigInteger ln = BigInteger.ONE;
            BigInteger ld = BigInteger.ONE;
            for (int j = 0; j < m; j++) {
                if (i == j) continue;
                BigInteger xj = xs.get(j);
                BigInteger nterm = xj.negate();
                BigInteger dterm = xi.subtract(xj);
                BigInteger g1 = nterm.gcd(dterm);
                nterm = nterm.divide(g1);
                dterm = dterm.divide(g1);
                ln = ln.multiply(nterm);
                ld = ld.multiply(dterm);
            }
            BigInteger cn = yi.multiply(ln);
            BigInteger g2 = cn.gcd(ld);
            cn = cn.divide(g2);
            BigInteger cd = ld.divide(g2);
            num = num.multiply(cd).add(cn.multiply(den));
            den = den.multiply(cd);
            BigInteger g3 = num.gcd(den);
            num = num.divide(g3);
            den = den.divide(g3);
        }
        BigInteger c = num.divide(den);
        System.out.println(c.toString());
    }
}
