package com.sejongmento.backend.domain.onboarding.infra.sejong;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.util.HashMap;
import java.util.Map;

public abstract class Authenticator {
    protected final Map<String,String> headers = new HashMap<>();
    protected final int timeoutMs;
    public Authenticator(){ this(SejongAuthProps.TIMEOUT_MS_DEFAULT); }
    public Authenticator(int timeoutMs){ this.timeoutMs = timeoutMs; headers.put("User-Agent", SejongAuthProps.USER_AGENT_DEFAULT); }

    protected Connection.Response post(String url, Map<String,String> data, Map<String,String> cookies) throws Exception {
        var conn = Jsoup.connect(url).method(Connection.Method.POST).ignoreContentType(true).timeout(timeoutMs).headers(headers).data(data);
        if(cookies!=null && !cookies.isEmpty()) conn.cookies(cookies);
        return conn.execute();
    }
    protected Connection.Response get(String url, Map<String,String> cookies) throws Exception {
        var conn = Jsoup.connect(url).method(Connection.Method.GET).ignoreContentType(true).timeout(timeoutMs).headers(headers);
        if(cookies!=null && !cookies.isEmpty()) conn.cookies(cookies);
        return conn.execute();
    }

    public abstract AuthResponse authenticate(String id, String password);

    protected AuthResponse success(Map<String,Object> body, int statusCode){ return new AuthResponse(true,true,statusCode,"success",body,this.getClass().getSimpleName()); }
    protected AuthResponse authFailed(Boolean isAuth, Map<String,Object> body, int statusCode, String prefix) {
        String code = (prefix!=null && !prefix.isBlank()) ? prefix + "_auth_failed" : "auth_failed";
        if(body==null) body = Map.of("message","계정 정보가 잘못되었거나, 인증 포맷 자체에 문제가 있습니다.");
        return new AuthResponse(true,isAuth,statusCode,code,body,this.getClass().getSimpleName());
    }
    protected AuthResponse unknownIssue(int statusCode){ return new AuthResponse(true,null,statusCode,"unknown_issue",Map.of("message","모듈이 예상한 포맷과 다릅니다."),this.getClass().getSimpleName()); }
    protected AuthResponse unknownServerError(int statusCode){ return new AuthResponse(false,null,statusCode,"unknown_server_error",Map.of("message","인증 서버 오류"),this.getClass().getSimpleName()); }
    protected AuthResponse timeout(){ return new AuthResponse(false,null,null,"timeout",Map.of("message","Timeout"),this.getClass().getSimpleName()); }
}
