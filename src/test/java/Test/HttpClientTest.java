package Test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class HttpClientTest {

	public static void main(String[] args) throws Exception {
		
//		CloseableHttpClient httpclient = HttpClients.createDefault();
//        try {
//            HttpGet httpget = new HttpGet("http://localhost:8080/");//注意这里的url地址
//
//            System.out.println("Executing request " + httpget.getRequestLine());
//
//            // Create a custom response handler
//            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
//
//                @Override
//                public String handleResponse(
//                        final HttpResponse response) throws ClientProtocolException, IOException {
//                    int status = response.getStatusLine().getStatusCode();
//                    if (status >= 200 && status < 300) {
//                        HttpEntity entity = response.getEntity();
//                        return entity != null ? EntityUtils.toString(entity) : null;
//                    } else {
//                        throw new ClientProtocolException("Unexpected response status: " + status);
//                    }
//                }
//
//            };
//            String responseBody = httpclient.execute(httpget, responseHandler);
//            System.out.println("----------------------------------------");
//            System.out.println(responseBody);
//        } finally {
//            httpclient.close();
//        }
        login();
	}
	
	public static  void login() throws Exception {
		HttpPost http=new HttpPost("http://localhost:8080//user/login.do");
		List<NameValuePair> vps=new ArrayList<>();
		vps.add(new BasicNameValuePair("username", "vvv"));
		vps.add(new BasicNameValuePair("password", "vvv"));
		vps.add(new BasicNameValuePair("code", "s8fgy8"));
		http.setEntity(new UrlEncodedFormEntity(vps));
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse resp=httpclient.execute(http);
		HttpEntity entity=resp.getEntity();//后台返回数据
		String result=EntityUtils.toString(entity);
		System.out.println(result);
		
		
		
	}
}
