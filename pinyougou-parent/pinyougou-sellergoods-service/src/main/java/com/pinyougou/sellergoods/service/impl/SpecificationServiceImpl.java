package com.pinyougou.sellergoods.service.impl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pinyougou.group.Specification;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSpecificationMapper;
import com.pinyougou.pojo.TbSpecificationExample.Criteria;
import com.pinyougou.sellergoods.service.SpecificationService;

import entity.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SpecificationServiceImpl implements SpecificationService {

	@Autowired
	private TbSpecificationMapper specificationMapper;

	@Autowired
	private TbSpecificationOptionMapper specificationOptionMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSpecification> findAll() {
		return specificationMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSpecification> page=   (Page<TbSpecification>) specificationMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Specification specification) {
		//获取规格的数据
		TbSpecification specification1 = specification.getSpecification();
		//获取规格的选项数据
		List<TbSpecificationOption> optionList = specification.getOptionList();
		//添加规格表
		specificationMapper.insert(specification1);
		//添加规格选项表
		for (TbSpecificationOption tbSpecificationOption : optionList) {
			tbSpecificationOption.setSpecId(specification1.getId());
			specificationOptionMapper.insert(tbSpecificationOption);
		}

	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(Specification specification){

		//获取规格的数据
		TbSpecification specification1 = specification.getSpecification();
		//获取规格选项的数据
		List<TbSpecificationOption> optionList = specification.getOptionList();
		//更新规格（pojo里面一定要有主键的值）
		specificationMapper.updateByPrimaryKey(specification1);
		//更新规格的选项
			//删除原来的数据库中的规格的选项   delete from option where spec_id=1
		TbSpecificationOptionExample exmaple = new TbSpecificationOptionExample();
		TbSpecificationOptionExample.Criteria criteria = exmaple.createCriteria();
		criteria.andSpecIdEqualTo(specification1.getId());
		specificationOptionMapper.deleteByExample(exmaple);

			//新增页面传递过来的规格的选项   insert into (....)
		for (TbSpecificationOption tbSpecificationOption : optionList) {
			tbSpecificationOption.setSpecId(specification1.getId());
			specificationOptionMapper.insert(tbSpecificationOption);
		}

	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Specification findOne(Long id){
		//定义一个POJO对象
		Specification specification = new Specification();
		//查询规格的数据
		TbSpecification tbSpecification = specificationMapper.selectByPrimaryKey(id);
		//查询该规格对应的选项的列表数据
		//select * from option where spec_id=1
		TbSpecificationOptionExample exmaple = new TbSpecificationOptionExample();
		TbSpecificationOptionExample.Criteria criteria = exmaple.createCriteria();
		criteria.andSpecIdEqualTo(id);
		List<TbSpecificationOption> options = specificationOptionMapper.selectByExample(exmaple);
		//组合 返回
		specification.setSpecification(tbSpecification);
		specification.setOptionList(options);
		return specification;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			//删除规格 以及删除该规格对应的规格的选项
			specificationMapper.deleteByPrimaryKey(id);
			//delete from option where spec_id=1
			TbSpecificationOptionExample exmaple = new TbSpecificationOptionExample();
			TbSpecificationOptionExample.Criteria criteria = exmaple.createCriteria();
			criteria.andSpecIdEqualTo(id);
			specificationOptionMapper.deleteByExample(exmaple);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbSpecification specification, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSpecificationExample example=new TbSpecificationExample();
		Criteria criteria = example.createCriteria();
		
		if(specification!=null){			
						if(specification.getSpecName()!=null && specification.getSpecName().length()>0){
				criteria.andSpecNameLike("%"+specification.getSpecName()+"%");
			}
	
		}
		
		Page<TbSpecification> page= (Page<TbSpecification>)specificationMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

    @Override
    public List<Map> findSpecList() {
		// [{id:1,text:'bug'}]
		List<TbSpecification> tbSpecifications = specificationMapper.selectByExample(null);
		List<Map> specMapList = new ArrayList<>();
		for (TbSpecification tbSpecification : tbSpecifications) {
			Map map = new HashMap();
			map.put("id",tbSpecification.getId());
			map.put("text",tbSpecification.getSpecName());
			specMapList.add(map);
		}
		return specMapList;
    }

}
