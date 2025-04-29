package com.basestudy.rewards.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Embeddable
@NoArgsConstructor
public class Quantity {
    @Column(nullable = false, name = "total_quantity")
    private int total;
    @Column(nullable = false, name = "issued_quantity")
    private int issued;


    public Quantity(int total, int issued) {
        if (issued > total) throw new IllegalArgumentException("발급수량이 총 수량을 넘었습니다.");
        this.total = total;
        this.issued = issued;
    }

    public boolean isExhausted() {
        return issued >= total;
    }
}