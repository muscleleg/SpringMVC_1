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
   // 그리고 store와 sequence를 static한 이유는 여러곳에서 한 데이터를 사용하게 하려는것도 있지만
    //어처피 싱글통으로 생성하기때문에 여기서는 그 의미가 사라짐
    //왜냐하면 이미 MemberRepository라는 객체가 메모리에 고정되어있고 그 안에 변수들을 바꾸기 때문에
    //static과 같은 결과를 내기 때문임
    //하지만 왜 static을 붙였냐면 나중에 이 MemberRepository객체를 private로 생성자 막아버린거
    //없애고 새로운 객체인 new로 생성하게 된다면 static을 사용안 할 시 각각 객체마다 데이터를 갖게 되버림
    //일종의 이중 보안인것 같음, 멤버변수에 static을 넣으면 어쨋든 중간 코드가 이상해도 static을 통해서
    //하나의 데이터만 접근하게 되니 결과적으로 각각의 데이터가 생성될 일은 없을 거임
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
        return new ArrayList<>(store.values()); //member를 저장한것

    }
    public void clearStore(){
        store.clear();
    }

}
