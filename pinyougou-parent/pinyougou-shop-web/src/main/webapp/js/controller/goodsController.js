 //控制层 
app.controller('goodsController' ,function($scope,$controller,$location   ,goodsService,itemCatService,uploadService,typeTemplateService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(){
        //写一个方法  页面加载就调用  获取URL中的ID 的值 发送请求获取数据展示
		var id = $location.search()['id'];
		alert(id);
		//var obj={}
		//obj.id=1
		//obj['id']=1
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;

				editor.html($scope.entity.goodsDesc.introduction);
				//转换字符串为JSON  itemImages
				//JSON.pare
				//JSON.parse();==from
				//JSON.stringify()==to
                $scope.entity.goodsDesc.itemImages=angular.fromJson($scope.entity.goodsDesc.itemImages);
                $scope.entity.goodsDesc.customAttributeItems=angular.fromJson($scope.entity.goodsDesc.customAttributeItems);
                $scope.entity.goodsDesc.specificationItems=angular.fromJson($scope.entity.goodsDesc.specificationItems);


                for(var i=0;i<$scope.entity.itemList.length;i++){
                    var item = $scope.entity.itemList[i];//{}
                    item.spec=angular.fromJson(item.spec);
				}



			}
		);
	}
	

	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

    $scope.entity={goods:{},goodsDesc:{itemImages:[],customAttributeItems:[],specificationItems:[]},itemList:[]};


	//添加商品数据
    //保存
   /* $scope.add=function(){
    	//先获取富文本中的html的值赋值给entity中的introduction的属性中
        var text = editor.html();
        $scope.entity.goodsDesc.introduction=text;
        goodsService.add( $scope.entity  ).success(
            function(response){
                if(response.success){
                    //重新查询
                    //清空entity对象
                    $scope.entity={goods:{},goodsDesc:{itemImages:[],specificationItems:[]},itemList:[]};
                    editor.html('');
                }else{

                    alert(response.message);
                }
            }
        );
    }*/

    //保存
    $scope.save=function(){
        var serviceObject;//服务层对象
        if($scope.entity.goods.id!=null){//如果有ID
            serviceObject=goodsService.update( $scope.entity ); //修改
        }else{
            var text = editor.html();
            $scope.entity.goodsDesc.introduction=text;
            serviceObject=goodsService.add( $scope.entity  );//增加

        }
        serviceObject.success(
            function(response){
                if(response.success){
                    //重新查询
                    window.location.href="goods.html";
                }else{
                    alert(response.message);
                }
            }
        );
    }

    //写一个方法 用于当点击的时候调用上传图片
	$scope.uploadFile=function () {
			uploadService.uploadFile().success(
				function (response) {//result{message :url}
					if(response.success){
						//$scope.imagUrl=response.message;//图片地址
						$scope.image_entity.url=response.message;
					}else{
						alert(response.message);
					}
                }
			)
    }
    //写一个方法  当点击保存的按钮的时候调用  ：将图片对象image_entity 存储到数组中

	$scope.addTableRow=function () {
        $scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    }
    
    //写一个方法  当页面加载的是调用  查询一级分类
	
	$scope.selectItemCat1List=function () {
		itemCatService.findByParentId(0).success(
			function (response) {//List
				$scope.itemCat1List=response;
            }
		)
    }
    
    //监听某一个变量的变化而执行方法  监听一级分类  获取二级分类的列表
    $scope.$watch('entity.goods.category1Id',function (newvalue,oldvalue) {
			//发送请求根据一级分类的ID 查询二级分类的列表

		if(newvalue!=undefined){
            itemCatService.findByParentId(newvalue).success(
                function (response) {//List
                    $scope.itemCat2List=response;
                }
            )
		}

    });

    //监听某一个变量的变化而执行方法  监听二级分类  获取三级分类的列表
    $scope.$watch('entity.goods.category2Id',function (newvalue,oldvalue) {
        //监听二级分类  获取三级分类的列表

        if(newvalue!=undefined){
            itemCatService.findByParentId(newvalue).success(
                function (response) {//List
                    $scope.itemCat3List=response;
                }
            )
        }

    });

    //监听某一个变量的变化而执行方法  监听三级分类  获取三级分类对象 中的模板的ID 的值
    $scope.$watch('entity.goods.category3Id',function (newvalue,oldvalue) {
        //监听二级分类  获取三级分类的列表

        if(newvalue!=undefined){
            itemCatService.findOne(newvalue).success(
            	function (response) {//TBitemcat
					$scope.entity.goods.typeTemplateId=response.typeId;
                }
			)
        }

    });


    //监听某一个变量的变化而执行方法  监听模板的ID  获取模板的对象
    $scope.$watch('entity.goods.typeTemplateId',function (newvalue,oldvalue) {


        if(newvalue!=undefined){
			typeTemplateService.findOne(newvalue).success(
				function (response) {//模板对象
					$scope.typeTemplate=response;
                    $scope.typeTemplate.brandIds=angular.fromJson( $scope.typeTemplate.brandIds);
                    //获取模板的对象中的扩展属性的值
					//[{"text":"内存大小"},{"text":"颜色"}]
					//判断 如果是新增 就 取消注释
					if($scope.entity.goods.id==null || $scope.entity.goods.id==undefined){
                        $scope.entity.goodsDesc.customAttributeItems=angular.fromJson( $scope.typeTemplate.customAttributeItems);
					}

                }
			);


			//发送请求 规格的列表
            typeTemplateService.findSpecList(newvalue).success(
            	function (response) {//[{id,text,options}]
					$scope.specList=response;
                }
			)


        }

    });


    // 当点击的复选框的时候调用方法 ：目的就是改变变量的specificationItems的值。
    //specificationItems:[{"attributeValue":["移动3G","移动4G"],"attributeName":"网络"}]
	//伪代码
    /**
	 *
     * @param specName  网络
     * @param specValue  移动3G
     */
	$scope.updateSpecAttribute=function ($event,specName,specValue) {
		//在数组中根据规格名称去找对象 如果有对象 返回对象，如果没有对象返回null
		var object = $scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems,specName,'attributeName');

		if(object!=null){//{"attributeValue":["移动3G","移动4G"],"attributeName":"网络"}
			//在对象中添加数据
			//如果是勾选 就push

			if($event.target.checked){
                object.attributeValue.push(specValue);
			}else{
				////如果是取消勾选 就splice()
                object.attributeValue.splice(object.attributeValue.indexOf(specValue),1);
                //判断如果 数组中没有元素了就删除对象
				if(object.attributeValue.length==0){
                    $scope.entity.goodsDesc.specificationItems.splice($scope.entity.goodsDesc.specificationItems.indexOf(object),1);
				}
			}

		}else{
			//object=null
			//直接添加对象
            $scope.entity.goodsDesc.specificationItems.push({"attributeValue":[specValue],"attributeName":specName});
		}
    }


    
    //写一个方法：当点击复选框的时候  从头到尾重新构建变量
	
	$scope.createList=function () {
		//初始化变量
		$scope.entity.itemList=[{price:0,num:0,status:'0',isDefault:'0',spec:{}}];


		var speificationItems=$scope.entity.goodsDesc.specificationItems;
		for(var i=0;i< speificationItems.length;i++){
			var obj = speificationItems[i];//{"attributeValue":["移动3G","移动4G"],"attributeName":"网络"};

            $scope.entity.itemList=$scope.addColumn(obj.attributeName,obj.attributeValue,$scope.entity.itemList);
		}




    }

    //定义一个方法 ：就是循环遍历specifications 获取里面的规格名 和选项  拼接 返回一个全新的变量
    /**
	 *
     * @param attributeName     网络
     * @param attributeValue    [移动3G,"移动4G"]
     * @param List              [{price:0,num:0,status:'0',isDefault:'0',spec:{}}]
     * @returns {Array}
     */
	$scope.addColumn=function (attributeName,attributeValue,list) {
    	var newList=[];
    	//业务操作  就是拼接  最终放入到newList中
		for(var j=0;j<list.length;j++){
			var oldRow=list[j];//{price:0,num:0,status:'0',isDefault:'0',spec:{}}

			for(var n=0;n<attributeValue.length;n++){// [移动3G,"移动4G"]
				var newRow =angular.fromJson(angular.toJson(oldRow)); //序列化（深克隆）
                newRow.spec[attributeName]=attributeValue[n];
                newList.push(newRow);
			}

		}
		return newList;
    }




    $scope.status=['未审核','已审核','审核未通过','已关闭'];

	$scope.itemCatList=[];//$scope.itemCatList[558]=手机



	//页面加载时候调用一下
	$scope.findItemCatListAll=function () {
		//所有的分类
		itemCatService.findAll().success(
			function (response) {//List<tbitemcat>

				for(var i=0;i<response.length;i++){
					var itemcat = response[i];//对象
                    $scope.itemCatList[itemcat.id]=itemcat.name;
				}

            }
		)
    }

    /**
	 *
     * @param specName  循环到的规格的名称  网络
     * @param specValue  循环到的规格对应的具体的选项值  移动3G
     * @returns {boolean}
     */
    $scope.isChecked=function (specName,specValue) {
    	//[{"attributeValue":["移动3G","移动4G"],"attributeName":"网络"},
		// {"attributeValue":["16G","32G"],"attributeName":"机身内存"}]
    	var specificationItems = $scope.entity.goodsDesc.specificationItems;
		//判断 循环到的 移动3G 是否在数组中存储 如果存储 就返回true  否则就是false
        var obj = $scope.searchObjectByKey(specificationItems,specName,'attributeName');

        if(obj!=null){
            if(obj.attributeValue.indexOf(specValue)!=-1){
            	return true;
			}
		}
		return false;
    }










    
});	
