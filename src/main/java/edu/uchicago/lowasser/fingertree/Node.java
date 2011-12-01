package edu.uchicago.lowasser.fingertree;

import static com.google.common.base.Preconditions.checkNotNull;

final class Node<E, T extends Container<E>> implements DeepContainer<E, T> {
  private final T[] contents;
  private final int length;

  public static <E, T extends Container<E>> Node<E, T> of(T a, T b) {
    return new Node<E, T>(checkNotNull(a), checkNotNull(b));
  }

  public static <E, T extends Container<E>> Node<E, T> of(T a, T b, T c) {
    return new Node<E, T>(checkNotNull(a), checkNotNull(b), checkNotNull(c));
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

  public final int length() {
    return length;
  }

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

  public T get(int i) {
    return contents[i];
  }

  public int size() {
    return contents.length;
  }

  public Digit<E, T> asDigit() {
    return new Digit<E, T>(contents, length);
  }
}
