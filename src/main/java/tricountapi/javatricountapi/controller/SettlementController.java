package tricountapi.javatricountapi.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import tricountapi.javatricountapi.model.ApiResponse;
import tricountapi.javatricountapi.model.Settlement;
import tricountapi.javatricountapi.service.SettlementService;
import tricountapi.javatricountapi.util.MemberContext;

@RestController
@RequiredArgsConstructor
public class SettlementController {
    private final SettlementService settlementService;

    @PostMapping("/settles/create")
    public ApiResponse<Settlement> createSettlement(@RequestParam String settlementName) {
        return new ApiResponse<Settlement>()
                .ok(settlementService.createAndJoiSettlement(settlementName, MemberContext.getCurrentMember()));
    }

    @PostMapping("/settles/{id}/join")
    public ApiResponse<Void> joinSettlement(@PathVariable("id") Long settlementId) {
        settlementService.joinSettlement(settlementId, MemberContext.getCurrentMember().getId());
        return new ApiResponse<Void>().ok();
    }
}
