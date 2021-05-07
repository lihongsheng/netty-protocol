package netty.pro.ServerApi;

import netty.pro.entiry.UserInfo;

import javax.jws.soap.SOAPBinding;

public interface User {

    public UserInfo get(int uid);
}
