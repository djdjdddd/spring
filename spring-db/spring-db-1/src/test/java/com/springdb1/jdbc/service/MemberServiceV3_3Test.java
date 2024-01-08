package com.springdb1.jdbc.service;

import com.springdb1.jdbc.domain.Member;
import com.springdb1.jdbc.repository.MemberRepositoryV3;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.sql.SQLException;

import static com.springdb1.jdbc.connection.ConnectionConst.*;

/**
 * 트랜잭션 - 트랜잭션 매니저
 */
@Slf4j
@SpringBootTest
public class MemberServiceV3_3Test {

    public static final String MEMBER_A = "memberA";
    public static final String MEMBER_B = "memberB";
    public static final String MEMBER_EX = "ex";

    @Autowired
    private MemberRepositoryV3 memberRepository;
    @Autowired
    private MemberServiceV3_3 memberService;

    // ★★★★★
    // 기존 코드 그대로 Test를 진행하면 '이체중 예외 발생'에서 테스트가 실패하는 걸 볼 수 있다.
    // ★★★트랜잭션과 같은 AOP를 사용하기 위해선... 더 나아가 (단순히 자바 코드 테스트가 아니라)스프링의 기능을 제대로 사용하기 위해선 스프링 Bean으로 등록하는 절차가 필요하다.
    // cf. @BeforeEach에 있는 코드처럼 직접 new해서 의존성을 주입해주는 것으로는 한계가 있다. 스프링이 Bean, Config를 인식해서 세팅할 수 있도록 코드를 짜야 한다.

    // **
    // 1. 우선 @SpringBootTest - 스프링 AOP를 적용하려면 스프링 컨테이너가 필요하다. 이 애노테이션이 있으면 테스트시 스프링 부트를 통해 스프링 컨테이너를 생성한다.
    //                          그리고 테스트에서 @Autowired 등을 통해 스프링 컨테이너가 관리하는 빈들을 사용할 수 있다.
    // 2. @Autowired - 의존관계 주입을 위해
    // 3. @TestConfiguration - Bean 등록을 위한 Configuration 애노테이션인데... Test용이라 @TestConfiguration 인가 보다? -> 이제 @BeforeEach에 해당하는 코드는 필요 없다.

    @TestConfiguration
    static class TestConfig{
        @Bean
        DataSource dataSource(){
            return new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        }
        @Bean
        PlatformTransactionManager transactionManager(){
            return new DataSourceTransactionManager(dataSource());
        }
        @Bean
        MemberRepositoryV3 memberRepositoryV3(){
            return new MemberRepositoryV3(dataSource());
        }
        @Bean
        MemberServiceV3_3 memberServiceV3_3(){
            return new MemberServiceV3_3(memberRepositoryV3());
        }
    }

    /*
    // @Test 전에 수행되는 메서드
    @BeforeEach
    void before(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        memberRepository = new MemberRepositoryV3(dataSource);

        PlatformTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
        memberService = new MemberServiceV3_3(memberRepository);
    }
    */

    // @Test 후에 수행되는 메서드
    @AfterEach
    void after() throws SQLException {
        memberRepository.delete(MEMBER_A);
        memberRepository.delete(MEMBER_B);
        memberRepository.delete(MEMBER_EX);
    }

    // 실제로 AOP가 적용되었는지 체크해보자.
    // 체크 결과 -> 서비스(MemberServiceV3_3) 객체가 아니라 프록시 객체(MemberServiceV3_3$$SpringCGLIB$$0)가 사용됐음을 알 수 있다!!
    // ★★★해석 : @Transactional이 붙어있어서 스프링이 빈을 등록할 때 서비스가 아니라 "저 서비스에 AOP를 적용한 프록시 객체를 스프링 빈으로 등록한 것"이다. (즉, @Autowired 해서 얻은 빈이 프록시 객체이다)
    @Test
    void AopCheck(){
        log.info("memberService class={}", memberService.getClass()); // MemberServiceV3_3$$SpringCGLIB$$0
        log.info("memberRepository class={}", memberRepository.getClass()); // MemberRepositoryV3

        // ★코드로 AopProxy 객체인지 판단하는 방법
        Assertions.assertThat(AopUtils.isAopProxy(memberService)).isTrue();
        Assertions.assertThat(AopUtils.isAopProxy(memberRepository)).isFalse();
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
