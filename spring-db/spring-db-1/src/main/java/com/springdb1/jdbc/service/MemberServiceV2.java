package com.springdb1.jdbc.service;

// 트랜잭션 - 적용1

// 실제 애플리케이션에서 DB 트랜잭션을 사용해서 계좌이체 같이 원자성이 중요한 비즈니스 로직을 어떻게 구현하는지 알아보자.
// 먼저 트랜잭션 없이 단순하게 계좌이체 비즈니스 로직만 구현해보자.

import com.springdb1.jdbc.domain.Member;
import com.springdb1.jdbc.repository.MemberRepositoryV1;
import com.springdb1.jdbc.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 트랜잭션 - 파라미터 연동, 풀을 고려한 종료
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV2 {

    private final DataSource dataSource; // **커넥션을 파라미터로 직접 넘겨야 하므로 dataSource가 필요하다. getConnection() 해야 돼서 ㅎㅎ..
    private final MemberRepositoryV2 memberRepository;

    // 계좌이체
    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        Connection con = dataSource.getConnection(); // **

        try {
            con.setAutoCommit(false); // 트랜잭션 시작 <- auto commit을 false로 함으로써 트랜잭션이 시작되는 것이다.

            // 비즈니스 로직
            bizLogic(con,  fromId, toId, money);

            con.commit(); // 성공시 커밋
        }catch (Exception e){
            con.rollback(); // 실패시 롤백
            throw new IllegalStateException(e);
        }finally {
            release(con);
        }

    }

    private void bizLogic(Connection con, String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(con, fromId);
        Member toMember = memberRepository.findById(con, toId);

        memberRepository.update(con, fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(con, toId, fromMember.getMoney() + money);
    }

    private static void release(Connection con) {
        if(con != null){
            try{
                con.setAutoCommit(true); // 커넥션 풀 고려 (CP를 사용하는 경우 해당 커넥션이 다시 풀로 돌아갈텐데, 내가 위에서 오토커밋을 꺼놨으므로 다시 켜주고 돌아가게끔 해야 한다. 안 그러면 꺼진 상태로 돌아가겠죠?)
                con.close();
            }catch (Exception e){
                log.info("error", e); // cf. 일반적인 파라미터와 달리 예외의 경우 출력할 때 {}가 필요없다. 
            }
        }
    }

    private void validation(Member toMember){
        if(toMember.getMemberId().equals("ex")){
            throw new IllegalStateException("이체중 예외 발생");
        }
    }
}
