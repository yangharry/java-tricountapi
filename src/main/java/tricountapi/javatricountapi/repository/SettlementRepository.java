package tricountapi.javatricountapi.repository;

import java.util.Optional;

import tricountapi.javatricountapi.model.Settlement;

public interface SettlementRepository {
    Settlement create(String name);

    void addParticipantToSettlemnet(Long settlementId, Long memberId);

    Optional<Settlement> findById(Long id);
}
