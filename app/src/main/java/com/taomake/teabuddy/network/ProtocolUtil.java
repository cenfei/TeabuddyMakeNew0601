package com.taomake.teabuddy.network;

import android.content.Context;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.taomake.teabuddy.util.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by xiaoganghu on 15/6/29.
 */
public class ProtocolUtil {
    public static final int VERIFY_CODE_TYPE_REGISTER = 1;
    public static final int VERIFY_CODE_TYPE_BIND_PHONE = 2;
    public static final int VERIFY_CODE_TYPE_ALTER_PASSWD = 3;
    public static final int VERIFY_CODE_TYPE_FORGOT_PASSWD = 4;

    public static boolean isSuccess(JSONObject resp) {
        try {
            if (resp.has(Constant.FIELD_RTN)) {
                return resp.getString(Constant.FIELD_RTN).equals(Constant.RES_SUCCESS);
            } else if (resp.has(Constant.FIELD_RTN2)) {
                return resp.getString(Constant.FIELD_RTN2).equals(Constant.RES_SUCCESS2);
            }
            return false;
        } catch (JSONException e) {
            return false;
        }
    }



    public static void getPhoneMsg(Context context,
                                   Protocol.CallBack callBack,
                                   String mobile
    ) {
        Map<String, Object> parmaMap = new HashMap<String, Object>();
        parmaMap.put("type", "yzm");

        parmaMap.put("step", "send");
        parmaMap.put("mobile", mobile);


        new Protocol(context, API.GET_PHONE_CHECK_CODE, parmaMap, callBack);
    }


    public static void getBindDeviceMsg(Context context,
                                   Protocol.CallBack callBack,
                                   String unionid
    ) {
        Map<String, Object> parmaMap = new HashMap<String, Object>();
        parmaMap.put("type", "login");

        parmaMap.put("unionid", unionid);


        new Protocol(context, API.GET_PHONE_CHECK_CODE, parmaMap, callBack);
    }


    public static void bindDevcieMsg(Context context,
                                   Protocol.CallBack callBack,
                                   String mobile,String unionid,String ticket,String age,String sex
    ) {
        Map<String, Object> parmaMap = new HashMap<String, Object>();
        parmaMap.put("type", "yzm");

        parmaMap.put("step", "binding");
        parmaMap.put("mobile", mobile);
        parmaMap.put("unionid", unionid);
        parmaMap.put("ticket", ticket);
        parmaMap.put("age", age);
        parmaMap.put("sex", sex);
        new Protocol(context, API.server, parmaMap, callBack);
    }




    public static void getTeainfoByUnionidMsg(Context context,
                                   Protocol.CallBack callBack,
                                   String unionid
    ) {
        Map<String, Object> parmaMap = new HashMap<String, Object>();
        parmaMap.put("type", "kz");

        parmaMap.put("step", "getcurrentcz");
        parmaMap.put("unionid", unionid);


        new Protocol(context, API.GET_PHONE_CHECK_CODE, parmaMap, callBack);
    }


    public static void getTeainfoByUnionidAndCzidMsg(Context context,
                                              Protocol.CallBack callBack,
                                              String unionid,String czid
    ) {
        Map<String, Object> parmaMap = new HashMap<String, Object>();
        parmaMap.put("type", "kz");

        parmaMap.put("step", "getczname");
        parmaMap.put("unionid", unionid);

        parmaMap.put("id", czid);


        new Protocol(context, API.GET_PHONE_CHECK_CODE, parmaMap, callBack);
    }


    public static void sendLogToServer(Context context,
                                              Protocol.CallBack callBack,
                                              String unionid,String deviceid,String bytes
    ) {
        Map<String, Object> parmaMap = new HashMap<String, Object>();
        parmaMap.put("type", "logsave");

        parmaMap.put("deviceid", deviceid);
        parmaMap.put("unionid", unionid);
        parmaMap.put("bytes", bytes);


        new Protocol(context, API.GET_PHONE_CHECK_CODE, parmaMap, callBack);
    }

