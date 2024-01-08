package com.springdb1.jdbc.service;

import com.springdb1.jdbc.domain.Member;
import com.springdb1.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.sql.Connection;
import java.sql.SQLException;

// 트랜잭션 문제 해결 - 트랜잭션 매니저1

/**
 * 트랜잭션 - 트랜잭션 매니저
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV3_1 {

//    private final DataSource dataSource; // 데이터소스 대신 트랜잭션 매니저를 사용. 왜냐하면 Service layer(서비스 계층)와 관계없고 DB 접근 계층과 관련된 코드이므로
    private final PlatformTransactionManager transactionManager; // = new DataSourceTransactionManager(); - ★주의! 이렇게 초기화하면 의존성 주입(DI) 방식으로 할 수 없다. 즉, OCP가 제대로 지켜지지 않게 되는 것이다.

    private final MemberRepositoryV3 memberRepository;

    // 계좌이체
    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        // 트랜잭션 시작
//        Connection con = dataSource.getConnection();
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            // 비즈니스 로직
            bizLogic(fromId, toId, money);

            transactionManager.commit(status); // 성공시 커밋
        }catch (Exception e){
            transactionManager.rollback(status); // 실패시 롤백
            throw new IllegalStateException(e);
        }
    }

    private void bizLogic(String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);

        memberRepository.update(fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(toId, fromMember.getMoney() + money);
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
