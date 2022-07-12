package hello.servlet.domain.member;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemberRepository {
    //static으로 생성되었기때문에 MemberRepository가 아무리 많이 생성되어도
    //store와 long은 하나만 생성됨
    private static Map<Long,Member> store = new HashMap<>();
    private static long sequence=0L;

    //https://readystory.tistory.com/116해당 링크를 보면 왜 자기 자신에서 new를 하는지 알 수 있음
    //클래스 로딩 단계에서 new를 해서 자기 자신을(MemberRespository) 생성하고 전역화시킴
    //남이 못생성하게 private로 생성자를 막아버리는거임
    //그리고 store나 sequence를 get이나 set이 아닌 다른걸로 못바꾸게 private로 잠궈버리고 어디서든 쓸 수 있게 전역화함(싱글톤이기 때문에 어디서든 사용할 수 있어야함
    //static을 store나 sequence에 선언을 안해주면 memberRepository.store나 memberRepository.sequence로 사용해야하는데
    //static을 넣으면 MemberRepository클래스만 사용하려는 class에 import해주면 sequence= 1L이런식으로 사용 가능한것 같음, 하지만 MemberRepository.java에서는
    //싱글톤을 구현하기위해 private로 잠궈버렸기때문에 그렇게 사용 못하게 막음, sequence와 store를 보호하기위해, memberRepository가 제공하는 메서드를 통해
    //수정하는것을 강제하기위해 잠금
    private static final MemberRepository instance = new MemberRepository();

    public static MemberRepository getInstance(){
        return instance;
    }
    private MemberRepository(){
    }
    public Member save(Member member){
        member.setId(++sequence);
        store.put(member.getId(), member);
        return member;
    }
    public Member findById(Long id){
        return store.get(id);
    }
    public List<Member> findAll(){
        return new ArrayList<>(store.values());

    }
    public void clearStore(){
        store.clear();
    }

}
