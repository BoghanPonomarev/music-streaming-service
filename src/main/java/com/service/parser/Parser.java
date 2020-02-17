package com.service.parser;

public interface Parser<R,T> {

  R parse(T parameter);

}
