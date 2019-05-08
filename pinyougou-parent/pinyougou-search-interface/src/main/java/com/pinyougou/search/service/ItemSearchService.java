package com.pinyougou.search.service;

import com.pinyougou.pojo.TbItem;

import java.util.List;
import java.util.Map; /**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.search.service *
 * @since 1.0
 */
public interface ItemSearchService {
    Map search(Map searchMap);


    void updateIndex(List<TbItem> skuList);


    void deleteByIds(Long[] ids);

}