    public static void getSumUnReadMsg(Context context,
                                       Protocol.CallBack callBack,
                                       String unionid
    ) {
        Map<String, Object> parmaMap = new HashMap<String, Object>();
        parmaMap.put("type", "getnotreadcount");

        parmaMap.put("unionid", unionid);


        new Protocol(context, API.GET_PHONE_CHECK_CODE, parmaMap, callBack);
    }

    public static void setAllMsgRead(Context context,
                                       Protocol.CallBack callBack,
                                       String unionid
    ) {
        Map<String, Object> parmaMap = new HashMap<String, Object>();
        parmaMap.put("type", "setmsgs");

        parmaMap.put("unionid", unionid);

        parmaMap.put("isread", "1");

        new Protocol(context, API.GET_PHONE_CHECK_CODE, parmaMap, callBack);
    }



    public static void sendDebugLogToServer(Context context,
                                       Protocol.CallBack callBack,
                                      String mac,String bytes
    ) {
        Map<String, Object> parmaMap = new HashMap<String, Object>();
        parmaMap.put("type", "logwx");

        parmaMap.put("mac", mac);
//        parmaMap.put("unionid", unionid);
        parmaMap.put("bytes", bytes);


        new Protocol(context, API.GET_PHONE_CHECK_CODE, parmaMap, callBack);
    }

    public static void getTeaListInfo(Context context,
                                              Protocol.CallBack callBack,
                                              String unionid
    ) {
        Map<String, Object> parmaMap = new HashMap<String, Object>();
        parmaMap.put("type", "hc");

        parmaMap.put("step", "list");
        parmaMap.put("unionid", unionid);


        new Protocol(context, API.GET_PHONE_CHECK_CODE, parmaMap, callBack);
    }

    public static void getTeaDetailInfo(Context context,
                                      Protocol.CallBack callBack,
                                      String unionid,String czid
    ) {
        Map<String, Object> parmaMap = new HashMap<String, Object>();
        parmaMap.put("type", "czgl");

        parmaMap.put("step", "get");
        parmaMap.put("unionid", unionid);
        parmaMap.put("id", czid);


        new Protocol(context, API.GET_PHONE_CHECK_CODE, parmaMap, callBack);
    }




    public static void saveTeaDetailInfo(Context context,
                                        Protocol.CallBack callBack,
                                        String unionid,String objjson,String obj2json
    ) {
        Map<String, Object> parmaMap = new HashMap<String, Object>();
        parmaMap.put("type", "czgl");

        parmaMap.put("step", "save");
        parmaMap.put("unionid", unionid);
        parmaMap.put("obj", objjson);
        parmaMap.put("obj2", obj2json);


        new Protocol(context, API.GET_PHONE_CHECK_CODE, parmaMap, callBack);
    }


    public static void saveChooseTeaInfo(Context context,
                                      Protocol.CallBack callBack,
                                      String unionid,String teaid
    ) {
        Map<String, Object> parmaMap = new HashMap<String, Object>();
        parmaMap.put("type", "hc");

        parmaMap.put("step", "save");
        parmaMap.put("unionid", unionid);
        parmaMap.put("id", teaid);


        new Protocol(context, API.GET_PHONE_CHECK_CODE, parmaMap, callBack);
    }


    public static void bcRecordList(Context context,
                                         Protocol.CallBack callBack,
                                         String unionid
    ) {
        Map<String, Object> parmaMap = new HashMap<String, Object>();
        parmaMap.put("type", "voice");

        parmaMap.put("step", "defaultvoice");
        parmaMap.put("unionid", unionid);



        new Protocol(context, API.GET_PHONE_CHECK_CODE, parmaMap, callBack);
    }



    public static void getUpdateRecordList(Context context,
                                    Protocol.CallBack callBack,
                                    String unionid,String voicefileindex
    ) {
        Map<String, Object> parmaMap = new HashMap<String, Object>();
        parmaMap.put("type", "edit");

        parmaMap.put("voicefileindex", voicefileindex);
        parmaMap.put("unionid", unionid);



        new Protocol(context, API.GET_PHONE_CHECK_CODE, parmaMap, callBack);
    }

