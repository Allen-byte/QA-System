package com.example.questions.interceptor;

import com.example.questions.common.IPUtils;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;

@Component
public class IPInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //IP
        String ip = IPUtils.getIpAddr(request);
        //考虑到如果对方是爬虫的可能，那么请求也许会很多，手动加分割线区分每次请求，后头看日志可以直观些
        System.out.println("["+new DateTime().toString("yyyy-MM-dd HH:mm:ss")+"]: ┌"+"──────────────────────────────────────────────────────────────────────────────────────────────");
        System.out.println("["+new DateTime().toString("yyyy-MM-dd HH:mm:ss")+"]: │"+"IP: "+ip);
        System.out.println("["+new DateTime().toString("yyyy-MM-dd HH:mm:ss")+"]: │"+"URI: "+request.getRequestURI());     // /index/getAll
        System.out.println("["+new DateTime().toString("yyyy-MM-dd HH:mm:ss")+"]: │"+"Method: "+request.getMethod());      //GET
        System.out.println("["+new DateTime().toString("yyyy-MM-dd HH:mm:ss")+"]: │"+"CharacterEncoding: "+request.getCharacterEncoding());    //UTF-8
        System.out.println("["+new DateTime().toString("yyyy-MM-dd HH:mm:ss")+"]: │"+"QueryString: "+request.getQueryString());    //请求中的查询条件 相当于请求参数 如: name=abc
        Enumeration<String> headers = request.getHeaderNames();
        //Header部分
        while (headers.hasMoreElements()){
            String headerName = headers.nextElement();
            String header = request.getHeader(headerName);
            System.out.println("["+new DateTime().toString("yyyy-MM-dd HH:mm:ss")+"]: │-"+headerName+": "+header);
        }
        System.out.println("["+new DateTime().toString("yyyy-MM-dd HH:mm:ss")+"]: └"+"──────────────────────────────────────────────────────────────────────────────────────────────");
        //该拦截器仅用于查看请求的信息内容，不做实际的拦截功能，所以直接return true放行
        return true;
    }
}

