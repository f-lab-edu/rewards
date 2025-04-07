package com.basestudy.rewards.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="coupon")
@EqualsAndHashCode(callSuper = false, of = "id")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Coupon {
    @Id
    @Column(name = "coupon_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false, name = "available_to")
    private LocalDateTime availableTo; //발급가능일자 종료

    @Column(nullable = false, name = "available_from")
    private LocalDateTime availableFrom; //발급가능일자 시작

    @Column(nullable = false, name = "total_amount")
    private int totalAmount; 
    //잔여갯수를 따로 관리안하면 매번 디비에서 조회해오는 상황일텐데 괜찮나
    //잔여갯수를 따로 관리해도 매번 디비에서 가져와야하는구나..그래도계산은 안하겠네
    //jpa에서 어떻게 못하나, 따로 entity로 빼서 가지고 있고 실제 디비에는 안가지고 있는 것도 가능한거아닌가
    //redis cache가 잔여갯수 카운트만 해줘도 되겠네
    // @Column(nullable = false)
    // private int remaining;

    @Column(nullable = false)
    private int useDays; //
}
