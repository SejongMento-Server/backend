package com.sejongmento.backend.domain.onboarding.infra.sejong;

import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.Map;

public class MoodlerSession extends Authenticator {
    @Override
    public AuthResponse authenticate(String id, String password) {
        try {
            Map<String,String> data = Map.of("username", id, "password", password);
            Connection.Response res = post("https://sjulms.moodler.kr/login/index.php", data, null);
            if (res.statusCode() != 200) return unknownServerError(res.statusCode());
            Document doc = res.parse();
            Element userInfo = doc.selectFirst("div.user-info-picture");
            if (userInfo == null) return authFailed(false, null, 200, null);
            Element name = userInfo.selectFirst("h4");
            Element major = userInfo.selectFirst("p.department");
            if (name == null || major == null) return unknownIssue(200);
            return success(Map.of("name", name.text().trim(), "major", major.text().trim()), 200);
        } catch (java.net.SocketTimeoutException e) { return timeout(); }
        catch (Exception e) { return unknownIssue(200); }
    }
}
