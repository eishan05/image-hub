package com.eishanlawrence.firestore;

public enum ProjectVariables {
  PROJECT_ID("annular-welder-253418");

  final String value;

  ProjectVariables(String value) {
    this.value = value;
  }

  public String getValue() {
    return this.value;
  }
}
