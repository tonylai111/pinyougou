package com.pinyougou.user.service;

import com.pinyougou.pojo.TbAddress;

import java.util.List;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.user.service *
 * @since 1.0
 */
public interface AddressService {
    List<TbAddress> findAddressList(String userId);

}
