package sample.myshop.utils;

import jakarta.servlet.http.HttpSession;
import sample.myshop.auth.SessionConst;

import java.util.UUID;

import static sample.myshop.auth.SessionConst.*;

public class Csrf {
    public static String generateToken(HttpSession session) {
        String token = (String) session.getAttribute(CSRF_TOKEN);

        if (token == null || token.isBlank()) {
            token = UUID.randomUUID().toString().replace("-", "");
            session.setAttribute(CSRF_TOKEN, token);
        }

        return token;
    }
}