    public static void bcDbTheVoiceRecordList(Context context,
                                    Protocol.CallBack callBack,
                                    String unionid,String Voice_no
    ) {
        Map<String, Object> parmaMap = new HashMap<String, Object>();
        parmaMap.put("type", "voice");

        parmaMap.put("step", "gffnolist");
        parmaMap.put("unionid", unionid);

        parmaMap.put("voice_no", Voice_no);

        new Protocol(context, API.GET_PHONE_CHECK_CODE, parmaMap, callBack);
    }
    public static void getMyCreateRecordList(Context context,
                                              Protocol.CallBack callBack,
                                              String unionid
    ) {
        Map<String, Object> parmaMap = new HashMap<String, Object>();
        parmaMap.put("type", "voice");

        parmaMap.put("step", "myvoice");
        parmaMap.put("unionid", unionid);


        new Protocol(context, API.GET_PHONE_CHECK_CODE, parmaMap, callBack);
    }

    public static void getGFRecordList(Context context,
                                             Protocol.CallBack callBack,
                                             String unionid
    ) {
        Map<String, Object> parmaMap = new HashMap<String, Object>();
        parmaMap.put("type", "voice");

        parmaMap.put("step", "gff");
        parmaMap.put("unionid", unionid);


        new Protocol(context, API.GET_PHONE_CHECK_CODE, parmaMap, callBack);
    }
    public static void getCollectRecordList(Context context,
                                       Protocol.CallBack callBack,
                                       String unionid
    ) {
        Map<String, Object> parmaMap = new HashMap<String, Object>();
        parmaMap.put("type", "voice");

        parmaMap.put("step", "fav");
        parmaMap.put("unionid", unionid);


        new Protocol(context, API.GET_PHONE_CHECK_CODE, parmaMap, callBack);
    }
    public static void delMyCreateRecord(Context context,
                                             Protocol.CallBack callBack,
                                             String unionid,String voicefileindex
    ) {
        Map<String, Object> parmaMap = new HashMap<String, Object>();
        parmaMap.put("type", "drop");

        parmaMap.put("voicefileindex", voicefileindex);
        parmaMap.put("unionid", unionid);


        new Protocol(context, API.GET_PHONE_CHECK_CODE, parmaMap, callBack);
    }

    public static void collectMyCreateRecord(Context context,
                                         Protocol.CallBack callBack,
                                         String unionid,String voicefileindex
    ) {
        Map<String, Object> parmaMap = new HashMap<String, Object>();
        parmaMap.put("type", "voice");

        parmaMap.put("step", "setfav");

        parmaMap.put("voicefile_index", voicefileindex);
        parmaMap.put("unionid", unionid);


        new Protocol(context, API.GET_PHONE_CHECK_CODE, parmaMap, callBack);
    }


    public static void getDeviceUpdateVersion(Context context,
                                             Protocol.CallBack callBack,
                                             String deviceid,String version
    ) {
        Map<String, Object> parmaMap = new HashMap<String, Object>();
        parmaMap.put("type", "gj");

//        parmaMap.put("step", "setfav");

        parmaMap.put("deviceid", deviceid);
        parmaMap.put("version", version);


        new Protocol(context, API.GET_PHONE_CHECK_CODE, parmaMap, callBack);
    }
    public static void getAppUpdateVersion(Context context,
                                              Protocol.CallBack callBack,
                                              String unionid
    ) {
        Map<String, Object> parmaMap = new HashMap<String, Object>();
        parmaMap.put("type", "version");

//        parmaMap.put("step", "setfav");

        parmaMap.put("step", "android");
        parmaMap.put("unionid", unionid);


        new Protocol(context, API.GET_PHONE_CHECK_CODE, parmaMap, callBack);
    }


    public static void getDeviceCubImgUrl(Context context,
                                              Protocol.CallBack callBack,
                                              String deviceid
    ) {
        Map<String, Object> parmaMap = new HashMap<String, Object>();
        parmaMap.put("type", "getdeviceurl");

//        parmaMap.put("step", "setfav");

        parmaMap.put("deviceid", deviceid);


        new Protocol(context, API.GET_PHONE_CHECK_CODE, parmaMap, callBack);
    }

