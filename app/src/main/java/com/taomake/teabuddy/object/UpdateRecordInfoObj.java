package com.taomake.teabuddy.object;

import java.io.Serializable;

public class UpdateRecordInfoObj implements Serializable , Comparable<UpdateRecordInfoObj>{

    private static final long serialVersionUID = 1L;

    public String voicefile_id;
    public String unionid;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getVoicefile_id() {
        return voicefile_id;
    }

    public void setVoicefile_id(String voicefile_id) {
        this.voicefile_id = voicefile_id;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public String getVoicefile_index() {
        return voicefile_index;
    }

    public void setVoicefile_index(String voicefile_index) {
        this.voicefile_index = voicefile_index;
    }

    public String getVoicefile_no() {
        return voicefile_no;
    }

    public void setVoicefile_no(String voicefile_no) {
        this.voicefile_no = voicefile_no;
    }

    public String getVoicefile_path() {
        return voicefile_path;
    }

    public void setVoicefile_path(String voicefile_path) {
        this.voicefile_path = voicefile_path;
    }

    public String getVoicefile_url() {
        return voicefile_url;
    }

    public void setVoicefile_url(String voicefile_url) {
        this.voicefile_url = voicefile_url;
    }

    public String getVoicefile_cutpathfile() {
        return voicefile_cutpathfile;
    }

    public void setVoicefile_cutpathfile(String voicefile_cutpathfile) {
        this.voicefile_cutpathfile = voicefile_cutpathfile;
    }

    public String getMedia_id() {
        return media_id;
    }

    public void setMedia_id(String media_id) {
        this.media_id = media_id;
    }

    public String getSpx_path() {
        return spx_path;
    }

    public void setSpx_path(String spx_path) {
        this.spx_path = spx_path;
    }

    public String getVoicefile_mute() {
        return voicefile_mute;
    }

    public void setVoicefile_mute(String voicefile_mute) {
        this.voicefile_mute = voicefile_mute;
    }

    public String getVoicefile_property() {
        return voicefile_property;
    }

    public void setVoicefile_property(String voicefile_property) {
        this.voicefile_property = voicefile_property;
    }

    public String voicefile_index;
    public String voicefile_no;
    public String voicefile_path;
    public String voicefile_url;
    public String voicefile_cutpathfile;
    public String media_id;
    public String spx_path;
    public String voicefile_mute;
    public String voicefile_property;

    @Override
      public int compareTo(UpdateRecordInfoObj another) {
        return Integer.valueOf(this.voicefile_no).compareTo(Integer.valueOf(another.voicefile_no));

    }

//    {"voicefile_id":"21","unionid":"sys","voicefile_index":"3","voicefile_no":"5","voicefile_path":"\/var\/www\/html\/voice\/sys\/3\/005.mp3",
// "voicefile_url":"http:\/\/testserver.teabuddy.cn\/voice\/sys\/3\/005.mp3",
// "voicefile_cutpathfile":"voice\/sys\/3\/005.mp3",
// "media_id":"","spx_path":"","voicefile_mute":"0"}

}
