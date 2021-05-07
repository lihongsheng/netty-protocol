package netty.pro.ServerApi;

import netty.pro.entiry.UserInfo;

public class UserImpl implements User {
    public UserInfo get(int uid) {
        UserInfo userInfo  = new UserInfo();
        userInfo.setUid(uid);
        userInfo.setName(uid + ":name");
        return userInfo;
    }
}
