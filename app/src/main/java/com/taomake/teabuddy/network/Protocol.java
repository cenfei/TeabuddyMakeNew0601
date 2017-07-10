package com.taomake.teabuddy.network;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.taomake.teabuddy.util.Util;

import java.util.Map;

@TargetApi(Build.VERSION_CODES.CUPCAKE)
public class Protocol {
    Context context;
    CallBack call;
    int callerId;
    Main mMainAsync;
    String url;
    int TOSTETIME = 3000;

    public Protocol(Context context, String url, Map<String, Object> paramMap,
                    CallBack call) {
        this.url = url;
        this.context = context;
        this.call = call;
        if (context == null)
            makeAsyncRequest(url, paramMap);
        else {
            if (Util.netState(context)) {
                makeAsyncRequest(url, paramMap);
            } else {
                Util.Toast(context, "网络不可用\n请稍后重试",null);
                call.getMessage("", url);
            }
        }

    }

    private void makeAsyncRequest(String url, Map<String, Object> paramMap) {

        if (Util.netState(context)) {
            ProtocolManager.getProtocolManager().addToQueue(this, call);
            mMainAsync = new Main(paramMap);
            mMainAsync.execute(url);
        } else {
            Util.Toast(context, "网络连接\n请稍后重试",null);
            call.getMessage("", url);
        }
    }

    public class Main extends AsyncTask<String, Void, String> {
        Double return_code;
        private Map<String, Object> paramMap;
        Map<String, Integer> param;

        public Main() {
        }

        public Main(Map<String, Object> params) {
            this.paramMap = params;
        }

        // public Main(Map<String, String> params,Map<String, Integer> param) {
        // this.paramMap = params;
        // this.param=param;
        // }

        public void toBaiduLogin(String resultNumCode) {
            // 判断是否登录

//            if (resultNumCode != null && "6000037".equals(resultNumCode)) {// 跳转到重新登录页面
//                Intent intent = new Intent(context, LoginActivity.class);
//                context.startActivity(intent);
//                return;
//            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null && result.length() > 0) {
                try {
                    String returnMsg = null;

                    Map<String, Object> orderMap = new Gson().fromJson(result,
                            new TypeToken<Map<String, Object>>() {
                            }.getType());
                    // 将 infomation 转成需要的 order信息

                    Double return_code_int = (Double) orderMap.get("rcode");
                    returnMsg = (String) orderMap.get("rmsg");

                    if (return_code_int == null) {
//                        Util.Toast(context, returnMsg);
                        Log.e("result:",result);
                        call.getMessage(result, url);
                    } else {
                        return_code = return_code_int ;
                    }
if(return_code_int==null){
    call.getMessage(result, url);
return;
}

                    if (return_code==1) {
                        call.getMessage(result, url);
                    } else if (return_code==0) {
                        if(!returnMsg.contains("fail")) {
                            Util.Toast(context, returnMsg,null);
                        }
                        call.getMessage(result, url);
                    } else if (return_code==2) {
//                        Util.Toast(context, returnMsg);
//                        if(!returnMsg.contains("fail")) {
                            Util.Toast(context, returnMsg,null);
//                        }
                        call.getMessage(result, url);
                    } else if (return_code==3) {
                        Util.Toast(context, returnMsg,null);
                        call.getMessage(result, url);
                    } else if (return_code.equals("6000021")) {
                        Log.i("return_code", "请求参数不合法");
                    } else if (return_code.equals("6200027")) {
                        call.getMessage(result, url);
                        //WalletUtil.Toast(context, "抱歉，一卡通系统临时维护，暂时无法充值，请您稍后再试");
                    } else if (return_code.equals("6000024")) {
                        Log.i("return_code", "系统繁忙，请稍后重试");
                    } else if (return_code.equals("6500007")) {
                        Log.i("return_code", "没有配置内容信息");
                    }
                    // } else if (return_code.equals("6500006")) {
                    // WalletUtil.Toast(context, "不支持该手机型号");
                    // } else if (return_code.equals("6500002")) {
                    // WalletUtil.Toast(context, "暂不支持该区域");
                    // } else if (return_code.equals("6500008")) {
                    // WalletUtil.Toast(context, "没有可用的充值面额");
                    //
                    // } else if (return_code.equals("6500015")) {
                    // WalletUtil.Toast(context, "一卡通余额超过1000不支持充值");
                    // } else if (return_code.equals("6200011")) {
                    // WalletUtil.Toast(context, "金额错误");
                    // }
                    else if (return_code.equals("6000032")) {
                        Log.i("return_code", "无权限操作");

                    } else if (return_code.equals("6000053")) {
                        Log.i("return_code", "缴费订单号为空");
                    } else if (return_code.equals("6000032")) {
                        Log.i("return_code", "无权限操作");
                    } else if (return_code.equals("6200026")) {
                        Log.i("return_code", "账户号为空");
                    } else if (return_code.equals("6500012")) {
                        Log.i("return_code", "对不起,充值发生异常");
                    } else if (return_code.equals("6000013")) {
                        Log.i("return_code", "缴费订单不存在");
                    } else if (return_code.equals("6000010")) {
                        //WalletUtil.Toast(context, "订单状态不可操作");//直接跳转到第四步
                        call.getMessage(result, url);

                    } else if (return_code.equals("6500011")) {
                        Log.i("return_code", "充值的卡片不一致");
                    } else if (return_code.equals("6500009")) {
                        Log.i("return_code", "对不起,充值发生异常,请联系客服");
                    } else if (return_code.equals("6000043")) {
                        Log.i("return_code", "未知的业务类型");
                    } else if (return_code.equals("6500019")) {
                        Log.i("return_code", "对不起,充值失败(备注：订单挂起");
                    } else if (return_code.equals("6500010")) {
                        Log.i("return_code", "对不起,充值失败(备注：订单退款)");
                    } else if (return_code.equals("6000052")) {
                        Log.i("return_code", "订单业务类型错误");
                    } else if (return_code.equals("6500013")) {
                        Log.i("return_code", "卡号已被绑定");
                        call.getMessage(result, url);

                    } else if (return_code.equals("p000")) {
                        Log.i("return_code", returnMsg);

                        call.getMessage(result, url);

                    } else if (return_code.equals("e0002")) {
                        Log.i("return_code", returnMsg);
                        Util.Toast(context, returnMsg,null);


                        call.getMessage(result, url);

                    } else if (return_code.equals("e002")) {
                        Log.i("return_code", returnMsg);


                        call.getMessage(result, url);

                    } else {
                        Log.i("return_code", "未知错误码" + return_code);
                        Util.Toast(context, returnMsg,null);

                    }

                } catch (Exception e) {
                    call.getMessage(null, url);
                    Util.Toast(context, "服务器错误",null);

                    System.out.println("Exception : " + e);
                    Log.e("Exception", "Exception", e);
                }
            } else {
                // WalletUtil.makeText(context, "网络连接超时!",
                // Toast.LENGTH_SHORT).show();.
                Util.Toast(context, "网络连接超时!",null);

                // call.getMessage(null, url);
                call.getMessage(null, url);// 网络请求失败-客户端进行处理
            }
            ProtocolManager.getProtocolManager().removeFromQueue(Protocol.this,
                    call);
        }

        @Override
        protected String doInBackground(String... params) {
            String result;

//            if (paramMap != null && paramMap.get(API.SH_NET_POST) != null) {
            result = Caller.sendPost(params[0], paramMap);
//
//            } else {
//                result = Caller.doHttpsPost(params[0], paramMap);
//            }
            return result;
        }
    }

    public interface CallBack {
        void getMessage(String infomation, String url);
    }
}
