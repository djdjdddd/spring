package com.springdb1.jdbc.service;

import com.springdb1.jdbc.domain.Member;
import com.springdb1.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.Connection;
import java.sql.SQLException;

// 트랜잭션 문제 해결 - 트랜잭션 템플릿

// 강의 요약
// - 비즈니스 로직 주변에 덕지덕지 붙어있는 트랜잭션 관련 코드들을 '트랜잭션 템플릿'이라는 클래스를 통해 획기적으로 바꿔보자.
// - '트랜잭션 템플릿'에 대한 자세한 설명은 스프링 고급편 강의에서 이어진다.

/**
 * 트랜잭션 - 트랜잭션 템플릿
 */
@Slf4j
public class MemberServiceV3_2 {

    // **트랜잭션 매니저를 직접 사용하지 않고, 트랜잭션 템플릿을 사용한다.
//    private final PlatformTransactionManager transactionManager;
    private final TransactionTemplate txTemplate;

    private final MemberRepositoryV3 memberRepository;

    public MemberServiceV3_2(PlatformTransactionManager transactionManager, MemberRepositoryV3 memberRepository){
        this.txTemplate = new TransactionTemplate(transactionManager); // **대신 생성할 때 이러한 패턴으로 많이 사용한다.
        this.memberRepository = memberRepository;
    }

    // 계좌이체
    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        // 새 코드 - 트랜잭션 템플릿
        // cf. 리턴값이 없을 땐 executeWithoutResult, 리턴값이 있을 땐 execute
        txTemplate.executeWithoutResult((status) -> {
            // 비즈니스 로직
            try {
                bizLogic(fromId, toId, money);
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        });
        // 트랜잭션 매니저를 사용하는 대략 이렇다는 정도로 알아두자. 자세한 내용은 고급편 강의에서 이어진다.
        // ★어쨌든 중요한 건 '트랜잭션 템플릿 덕분에 트랜잭션을 시작하고, 커밋하거나 롤백하는 코드가 모두 제거되었다'는 사실이다.
        // ★트랜잭션 템플릿의 기본 동작은 다음과 같다.
        // - 비즈니스 로직이 정상 수행되면 커밋
        // - 런타임 예외가 발생하면 롤백. 그 외엔 커밋 (즉, 체크 예외일 땐 커밋한다. 물론 설정을 바꿀 수 있다)


        // [정리]
        // 트랜잭션 템플릿 덕분에 트랜잭션을 사용할 때 필요한 코드량이 줄긴 했지만... 여전히 문제가 남아있다.
        // ★서비스 로직이라 비즈니스 로직만 있으면 좋겠는데, 계속 트랜잭션 관련 로직이 섞여있다는 것이다.
        // 서비스 입장에서 비즈니스 로직은 '핵심 기능'이고, 트랜잭션은 '부가 기능'이다. 두 관심사를 하나의 클래스에서 처리하게 되면 결과적으로 코드 유지보수가 어렵다.
        // 어떻게 해결할 수 있을까?


        // 기존 코드 - 트랜잭션 매니저
//        // 트랜잭션 시작
//        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
//        try {
//            // 비즈니스 로직
//            bizLogic(fromId, toId, money);
//            transactionManager.commit(status); // 성공시 커밋
//        }catch (Exception e){
//            transactionManager.rollback(status); // 실패시 롤백
//            throw new IllegalStateException(e);
//        }
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
