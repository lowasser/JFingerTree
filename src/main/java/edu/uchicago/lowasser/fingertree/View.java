package edu.uchicago.lowasser.fingertree;

import static com.google.common.base.Preconditions.checkNotNull;

final class View<V, C> {
  public static <V, C> View<V, C> of(V end, C remainder) {
    return new View<V, C>(end, remainder);
  }

  private final V end;
  private final C remainder;

  private View(V end, C remainder) {
    this.end = checkNotNull(end);
    this.remainder = checkNotNull(remainder);
  }

  public V getEnd() {
    return end;
  }

  public C getRemainder() {
    return remainder;
  }
}
