package com.pinyougou.search.listener;

import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.xml.soap.Text;
import java.util.List;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.com.pinyougou.search.listener *
 * @since 1.0
 */
public class UpdateSolrListener implements MessageListener {

    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message) {
        if(message instanceof TextMessage){


            TextMessage textMessage = (TextMessage) message;
            try {
                //获取消息内容本身
                String text = textMessage.getText();

                //更新到索引库中
                List<TbItem> tbItems = JSON.parseArray(text, TbItem.class);

                itemSearchService.updateIndex(tbItems);

            } catch (JMSException e) {
                e.printStackTrace();
            }

        }

    }
}
