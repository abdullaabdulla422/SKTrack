package com.signakey.sktrack;

import java.io.File;
import java.util.Comparator;

class StringRef {
    public String value;
}

@SuppressWarnings("rawtypes")
public class NumAlphaComparator implements Comparator {
    public int compare(Object o1, Object o2) {
    	if(o1 instanceof File && o2 instanceof File) {
            StringRef sr1 = new StringRef();
            StringRef sr2 = new StringRef();
            int i1 = leadingNumber(((File)o1).getName(), sr1);
            int i2 = leadingNumber(((File)o2).getName(), sr2);
            if (i1 < i2) return -1;
            if (i1 > i2) return 1;
            return sr1.value.compareTo(sr2.value);
    	}
    	else {
            throw new ClassCastException();
        }
    }

    int leadingNumber(String s, StringRef rest) {
        // if(s.length() == 0) ...
        int i = 0;
        char c = s.charAt(0);
        while(Character.isDigit(c)) {
            i = 10 * i + Character.digit(c, 10);
            s = s.substring(1);
            if(s.length() == 0) break;
            c = s.charAt(0);
        }
        rest.value = s;
        return i;
    }
}