    public static void getReadmeUrl(Context context,
                                          Protocol.CallBack callBack,
                                          String instruction,String deviceid
    ) {
        Map<String, Object> parmaMap = new HashMap<String, Object>();
        parmaMap.put("type", "geturl");

        parmaMap.put("deviceid", deviceid);

        parmaMap.put("step", instruction);


        new Protocol(context, API.GET_PHONE_CHECK_CODE, parmaMap, callBack);
    }




    public static void getSecondCodeImgUrl(Context context,
                                          Protocol.CallBack callBack,
            String unionid
    ) {
        Map<String, Object> parmaMap = new HashMap<String, Object>();
        parmaMap.put("type", "getqrcode");

//        parmaMap.put("step", "setfav");
//        parmaMap.put("ticket", ticket);

        parmaMap.put("unionid", unionid);


        new Protocol(context, API.GET_PHONE_CHECK_CODE, parmaMap, callBack);
    }

    public static void clickLove(Context context,
                                              Protocol.CallBack callBack,
                                              String unionid,String tounionid
    ) {
        Map<String, Object> parmaMap = new HashMap<String, Object>();
        parmaMap.put("type", "phb");

        parmaMap.put("step", "praise");

        parmaMap.put("unionid", unionid);
        parmaMap.put("tounionid", tounionid);


        new Protocol(context, API.GET_PHONE_CHECK_CODE, parmaMap, callBack);
    }

    public static void undocollectMyCreateRecord(Context context,
                                             Protocol.CallBack callBack,
                                             String unionid,String voicefileindex
    ) {
        Map<String, Object> parmaMap = new HashMap<String, Object>();
        parmaMap.put("type", "voice");

        parmaMap.put("step", "delfav");

        parmaMap.put("voicefile_index", voicefileindex);
        parmaMap.put("unionid", unionid);


        new Protocol(context, API.GET_PHONE_CHECK_CODE, parmaMap, callBack);
    }


    public static void getMinePersonInfo(Context context,
                                              Protocol.CallBack callBack,
                                              String unionid
    ) {
        Map<String, Object> parmaMap = new HashMap<String, Object>();
        parmaMap.put("type", "status");

        parmaMap.put("step", "person");
        parmaMap.put("unionid", unionid);


        new Protocol(context, API.GET_PHONE_CHECK_CODE, parmaMap, callBack);
    }


    public static void getMineSortListInfo(Context context,
                                         Protocol.CallBack callBack,
                                         String unionid,int start,int limit
    ) {
        Map<String, Object> parmaMap = new HashMap<String, Object>();
        parmaMap.put("type", "phb");

        parmaMap.put("step", "list");
        parmaMap.put("unionid", unionid);
        parmaMap.put("start", start);
        parmaMap.put("limit", limit);


        new Protocol(context, API.GET_PHONE_CHECK_CODE, parmaMap, callBack);
    }

    public static void getMineMsgListInfo(Context context,
                                           Protocol.CallBack callBack,
                                           String unionid,int start,int limit
    ) {
        Map<String, Object> parmaMap = new HashMap<String, Object>();
        parmaMap.put("type", "getmsg");

//        parmaMap.put("step", "list");
        parmaMap.put("unionid", unionid);
        parmaMap.put("page", start);
        parmaMap.put("pagequantity", limit);


        new Protocol(context, API.GET_PHONE_CHECK_CODE, parmaMap, callBack);
    }
    public static void setMineMsgIsRead(Context context,
                                          Protocol.CallBack callBack,
                                          String unionid,String mid
    ) {
        Map<String, Object> parmaMap = new HashMap<String, Object>();
        parmaMap.put("type", "setmsg");

//        parmaMap.put("step", "list");
        parmaMap.put("unionid", unionid);
        parmaMap.put("mid", mid);
        parmaMap.put("isread", 1);


        new Protocol(context, API.GET_PHONE_CHECK_CODE, parmaMap, callBack);
    }

    public static void sendBackIdea(Context context,
                                           Protocol.CallBack callBack,
                                           String unionid,String advice
    ) {
        Map<String, Object> parmaMap = new HashMap<String, Object>();
        parmaMap.put("type", "getadvice");

//        parmaMap.put("step", "list");
        parmaMap.put("unionid", unionid);
        parmaMap.put("advice", advice);


        new Protocol(context, API.GET_PHONE_CHECK_CODE, parmaMap, callBack);
    }


