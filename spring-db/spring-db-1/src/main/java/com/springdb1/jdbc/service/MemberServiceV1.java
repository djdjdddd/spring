package com.springdb1.jdbc.service;

// 트랜잭션 - 적용1

// 실제 애플리케이션에서 DB 트랜잭션을 사용해서 계좌이체 같이 원자성이 중요한 비즈니스 로직을 어떻게 구현하는지 알아보자.
// 먼저 트랜잭션 없이 단순하게 계좌이체 비즈니스 로직만 구현해보자.

import com.springdb1.jdbc.domain.Member;
import com.springdb1.jdbc.repository.MemberRepositoryV1;
import lombok.RequiredArgsConstructor;

import java.sql.SQLException;

@RequiredArgsConstructor
public class MemberServiceV1 {

    private final MemberRepositoryV1 memberRepositoryV1;

    // 계좌이체
    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        // 계좌이체된 멤버를 조회한 후
        Member fromMember = memberRepositoryV1.findById(fromId);
        Member toMember = memberRepositoryV1.findById(toId);

        // 금액만큼을 더하고 뺀다.
        memberRepositoryV1.update(fromId, fromMember.getMoney() - money); // 이체한 사람
        validation(toMember); // 일부러 예외 발생 시키기 위한 메서드
        memberRepositoryV1.update(toId, fromMember.getMoney() + money); // 이체 받은 사람
    }

    private void validation(Member toMember){
        if(toMember.getMemberId().equals("ex")){
            throw new IllegalStateException("이체중 예외 발생");
        }
    }
}
