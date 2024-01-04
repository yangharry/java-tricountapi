package tricountapi.javatricountapi.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import tricountapi.javatricountapi.enums.TricountApiErrorCode;
import tricountapi.javatricountapi.model.Member;
import tricountapi.javatricountapi.repository.MemberRepository;
import tricountapi.javatricountapi.util.TricountApiConst;
import tricountapi.javatricountapi.util.TricountApiException;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    // 회원가입
    public Member signup(Member member) {
        return memberRepository.save(member);
    }

    // 로그인
    public Member login(String loginId, String passsword) {
        Member loginMember = memberRepository.findByLoginId(loginId).filter(m -> m.getPassword().equals(passsword))
                .orElseThrow(
                        () -> new TricountApiException("member info is not found", TricountApiErrorCode.NOT_FOUND));
        return loginMember;
    }

    // 로그아웃
    public void logout(HttpServletResponse response) {
        expireCookie(response, TricountApiConst.LOGIN_MEMBER_COOKIE);
    }

    private void expireCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    public Member findMemberById(Long memberId) {
        Optional<Member> loginMember = memberRepository.findMyId(memberId);
        if (!loginMember.isPresent()) {
            throw new TricountApiException("Member info is not found!", TricountApiErrorCode.NOT_FOUND);
        }

        return loginMember.get();
    }
}
