package com.sejongmento.backend.domain.onboarding.infra.sejong;

import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ClassicSession extends Authenticator {
    @Override
    public AuthResponse authenticate(String id, String password) {
        try {
            Map<String,String> cookies = new HashMap<>();
            Connection.Response first = get("http://classic.sejong.ac.kr/", cookies);
            cookies.putAll(first.cookies());
            Map<String,String> data = new HashMap<>();
            data.put("userId", id); data.put("password", password);
            Connection.Response loginRes = post("https://classic.sejong.ac.kr/userLogin.do", data, cookies);
            cookies.putAll(loginRes.cookies());
            Document doc = loginRes.parse();
            Element pTc = doc.selectFirst("p.tc");
            if (pTc != null) {
                String text = pTc.text().trim();
                if ("로그인 정보가 올바르지 않습니다.".equals(text)) return authFailed(false, Map.of("message","아이디 및 비밀번호가 일치하지 않습니다."), 200, null);
                else return unknownIssue(200);
            }
            Connection.Response infoRes = get("https://classic.sejong.ac.kr/userCertStatus.do?menuInfoId=MAIN_02_05", cookies);
            if (infoRes.statusCode() != 200) return unknownServerError(infoRes.statusCode());
            Document page = infoRes.parse();
            Elements soups = page.select("div.contentWrap > ul.tblA > li > dl > dd");
            if (soups.size() < 5) return unknownIssue(200);
            String major = soups.get(0).text().trim();
            String name = soups.get(2).text().trim();
            String grade = soups.get(3).text().trim();
            String status = soups.get(4).text().trim();
            Element table = page.selectFirst("table.listA");
            if (table == null) return unknownIssue(200);
            Elements keys = table.select("thead > tr > th");
            Elements values = table.select("tbody > tr > td");
            Map<String,Object> read = new LinkedHashMap<>();
            for (int i = 1; i <= 4 && i < keys.size() && i < values.size(); i++)
                read.put(keys.get(i).text().trim(), values.get(i).text().trim());
            Map<String,Object> body = new LinkedHashMap<>();
            body.put("major", major); body.put("name", name); body.put("grade", grade); body.put("status", status); body.put("read_certification", read);
            return success(body, 200);
        } catch (java.net.SocketTimeoutException e) { return timeout(); }
        catch (Exception e) { return unknownIssue(200); }
    }
}
