package hello.servlet.domain.member;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;


class MemberRepositoryTest {

    //junit4까지는 public class 써줘야하지만 5부터는 안써줘도딤 class만 쓰면 됨
    MemberRepository memberRepository = MemberRepository.getInstance();

    @AfterEach//각각 테스트가 실행된후 실행되는 코드, A테스트가 실행후 clear, B테스트가 실행후 clear 같은느낌임E
    void afterEach() {
        memberRepository.clearStore();
    }

    @Test
    void save(){
        //given

        Member member = new Member("hello", 20);


        //when

        Member savedMember = memberRepository.save(member);


        //then

        Member findMember = memberRepository.findById(savedMember.getId());
        assertThat(findMember).isEqualTo(savedMember);
    }
    @Test
    void findAll(){
        //given
        Member member1 = new Member("member1", 20);
        Member member2 = new Member("member2", 30);
        memberRepository.save(member1);
        memberRepository.save(member2);


        //when
        List<Member> result = memberRepository.findAll();

        //then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result).contains(member1,member2);
    }
}
