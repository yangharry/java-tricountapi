package tricountapi.javatricountapi.controller;

import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import tricountapi.javatricountapi.dto.LoginRequest;
import tricountapi.javatricountapi.dto.SignupRequest;
import tricountapi.javatricountapi.model.ApiResponse;
import tricountapi.javatricountapi.model.Member;
import tricountapi.javatricountapi.service.MemberService;
import tricountapi.javatricountapi.util.TricountApiConst;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final MemberService memberService;

    // 회원가입
    @PostMapping("/signup")
    public ApiResponse<Member> signup(@Valid @RequestBody SignupRequest request) {
        Member member = Member.builder().loginId(request.getLoginId()).password(request.getPassword())
                .name(request.getName()).build();
        return new ApiResponse<Member>().ok(memberService.signup(member));
    }

    // 로그인
    @PostMapping("/login")
    public ApiResponse<Void> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request,
            HttpServletResponse response) {
        Member loginMember = memberService.login(loginRequest.getLoginId(), loginRequest.getPassword());

        // 로그인 성공 처리 - 쿠키 생성
        Cookie idCookie = new Cookie(TricountApiConst.LOGIN_MEMBER_COOKIE, String.valueOf(loginMember.getId()));
        response.addCookie(idCookie);

        return new ApiResponse<Void>().ok();
    }

    // 로그아웃
    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletResponse response) {
        memberService.logout(response);
        return new ApiResponse<Void>().ok();
    }
}
