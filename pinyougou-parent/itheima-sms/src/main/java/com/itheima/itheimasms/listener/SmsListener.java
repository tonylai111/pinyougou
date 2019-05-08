package com.itheima.itheimasms.listener;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.itheima.itheimasms.listener *
 * @since 1.0
 */

@Component
public class SmsListener {


    //产品名称:云通信短信API产品,开发者无需替换
    static final String product = "Dysmsapi";
    //产品域名,开发者无需替换
    static final String domain = "dysmsapi.aliyuncs.com";

    // TODO 此处需要替换成开发者自己的AK(在阿里云访问控制台寻找)
    static final String accessKeyId = "LTAIzHeZa8Ai6C0j";
    static final String accessKeySecret = "mQNjtbRq72zOxy254AbXwZ5ytGj3uP";

    public  SendSmsResponse sendSms(Map<String,String> map) throws ClientException {

        //可自助调整超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");

        //初始化acsClient,暂不支持region化
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
        IAcsClient acsClient = new DefaultAcsClient(profile);

        //组装请求对象-具体描述见控制台-文档部分内容
        SendSmsRequest request = new SendSmsRequest();
        //必填:待发送手机号
        request.setPhoneNumbers(map.get("mobile"));//17373201258
        //必填:短信签名-可在短信控制台中找到
        request.setSignName(map.get("sign_name"));//"黑马三国的包子"
        //必填:短信模板-可在短信控制台中找到
        request.setTemplateCode(map.get("template_code"));
        //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
        request.setTemplateParam(map.get("param"));

        //选填-上行短信扩展码(无特殊需求用户请忽略此字段)
        //request.setSmsUpExtendCode("90997");

        //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
        request.setOutId("yourOutId");

        //hint 此处可能会抛出异常，注意catch
        SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);

        return sendSmsResponse;
    }



    /**
     *
     * @param map
     *
     * key:mobile
     * key:sign_name
     * key:template_code
     * key:param  json
     */

    @JmsListener(destination = "pinyougou-sms")
    public void jieshou(Map<String,String> map){

        try {
            //1.接收数据
            //2.获取数据  拼接API要求的参数
            //3.调用API 发送短信
            sendSms(map);
        } catch (ClientException e) {
            e.printStackTrace();
        }


    }
}
