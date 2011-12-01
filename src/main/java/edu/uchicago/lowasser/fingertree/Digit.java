package edu.uchicago.lowasser.fingertree;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;

import com.google.common.base.Optional;

final class Digit<E, T extends Container<E>> implements MetaContainer<E, T> {
  public static <E, T extends Container<E>> Digit<E, T> of(T a) {
    return new Digit<E, T>(a);
  }

  public static <E, T extends Container<E>> Digit<E, T> of(T a, T b) {
    return new Digit<E, T>(a, b);
  }

  public static <E, T extends Container<E>> Digit<E, T> of(T a, T b, T c) {
    return new Digit<E, T>(a, b, c);
  }

  public static <E, T extends Container<E>> Digit<E, T> of(T a, T b, T c, T d) {
    return new Digit<E, T>(a, b, c, d);
  }

  private final T[] contents;
  private final int length;

  @SuppressWarnings("unchecked")
  private Digit(T a) {
    this((T[]) new Container[] { a }, a.length());
  }

  @SuppressWarnings("unchecked")
  private Digit(T a, T b) {
    this((T[]) new Container[] { a, b }, a.length() + b.length());
  }

  @SuppressWarnings("unchecked")
  private Digit(T a, T b, T c) {
    this((T[]) new Container[] { a, b, c }, a.length() + b.length() + c.length());
  }

  @SuppressWarnings("unchecked")
  private Digit(T a, T b, T c, T d) {
    this((T[]) new Container[] { a, b, c, d }, a.length() + b.length() + c.length() + d.length());
  }

  Digit(T[] contents, int length) {
    checkArgument(contents.length >= 1 && contents.length <= 4);
    for (T t : contents) {
      checkNotNull(t);
    }
    this.contents = contents;
    this.length = length;
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

  public int length() {
    return length;
  }

  public T get(int i) {
    return contents[i];
  }

  public int size() {
    return contents.length;
  }

  public Optional<Digit<E, T>> tail() {
    switch (contents.length) {
      case 1:
        return Optional.absent();
      default:
        int newLen = length - contents[0].length();
        T[] newContents = Arrays.copyOfRange(contents, 1, contents.length);
        return Optional.of(new Digit<E, T>(newContents, newLen));
    }
  }

  public Optional<Digit<E, T>> init() {
    switch (contents.length) {
      case 1:
        return Optional.absent();
      default:
        int last = contents.length - 1;
        int newLen = length - contents[last].length();
        T[] newContents = Arrays.copyOfRange(contents, 0, last);
        return Optional.of(new Digit<E, T>(newContents, newLen));
    }
  }
}
