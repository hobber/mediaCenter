package main.data.datatypes;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;

public class MCList<DataSchemaObjectInterface> extends MCDatatype<LinkedList<DataSchemaObjectInterface>> implements Iterable<DataSchemaObjectInterface> {
  
  public MCList() {
    super(new LinkedList<DataSchemaObjectInterface>());
  }
  
  public MCList(LinkedList<DataSchemaObjectInterface> value) {
    super(value);
  }
  
  public boolean isEmpty() {
    return ((LinkedList<DataSchemaObjectInterface>)this.value).isEmpty();
  }

  public DataSchemaObjectInterface getFirst() {
    return ((LinkedList<DataSchemaObjectInterface>)this.value).getFirst();
  }
  
  public DataSchemaObjectInterface getLast() {
    return ((LinkedList<DataSchemaObjectInterface>)this.value).getLast();
  }
  
  public void addFirst(DataSchemaObjectInterface value) {
    ((LinkedList<DataSchemaObjectInterface>)this.value).addFirst(value);
  }
  
  public void addLast(DataSchemaObjectInterface value) {
    ((LinkedList<DataSchemaObjectInterface>)this.value).addLast(value);
  }
  
  public boolean add(DataSchemaObjectInterface value) {
    return this.value.add(value);
  }
  
  public void clear() {
    value.clear();
  }
  
  public int size() {
    return value.size();
  }
  
  public void sort(Comparator<DataSchemaObjectInterface> comparator) {
    Collections.sort((LinkedList<DataSchemaObjectInterface>)this.value, comparator);
  }

  @Override
  public Iterator<DataSchemaObjectInterface> iterator() {
    return ((LinkedList<DataSchemaObjectInterface>)this.value).iterator();
  }
}
