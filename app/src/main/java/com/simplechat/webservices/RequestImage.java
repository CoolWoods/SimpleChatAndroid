package com.simplechat.webservices;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import com.simplechat.MainActivity;
import com.simplechat.login.LoginActivity;
import com.simplechat.utils.OkHttpUtils;
import com.simplechat.utils.RequestUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class RequestImage {

    private static final   String BASE_URL = "http://10.0.2.2:8080/SimpleChat/";
    //mHandler用于请求图片
    private Handler mHandler;
    /**
     * 初始化Request
     * @return
     */
    private Request initRequest(String imageName){
        //定义访问的api
        String url = BASE_URL + "/user/image/head?imageName=" + imageName + "&time=" + new Date().getTime();
        return RequestUtils.buildRequestForGet(url);
    }

    @SuppressLint("HandlerLeak")
    public  void sendRequestImage(ImageView imageView, String imageName){

        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == 1) {//System.out.println("访问成功:\n" + responseData);
                    //获取运行在子线程中的OkHttp访问得到的数据
                    byte[] responseData = (byte[]) msg.obj;
                    //利用返回的数据更新适配器
                    Bitmap bitmap;
                    try {
                        bitmap = BitmapFactory.decodeByteArray(responseData, 0, responseData.length);
                        imageView.setImageBitmap(bitmap);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    /*try {
                        File file = new File("/data/simplechat/image/head/");
                        if (!file.exists()){
                            file.mkdirs();
                        }
                        File file1 = new File("/data/simplechat/image/head/" + imageName);
                        if (!file1.exists()){
                            file1.createNewFile();
                        }
                        System.out.println("file1:" + file.toString());
                        FileOutputStream fos = new FileOutputStream(file1);
                        fos.write(responseData);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/
                }
            }
        };

        //OkHttp start
        //获取一个OkHttpClient实例
        OkHttpClient client = OkHttpUtils.getInstance();
        //获取一个request对象
        Request request = initRequest(imageName);
        //使用OkHttpClient实例执行请求
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure:", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                byte[] responseData = response.body().bytes();

                Message message = new Message();

                message.obj = responseData;

                //设置handler什么时候作用
                message.what = 1;
                //向主线程发消息
                mHandler.sendMessage(message);
            }
        });
        //OkHttp end
    }

}
