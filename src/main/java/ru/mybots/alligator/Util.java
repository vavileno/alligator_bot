package ru.mybots.alligator;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

public class Util {

    public static void random() throws FileNotFoundException {
        PrintWriter p = new PrintWriter("c://temp//numbers.txt");
        Set<Long> ords = new HashSet<>();
        long n = 0;
        int i=3;
        for(; i<=35012; i++) {
            do {
                n = Math.round(Math.random()*35012);
            } while(ords.contains(n));
            ords.add(n);
            p.write("UPDATE WORD SET ORD = " + n + " WHERE ID = " + i + ";");
            p.write("\n");
            if(i%1000 == 0) {
                p.flush();
            }
        }
    }

}
