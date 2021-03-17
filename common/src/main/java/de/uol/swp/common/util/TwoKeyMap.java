package de.uol.swp.common.util;

import java.util.*;

public class TwoKeyMap<K1, K2, V> {

    private final List<Tuple3<K1, K2, V>> map = new LinkedList<>();

    public TwoKeyMap() {}

    public Map<K1, V> getKey1ValueMap() {
        Map<K1, V> returnMap = new LinkedHashMap<>();
        map.forEach(entry -> returnMap.put(entry.getValue1(), entry.getValue3()));
        return returnMap;
    }

    public List<V> getValues() {
        LinkedList<V> returnList = new LinkedList<>();
        map.forEach(entry -> returnList.add(entry.getValue3()));
        return returnList;
    }

    public Map<K2, V> getKey2ValueMap() {
        Map<K2, V> returnMap = new LinkedHashMap<>();
        map.forEach(entry -> returnMap.put(entry.getValue2(), entry.getValue3()));
        return returnMap;
    }

    public int size() {
        return map.size();
    }

    public Map<K1, K2> getKey1Key2Map() {
        Map<K1, K2> returnMap = new LinkedHashMap<>();
        map.forEach(entry -> returnMap.put(entry.getValue1(), entry.getValue2()));
        return returnMap;
    }

    public Map<K2, K1> getKey2Key1Map() {
        Map<K2, K1> returnMap = new LinkedHashMap<>();
        map.forEach(entry -> returnMap.put(entry.getValue2(), entry.getValue1()));
        return returnMap;
    }

    public V getWithKey1(K1 key1) {
        for (Tuple3<K1, K2, V> entry : map)
            if (entry.getValue1() == key1) return entry.getValue3();
        return null;
    }

    public V getWithKey2(K2 key2) {
        for (Tuple3<K1, K2, V> entry : map)
            if (entry.getValue2() == key2) return entry.getValue3();
        return null;
    }

    public void put(K1 key1, K2 key2, V value) {
        for (int i = 0; i < map.size(); i++) {
            Tuple3<K1, K2, V> entry = map.get(i);
            if ((entry.getValue1() == key1 && entry.getValue2() != key2) || (entry.getValue2() == key2 && entry.getValue1() != key1)) {
                throw new IllegalArgumentException("Keys are not matching!");
            } else if (entry.getValue1() == key1 && entry.getValue2() == key2) {
                map.set(i, new Tuple3<>(key1, key2, value));
                return;
            }
            map.add(new Tuple3<>(key1, key2, value));
        }
    }

    public K2 getKey2(K1 key) {
        return getKey1Key2Map().get(key);
    }

    public K1 getKey1(K2 key) {
        return getKey2Key1Map().get(key);
    }

    public K1[] getKey1Array() {
        List<K1> returnArray = new LinkedList<>();
        map.forEach((key -> returnArray.add(key.getValue1())));
        return (K1[]) returnArray.toArray();
    }
}
