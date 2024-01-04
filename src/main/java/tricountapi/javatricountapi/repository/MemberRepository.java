package tricountapi.javatricountapi.repository;

import java.util.Optional;

import tricountapi.javatricountapi.model.Member;

public interface MemberRepository {
    Member save(Member member);

    Optional<Member> findMyId(Long id);

    Optional<Member> findByLoginId(String loginId);
}
