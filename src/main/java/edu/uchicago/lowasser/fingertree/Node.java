package edu.uchicago.lowasser.fingertree;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Iterables;

final class Node<E, T extends Container<E>> implements DeepContainer<E, T> {
  private final T[] contents;
  private final int length;

  public static <E, T extends Container<E>> Node<E, T> of(T a, T b) {
    return new Node<E, T>(checkNotNull(a), checkNotNull(b));
  }

  public static <E, T extends Container<E>> Node<E, T> of(T a, T b, T c) {
    return new Node<E, T>(checkNotNull(a), checkNotNull(b), checkNotNull(c));
  }

  public List<T> asList() {
    return Collections.unmodifiableList(Arrays.asList(contents));
  }

  @SuppressWarnings("unchecked")
  private Node(T a, T b) {
    this.contents = (T[]) new Container[] { a, b };
    this.length = a.length() + b.length();
  }

  @SuppressWarnings("unchecked")
  private Node(T a, T b, T c) {
    this.contents = (T[]) new Container[] { a, b, c };
    this.length = a.length() + b.length() + c.length();
  }

  @Override
  public final int length() {
    return length;
  }

  @Override
  public E index(int i) {
    for (T sub : contents) {
      int len = sub.length();
      if (i < len) {
        return sub.index(i);
      } else {
        i -= len;
      }
    }
    throw new IndexOutOfBoundsException();
  }

  @Override
  public T get(int i) {
    return contents[i];
  }

  @Override
  public int size() {
    return contents.length;
  }

  public Digit<E, T> asDigit() {
    return new Digit<E, T>(contents);
  }

  @Override
  public Iterator<E> iterator() {
    return Iterables.concat(contents).iterator();
  }
}
