package fr.devoxx;

import java.lang.reflect.Field;
import java.util.HashMap;

public class FindHashMapResizingThresholds {

    /*
    threshold: 12
    table :    16
    threshold: 24
    table :    32
    threshold: 48
    table :    64
    threshold: 96
    table :    128
    threshold: 192
    table :    256
    threshold: 384
    table :    512
    threshold: 768
    table :    1024
    threshold: 1536
    table :    2048
    threshold: 3072
    table :    4096
    threshold: 6144
    table :    8192
    threshold: 12288
    table :    16384
    threshold: 24576
    table :    32768
    threshold: 49152
    table :    65536
    threshold: 98304
    table :    131072
    threshold: 196608
    table :    262144
     */

    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
        Field threshold = HashMap.class.getDeclaredField("threshold");
        threshold.setAccessible(true);
        Field table = HashMap.class.getDeclaredField("table");
        table.setAccessible(true);
        HashMap<Object, Object> map = new HashMap<>();

        int previous = 0;
        for (int i = 0; i < 10_000_000; i++) {
            map.put(i, i);
            int o = (int) threshold.get(map);
            if(o != previous) {
                System.out.println("threshold: " + o + "| table :    " + ((Object[]) table.get(map)).length);
                System.out.println();
                previous = o;
            }
        }
    }
}
