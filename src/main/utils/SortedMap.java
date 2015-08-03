package main.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SortedMap<K, V> extends LinkedHashMap<K, V> {

  private static final long serialVersionUID = -4403248492684777601L;

  public void sortForKeys(final Comparator<K> comparator) {
    List<Map.Entry<K, V>> entries = new ArrayList<Map.Entry<K, V>>(entrySet());
    Collections.sort(entries, new Comparator<Map.Entry<K, V>>() {
      public int compare(Map.Entry<K, V> a, Map.Entry<K, V> b){
        return comparator.compare(a.getKey(), b.getKey());
      }
    });
    clear();
    for (Map.Entry<K, V> entry : entries)
      put(entry.getKey(), entry.getValue());
  }
  
  public void sortForValues(final Comparator<V> comparator) {
    List<Map.Entry<K, V>> entries = new ArrayList<Map.Entry<K, V>>(entrySet());
    Collections.sort(entries, new Comparator<Map.Entry<K, V>>() {
      public int compare(Map.Entry<K, V> a, Map.Entry<K, V> b){
        return comparator.compare(a.getValue(), b.getValue());
      }
    });
    clear();
    for (Map.Entry<K, V> entry : entries)
      put(entry.getKey(), entry.getValue());
  }
}
