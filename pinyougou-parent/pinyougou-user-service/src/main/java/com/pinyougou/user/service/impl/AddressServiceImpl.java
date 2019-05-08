package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbAddressMapper;
import com.pinyougou.pojo.TbAddress;
import com.pinyougou.pojo.TbAddressExample;
import com.pinyougou.user.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.user.service.impl *
 * @since 1.0
 */
@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private TbAddressMapper tbAddressMapper;

    @Override
    public List<TbAddress> findAddressList(String userId) {
        //select * from tb_address where user_id=1
        TbAddressExample exmaple = new TbAddressExample();
        TbAddressExample.Criteria criteria = exmaple.createCriteria();
        criteria.andUserIdEqualTo(userId);
        return tbAddressMapper.selectByExample(exmaple);
    }
}
