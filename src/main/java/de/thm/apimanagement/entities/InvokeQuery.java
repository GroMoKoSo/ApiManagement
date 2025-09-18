package de.thm.apimanagement.entities;

import java.util.Map;


public class InvokeQuery {
RequestType requestType;
String requestPath;
Map<String,String> header;
String body;
Map<String,String> requestParam;
String[] pathParam;

void format(){};
}
