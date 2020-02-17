package com.service.web.builder;

public interface ResponseBuilder<R,T> {

  R buildResponse(T parameter);

}
