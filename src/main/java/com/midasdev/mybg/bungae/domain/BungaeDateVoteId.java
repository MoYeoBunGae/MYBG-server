package com.midasdev.mybg.bungae.domain;

import jakarta.persistence.Embeddable;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BungaeDateVoteId {

    private Long voterId;
    private Long dateOptionId;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BungaeDateVoteId that = (BungaeDateVoteId) o;
        return Objects.equals(voterId, that.voterId) &&
                Objects.equals(dateOptionId, that.dateOptionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(voterId, dateOptionId);
    }
}