    public static void registerJpushId(Context context,
                                    Protocol.CallBack callBack,
                                    String unionid,String registrationID
    ) {
        Map<String, Object> parmaMap = new HashMap<String, Object>();
        parmaMap.put("type", "registrationIDsave");

//        parmaMap.put("step", "list");
        parmaMap.put("unionid", unionid);
        parmaMap.put("registrationID", registrationID);


        new Protocol(context, API.GET_PHONE_CHECK_CODE, parmaMap, callBack);
    }


    public static void addDeviceInfo(Context context,
                                       Protocol.CallBack callBack,
                                       String unionid,String ticket
    ) {
        Map<String, Object> parmaMap = new HashMap<String, Object>();
        parmaMap.put("type", "binding");

//        parmaMap.put("step", "list");
        parmaMap.put("unionid", unionid);
        parmaMap.put("ticket", ticket);


        new Protocol(context, API.GET_PHONE_CHECK_CODE, parmaMap, callBack);
    }



    public static void checkOnlineDebugCode(Context context,
                                    Protocol.CallBack callBack
    ) {
        Map<String, Object> parmaMap = new HashMap<String, Object>();
        parmaMap.put("type", "getcheckpassword");



        new Protocol(context, API.GET_PHONE_CHECK_CODE, parmaMap, callBack);
    }

    public static void updateBcRecordList(Context context,
                                             AsyncHttpResponseHandler callBack,
                                             String unionid, String voicefileindex, String voicefile_title, byte[] headurl
,List<File> voiceListByte
    ) {


        RequestParams parmaMap = new RequestParams();
        if (headurl != null)

            parmaMap.put("headurl", new ByteArrayInputStream(headurl), "image/jpeg");

        parmaMap.put("type", "editsave");

//        parmaMap.put("step", "list");
        parmaMap.put("unionid", unionid);
        parmaMap.put("voicefileindex", voicefileindex);

        parmaMap.put("voicefile_title", voicefile_title);
int i=3;
        for(File file:voiceListByte){
            try {
                parmaMap.put("voice"+i, file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            i++;

        }




        AsyncHttpUtil.post(API.GET_PHONE_CHECK_CODE, parmaMap, callBack);

//            new Protocol(context, API.ORDRE_DESIGN_FROM_LIB, parmaMap, callBack);
    }

    public static void addBcRecordList(Context context,
                                          AsyncHttpResponseHandler callBack,
                                          String unionid, String voicefile_title, byte[] headurl
            ,List<File> voiceListByte
    ) {


        RequestParams parmaMap = new RequestParams();
        if (headurl != null)

            parmaMap.put("headurl", new ByteArrayInputStream(headurl), "image/jpeg");

        parmaMap.put("type", "new");

//        parmaMap.put("step", "list");
        parmaMap.put("unionid", unionid);
//        parmaMap.put("voicefileindex", voicefileindex);

        parmaMap.put("voicefile_title", voicefile_title);
        int i=3;
        for(File file:voiceListByte){
            try {
                parmaMap.put("voice"+i, file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            i++;

        }




        AsyncHttpUtil.post(API.GET_PHONE_CHECK_CODE, parmaMap, callBack);

//            new Protocol(context, API.ORDRE_DESIGN_FROM_LIB, parmaMap, callBack);
    }


    public static void sendUserinfo(Context context,
                                    AsyncHttpResponseHandler callBack,
                                    byte[] headurl,String unionid,String nickname,String city,String province
    ) {



        RequestParams parmaMap = new RequestParams();
        if (headurl != null)

            parmaMap.put("headurl", new ByteArrayInputStream(headurl), "image/jpeg");

        parmaMap.put("type", "createunion");

        parmaMap.put("nickname", nickname);
        parmaMap.put("unionid", unionid);

        parmaMap.put("city", city);
        parmaMap.put("province", province);

        AsyncHttpUtil.post(API.GET_PHONE_CHECK_CODE, parmaMap, callBack);

    }

}
