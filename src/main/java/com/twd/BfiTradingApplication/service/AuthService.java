package com.twd.BfiTradingApplication.service;
import com.twd.BfiTradingApplication.dto.ReqRes;

public interface AuthService {
    ReqRes signUp(ReqRes registrationRequest);
    ReqRes signIn(ReqRes signinRequest);
    ReqRes refreshToken(ReqRes refreshTokenRequest);
}