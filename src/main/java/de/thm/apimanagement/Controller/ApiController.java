package de.thm.apimanagement.Controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
@Controller
public class ApiController {

    ApiService apiService;

    Api[] getApis(){}

    void addApi(api:Api){}

    Api getApi(apiId:int){}

    Api editApi(apiId)
}
