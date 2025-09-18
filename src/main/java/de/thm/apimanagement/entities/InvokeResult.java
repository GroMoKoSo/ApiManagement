package de.thm.apimanagement.entities;

import java.util.Map;

public class InvokeResult {

    int responseCode;
    Map<String,String> header;
    String body;
    void format(){}
}
