package de.thm.apimanagement.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {

    ApiService apiService;

    Api[] getApis(){}

    void addApi(Api api){}

    Api getApi(int apiId){}

    Api editApi(int apiId){}

    void deleteApi(int apiId){}

    InvokeResult invoke(int apiId, InvokeQuery query){}
}
