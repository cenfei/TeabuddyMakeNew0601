package com.taomake.teabuddy.object;

import java.io.Serializable;

public class UpdateRecordHeadInfoObj implements Serializable {

    private static final long serialVersionUID = 1L;

    public String voicefile_index;
    public String unionid;
    public String voicefile_title;
    public String is_sys;
    public String sys_headurl;


//    {"head":{"voicefile_index":"3","unionid":"default3","voicefile_title":"\u5c0f\u5df4\u8336\u5bc6",
// "is_sys":"1","sys_headurl":"http:\/\/testserver.teabuddy.cn\/voice\/sys\/default.jpg",
// "sys_localheadurl":"\/var\/www\/html\/appadmin\/voice\/sys\/default.jpg"}

}
