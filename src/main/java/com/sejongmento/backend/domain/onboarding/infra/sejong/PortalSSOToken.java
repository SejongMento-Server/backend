package com.sejongmento.backend.domain.onboarding.infra.sejong;

import org.jsoup.Connection;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PortalSSOToken extends Authenticator {
    @Override
    public AuthResponse authenticate(String id, String password) {
        try {
            headers.put("Referer","https://portal.sejong.ac.kr");
            Map<String,String> data = new HashMap<>();
            data.put("mainLogin","Y");
            data.put("rtUrl","blackboard.sejong.ac.kr");
            data.put("id", id);
            data.put("password", password);
            Connection.Response res = post("https://portal.sejong.ac.kr/jsp/login/login_action.jsp", data, null);
            if (res.statusCode() != 200) return unknownServerError(res.statusCode());
            String setCookie = res.header("Set-Cookie");
            boolean ssotoken = setCookie != null && setCookie.contains("ssotoken");
            String result = extractResult(res.body());
            if (ssotoken) {
                if ("OK".equals(result)) return success(Map.of(), 200);
                else return unknownIssue(200);
            } else {
                if ("erridpwd".equals(result) || "Error".equals(result))
                    return authFailed(false, Map.of("message","아이디 및 비밀번호가 일치하지 않습니다."), 200, result);
                else if ("pwsNeedChg".equals(result))
                    return authFailed(false, Map.of("message","일정 횟수 이상 패스워드를 잘못 입력하여 계정이 잠겼습니다."), 200, result);
                else if ("invalidDt".equals(result))
                    return authFailed(null, Map.of("message","접근 가능한 기간이 아닙니다."), 200, result);
                else if ("invalid".equals(result))
                    return authFailed(false, Map.of("message","제한된 아이디입니다."), 200, result);
                else if (result != null) return unknownServerError(200);
                else return unknownIssue(200);
            }
        } catch (java.net.SocketTimeoutException e) { return timeout(); }
        catch (Exception e) { return unknownIssue(200); }
    }
    private static String extractResult(String body) {
        Matcher m = Pattern.compile("var result = \'(.*)\';").matcher(body);
        if (m.find()) return m.group(1);
        return null;
    }
}
