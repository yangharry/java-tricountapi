package tricountapi.javatricountapi.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import tricountapi.javatricountapi.model.Settlement;
import tricountapi.javatricountapi.model.Member;

@Repository
@RequiredArgsConstructor
public class SettlementRepositoryImpl implements SettlementRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Settlement create(String name) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName("settlement").usingGeneratedKeyColumns("id");

        Map<String, Object> params = new HashMap<>();
        params.put("name", name);

        Number key = jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(params));

        Settlement settlement = new Settlement();
        settlement.setId(key.longValue());
        settlement.setName(name);

        return settlement;

    }

    @Override
    public void addParticipantToSettlemnet(Long settlementId, Long memberId) {
        jdbcTemplate.update("INSERT INTO settlement_participant (settlement_id, member_id) VALUES (?, ?)", settlementId,
                memberId);
    }

    @Override
    public Optional<Settlement> findById(Long id) {
        List<Settlement> result = jdbcTemplate.query("select * from settlement "
                + "join settlement_participant on settlement.id = settlement_participant.settlement_id "
                + "join member on settlement_participant.member_id = settlement_participant.member_id = member_id "
                + "where settlement.id = ?", settlementParticipantsRowMapper(), id);
        return result.stream().findAny();
    }

    private RowMapper<Settlement> settlementParticipantsRowMapper() {
        return ((rs, rowNum) -> {
            Settlement settlement = new Settlement();
            settlement.setId(rs.getLong("settlement.id"));
            settlement.setName(rs.getString("settlement.name"));

            // List 만들기
            List<Member> participants = new ArrayList<>();
            do {
                Member participant = new Member(
                        rs.getLong("member.id"),
                        rs.getString("member.login_id"),
                        rs.getString("member.name"),
                        rs.getString("member.password"));
                participants.add(participant);
            } while (rs.next());

            settlement.setParticipants(participants);
            return settlement;
        });
    }
}
