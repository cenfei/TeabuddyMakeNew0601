package com.taomake.teabuddy.object;

import java.io.Serializable;

public class RecordInfoObj implements Serializable, Comparable<RecordInfoObj> {

    private static final long serialVersionUID = 1L;

    public String voicefile_title;
    public String sys_headurl;
    public String voicefile_url;
    public String voicefile_no;
    @Override
    public int compareTo(RecordInfoObj another) {
        return Integer.valueOf(this.voicefile_no).compareTo(Integer.valueOf(another.voicefile_no));

    }
//    {"voicefile_title":"\u53a6\u5927","sys_headurl":"http:\/\/testserver.teabuddy.cn\/voice\/sys\/default.jpg",
// "voicefile_url":"http:\/\/testserver.teabuddy.cn\/voice\/sys\/1\/003.mp3"}
}
