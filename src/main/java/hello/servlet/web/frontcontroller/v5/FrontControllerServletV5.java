package hello.servlet.web.frontcontroller.v5;

import hello.servlet.web.frontcontroller.ModelView;
import hello.servlet.web.frontcontroller.MyView;
import hello.servlet.web.frontcontroller.v3.ControllerV3;
import hello.servlet.web.frontcontroller.v3.controller.MemberFormControllerV3;
import hello.servlet.web.frontcontroller.v3.controller.MemberListControllerV3;
import hello.servlet.web.frontcontroller.v3.controller.MemberSaveControllerV3;
import hello.servlet.web.frontcontroller.v4.controller.MemberFormControllerV4;
import hello.servlet.web.frontcontroller.v4.controller.MemberListControllerV4;
import hello.servlet.web.frontcontroller.v4.controller.MemberSaveControllerV4;
import hello.servlet.web.frontcontroller.v5.adapter.ControllerV3HandlerAdapter;
import hello.servlet.web.frontcontroller.v5.adapter.ControllerV4HandlerAdapter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name="frontControllerServletV5",urlPatterns = "/front-controller/v5/*")
public class FrontControllerServletV5 extends HttpServlet {

    private final Map<String,Object> handlerMappingMap = new HashMap<>();
    private final List<MyHandlerAdapter> handlerAdapters= new ArrayList<>();

    public FrontControllerServletV5() {
        initHandlerMappingMap();
        initHandlerAdapters();

    }


    private void initHandlerMappingMap() {
        handlerMappingMap.put("/front-controller/v5/v3/members/save",new MemberSaveControllerV3());
        handlerMappingMap.put("/front-controller/v5/v3/members/new-form",new MemberFormControllerV3());
        handlerMappingMap.put("/front-controller/v5/v3/members",new MemberListControllerV3());
        
        
        //v4추가
        handlerMappingMap.put("/front-controller/v5/v4/members/save",new MemberSaveControllerV4());
        handlerMappingMap.put("/front-controller/v5/v4/members/new-form",new MemberFormControllerV4());
        handlerMappingMap.put("/front-controller/v5/v4/members",new MemberListControllerV4());
    }
    private void initHandlerAdapters() {
        handlerAdapters.add(new ControllerV3HandlerAdapter());
        handlerAdapters.add(new ControllerV4HandlerAdapter());

    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 맵핑url을 보고 MemberListControllerV3가 쓰이는지 MemberListControllerV4가 쓰이는지 확인함
        Object handler = getHandler(request);

        //아무것도 맵핑된게 없다면 null
        if(handler==null){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        //MemberListControllerV3 혹은 MemberListControllerV4를 넣고
        //MemberListControllerV3 혹은 MemberListControllerV4를
        //ControllerV3HandlerAdapter()와 ControllerV4HandlerAdapter()에 넣고
        //ControllerV3HandlerAdapter.supports를 통해 지원하는지 않하는지를 확인함(혹은 v4)
        //지원한다면 return으로 true가 나오기 때문에 해당 adapter가 노암
        MyHandlerAdapter adapter = getHandlerAdapter(handler);

        //ControllerV3HandlerAdapter 혹은 ControllerV4HandlerAdapter에 (return이 트루가 되어 선택된 어댑터, 둘중 하나가 나오겠지?)
        //request와 response를 넣고 또 맵핑된 MemberListController를 넣음,컨트롤러를 넣는 이유는 컨트롤러마다 반환되는 형식이 다르기때문에
        //컨트롤러를 보내서
        ModelView mv = adapter.handle(request, response, handler);

        //어뎁터 핸들을통해서 얻은 ModelView 객체, 이 안에 목적지 viewname과 보낼 model 데이터가 있음
        //ModelView 객체에서 목적지viewname 얻고
        String viewName = mv.getViewName();

        //MyView 객체의 생성자를 통해서 MyView의 viewpath에 viewname을 "/WEB-INF/views/" + viewName + ".jsp"로 넣고
        MyView view = viewResolver(viewName);

        //보낼 데이터와 request, response를 넣고 dispatcher.forward(request,response)를 통해 보냄
        view.render(mv.getModel(),request,response);

        //과정을 설명하자면
        //클라이언트에서 요청을 dispatcherServlet에 보냄(frontcontroller)
        //dsipatcherServlet에서 그 요청을 보고 핸들러맵핑에서 해당되는 것을 찾고 해당 컨트롤러를 가져옴
        //해당 컨트롤러를 가지고 핸들러 어댑터 목록에서 그 컨트롤러를 적용시킬 수 있는 어댑터를 찾아봄
        //어댑터를 찾으면 해당 어댑터를 통해서 컨트롤러를 실행시킴
        //컨트롤러를 실행시켜서 받음, 어댑터가 값을 dispatcherServlet(frontcontroller)가 처리할 수 있는 형식으로 변환하여 보냄
        //dispatcherservelt이 그 값을 viewResolver를 통해서 render하여 응답하고
        //웹페이지가 완성됨!
    }
    private MyView viewResolver(String viewName) {
        return new MyView("/WEB-INF/views/" + viewName + ".jsp");
    }


    private Object getHandler(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        return handlerMappingMap.get(requestURI);
    }
    private MyHandlerAdapter getHandlerAdapter(Object handler) {

        for (MyHandlerAdapter adapter : handlerAdapters) {
            if(adapter.supports(handler)){
                return adapter;
            }
        }
        throw new IllegalArgumentException("handler adapter를 찾을 수 없습니다. handler= "+handler);
    }
}
