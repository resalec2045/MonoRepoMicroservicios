package com.example.notification.service;

import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class TemplateEngine {
  public String render(String template, Map<String,String> vars){
    String result = template;
    if(vars!=null){
      for(var e: vars.entrySet()){
        result = result.replace("{{"+e.getKey()+"}}", e.getValue());
      }
    }
    return result;
  }
}
