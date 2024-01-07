package com.springdb1.jdbc.service;

import com.springdb1.jdbc.domain.Member;
import com.springdb1.jdbc.repository.MemberRepositoryV1;
import com.springdb1.jdbc.repository.MemberRepositoryV2;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import javax.xml.crypto.Data;
import java.sql.SQLException;

import static com.springdb1.jdbc.connection.ConnectionConst.*;

/**
 * 트랜잭션 - 커넥션 파라미터 전달 방식 동기화
 */
@Slf4j
public class MemberServiceV2Test {

    public static final String MEMBER_A = "memberA";
    public static final String MEMBER_B = "memberB";
    public static final String MEMBER_EX = "ex";

    private MemberRepositoryV2 memberRepository; // *필드로 선언
    private MemberServiceV2 memberService;

    // @Test 전에 수행되는 메서드
    @BeforeEach
    void before(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        // *생성한 객체를 해당 필드에 초기화
        memberRepository = new MemberRepositoryV2(dataSource); // 의존성 주입1 (Repository는 DataSource가 필요하므로 의존성 주입해주면서 생성자를 통해 생성한 것)
        memberService = new MemberServiceV2(dataSource, memberRepository); // 의존성 주입2 (Service는 Repository를 사용하므로 의존성 주입해주면서 생성자를 통해 생성한 것)
    }

    // @Test 후에 수행되는 메서드
    @AfterEach
    void after() throws SQLException {
        memberRepository.delete(MEMBER_A);
        memberRepository.delete(MEMBER_B);
        memberRepository.delete(MEMBER_EX);
    }

    @Test
    @DisplayName("정상 이체")
    void accountTransfer() throws SQLException {
        // given : 이러한 데이터가 준비되어 있을 때
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberB = new Member(MEMBER_B, 10000);
        memberRepository.save(memberA); // A 1만원
        memberRepository.save(memberB); // B 1만원

        // when : 이러한 상황일 때 이걸 수행하면
        log.info("START TX");
        memberService.accountTransfer(memberA.getMemberId(), memberB.getMemberId(), 2000);
        log.info("END TX");

        // then : 이렇게 될 것이다. 즉, 검증 단계
        Member findMemberA = memberRepository.findById(memberA.getMemberId());
        Member findMemberB = memberRepository.findById(memberB.getMemberId());
        Assertions.assertThat(findMemberA.getMoney()).isEqualTo(8000); // ★ Assertions <- JUnit 말고 AssertJ 것을 사용하는 게 훨씬 편하다고 함. (by 김영한)
        Assertions.assertThat(findMemberB.getMoney()).isEqualTo(12000);// Assertion을 'static import' 하면 그냥 assertThat 메서드만 작성해도 사용 가능~
    }

    @Test
    @DisplayName("이체중 예외 발생")
    void accountTransferEx() throws SQLException {
        // given : 이러한 데이터가 준비되어 있을 때
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberEX = new Member(MEMBER_EX, 10000);
        memberRepository.save(memberA); // A 1만원
        memberRepository.save(memberEX); // B 1만원

        // when : 이러한 상황일 때 이걸 수행하면
        Assertions.assertThatThrownBy(() -> memberService.accountTransfer(memberA.getMemberId(), memberEX.getMemberId(), 2000))
                .isInstanceOf(IllegalStateException.class); // accountTransfer를 수행한 결과가 IllegalStateException라면


        // then : 이렇게 될 것이다. 즉, 검증 단계
        Member findMemberA = memberRepository.findById(memberA.getMemberId());
        Member findMemberB = memberRepository.findById(memberEX.getMemberId());
        Assertions.assertThat(findMemberA.getMoney()).isEqualTo(10000);
        Assertions.assertThat(findMemberB.getMoney()).isEqualTo(10000); // 이체중 예외가 발생하여 findMemberB의 돈은 12,000원이 아니라 10,000원이 되는 게 맞을 것이다.
    }

}
