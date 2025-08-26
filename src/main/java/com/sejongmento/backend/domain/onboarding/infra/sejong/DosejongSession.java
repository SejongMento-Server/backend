package com.sejongmento.backend.domain.onboarding.infra.sejong;

import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;

public class DosejongSession extends Authenticator {
    @Override
    public AuthResponse authenticate(String id, String password) {
        try {
            Map<String,String> cookies = new HashMap<>();
            Map<String,String> data = Map.of("email", id, "password", password);
            Connection.Response login = post("https://do.sejong.ac.kr/ko/process/member/login", data, cookies);
            cookies.putAll(login.cookies());
            Connection.Response home = get("https://do.sejong.ac.kr/", cookies);
            if (home.statusCode() != 200) return unknownServerError(home.statusCode());
            Document doc = home.parse();
            Elements info = doc.select("div.info");
            if (info.isEmpty()) return authFailed(false, null, 200, null);
            Element block = info.get(0);
            Element name = block.selectFirst("b");
            Element major = block.selectFirst("small");
            if (name == null || major == null) return unknownIssue(200);
            String majorStr = major.text().trim();
            String[] parts = majorStr.split("\s+");
            String majorPick = parts.length > 1 ? parts[1] : majorStr;
            return success(Map.of("name", name.text().trim(), "major", majorPick), 200);
        } catch (java.net.SocketTimeoutException e) { return timeout(); }
        catch (Exception e) { return unknownIssue(200); }
    }
}
