package com.taomake.teabuddy.prefs;

import org.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import org.androidannotations.annotations.sharedpreferences.DefaultInt;
import org.androidannotations.annotations.sharedpreferences.DefaultLong;
import org.androidannotations.annotations.sharedpreferences.DefaultString;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

/**
 * foxcen build 2016 0726
 */
@SharedPref(SharedPref.Scope.UNIQUE)
public interface ConfigPref {


    @DefaultString("")
    public String deviceUpdateInfo();

    @DefaultString("")
    public String getuiClientId();

    @DefaultString("")
    public String payUrl();

    @DefaultString("")
    public String userDeviceMac();
    @DefaultString("")
    public String userDeviceVersion();
    @DefaultString("")
    public String userDeviceId();
    @DefaultString("")
    public String userUnion();
    @DefaultString("")
    public String userName();

    @DefaultString("")
    public String userPhonenum();


    @DefaultString("")
    public String userTicket();


    @DefaultString("")
    public String likeListJson();


    @DefaultString("")
    public String designAllListJson();


    @DefaultString("")
    public String designHotListJson();

    @DefaultString("")
    public String userToken();

    @DefaultString("")
    public String userMobile();

    @DefaultLong(0)
    public long codeTime();

    @DefaultBoolean(true)
    public boolean showWelcome4();

    @DefaultBoolean(true)
    public boolean getNewMessage();

    @DefaultBoolean(true)
    public boolean messageVoice();

    @DefaultBoolean(true)
    public boolean messageShake();

    @DefaultString("")
    public String userTarget();

    @DefaultBoolean(true)
    public boolean deviceHint();

    @DefaultInt(0)
    public int deviceIndex();



    @DefaultBoolean(false)
    public boolean smsRemind();

    @DefaultBoolean(false)
    public boolean phoneRemind();

    @DefaultBoolean(false)
    public boolean sedentaryRemind();

    @DefaultBoolean(false)
    public boolean antilostRemind();

    @DefaultInt(0)
    public int vibrationMode();

    @DefaultInt(3)
    public int vibrationDelay();

    @DefaultString("")
    public String runAdUrl();

    @DefaultString("")
    public String alarmSetting();

    @DefaultString("09:00")
    public String sedentaryBeginTime();

    @DefaultString("18:00")
    public String sedentaryEndTime();

    @DefaultInt(30)
    public int sedentaryInterval();

    @DefaultString("")
    public String sedentaryPeriod();

    @DefaultString("")
    public String lastTimeUserCancelUpdate();

    @DefaultBoolean(true)
    public boolean isDefaultSetting();

    @DefaultBoolean(false)
    public boolean hasNewAlarmSetting();

    @DefaultBoolean(false)
    public boolean hasNewSedentarySetting();

    @DefaultBoolean(false)
    public boolean hasNewPhoneSetting();

    @DefaultBoolean(false)
    public boolean hasNewSmsSetting();

    @DefaultBoolean(false)
    public boolean hasNewAnitlostSetting();

    @DefaultString("")
    public String alarmSmartSetting();

    @DefaultString("")
    public String sedentarySmartSetting();

    @DefaultString("")
    public String phoneSmartSetting();

    @DefaultString("")
    public String smsSmartSetting();

    @DefaultString("")
    public String antilostSmartSetting();

}
