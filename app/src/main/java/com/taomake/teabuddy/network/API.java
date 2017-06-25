package com.taomake.teabuddy.network;

/**
 * 功能：全局变量
 * <p/>
 * /**
 *
 * @author pc
 */
public interface API {
    /*********************************
     * URL变量
     *****************************************/
//    public String server = "http://testserver.teabuddy.cn/appadmin/server2.php";
    public String server = "http://app.teabuddy.cn/appadmin/server2.php";

//public String share_url="http://testserver.teabuddy.cn/appadmin/shareIndex.php?shareid=437";
//    public String share_url="http://testserver.teabuddy.cn/appadmin/share.php?voicefile_index=";
public String share_url="http://app.teabuddy.cn/appadmin/share.php?voicefile_index=";

    /**
     * 获取手机验证码
     */
    public String GET_PHONE_CHECK_CODE = server ;


    public String  shareTitle="想说的都在这里了，把他装进茶密里，每天陪着你!";


}