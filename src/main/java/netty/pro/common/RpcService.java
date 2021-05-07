package netty.pro.common;

import java.util.List;
import java.util.Map;

public interface RpcService {
    public Response noParamApi();
    public Response simpleParamApi(int param1,String params);
    public Response hardParamApi(String params, Map<String,String> map);
    //public Response hardParamApi2(List<Request> requests, Map<String,Request> map);
}
