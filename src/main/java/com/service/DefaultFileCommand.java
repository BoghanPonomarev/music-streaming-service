package com.service;

import java.util.ArrayList;
import java.util.List;


public class DefaultFileCommand {

  private List<String> executionParameters = new ArrayList<>();

  public void addParameter(String parameter) {
    executionParameters.add(parameter);
  }

  public List<String> getParameters() {
    return executionParameters;
  }

}
