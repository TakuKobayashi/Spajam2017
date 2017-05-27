package kobayashi.taku.com.egaonotatsuzin;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpRequest extends AsyncTask<String, Void, Map<String, String>> {
    private Map<String, Object> mParams = new HashMap<String, Object>();
    private ArrayList<ResponseCallback> callbackList = new ArrayList<ResponseCallback>();

    public void setParams(HashMap<String, Object> params){
        this.mParams = params;
    }

    public void addCallback(ResponseCallback callback){
        callbackList.add(callback);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        callbackList.clear();
    }

    protected Map<String, String> doInBackground(String... urls){
        HashMap<String, String> urlResponse = new HashMap<String, String>();
        OkHttpClient client = new OkHttpClient();
        for(String url : urls){
            Request.Builder requestBuilder = new Request.Builder();
            requestBuilder.url(url);

            Request request = requestBuilder.build();
            Response response = null;
            try {
                response = client.newCall(request).execute();
                if(response.isSuccessful()){
                    urlResponse.put(url, response.body().string());
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(Config.TAG, e.getMessage());
            }
        }
        return urlResponse;
    }

    @Override
    protected void onPostExecute(Map<String, String> result) {
        super.onPostExecute(result);
        for(Map.Entry<String, String> e : result.entrySet()) {
            for (ResponseCallback c : callbackList) {
                c.onSuccess(e.getKey(), e.getValue());
            }
        }
        callbackList.clear();
    }

    public interface ResponseCallback{
        public void onSuccess(String url, String body);
    }
}
