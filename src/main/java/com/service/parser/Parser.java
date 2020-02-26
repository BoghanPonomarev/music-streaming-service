package com.service.parser;

public interface Parser<T, R> {

  R parse(T parameter);

}
