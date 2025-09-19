package de.thm.apimanagement.entity;

import java.util.Map;

public class InvokeResult {

    int responseCode;
    Map<String,String> header;
    String body;
    void format(){}
}
