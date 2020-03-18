package com.service.stream.parser;

public interface PlaylistParser<T, R> {

  R parse(T parameter);

}
