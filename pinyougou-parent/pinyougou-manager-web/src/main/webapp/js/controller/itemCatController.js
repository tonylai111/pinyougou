 //控制层 
app.controller('itemCatController' ,function($scope,$controller   ,itemCatService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		itemCatService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		itemCatService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		itemCatService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=itemCatService.update( $scope.entity ); //修改  
		}else{
			serviceObject=itemCatService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		itemCatService.dele( $scope.selectIds ).success(
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
		itemCatService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

	//定义一个方法  当页面加载的时候调用 发送请求获取某一个级的分类的列表展示
	$scope.findByParentId=function (parentId) {
		itemCatService.findByParentId(parentId).success(
			function (response) {//List
				$scope.itemCatList=response;
            }
		)
    }

    //定义一个两个变量
	$scope.entity_1=null;
	$scope.entity_2=null;
	//默认当前的等级是1
	$scope.grade=1;
	$scope.setGrade=function (grade) {
		$scope.grade=grade;
    }

	//写一个方法 当点击的时候调用 去影响变量的值
	
	$scope.selectList=function (clikObject) {
		if($scope.grade==1){
            $scope.entity_1=null;
            $scope.entity_2=null;
		}
		if($scope.grade==2){
            $scope.entity_1=clikObject;//(被点击的那个对象)
            $scope.entity_2=null;
		}
		if($scope.grade==3){
            $scope.entity_2=clikObject;
		}

        $scope.findByParentId(clikObject.id);//findByParentId(itemCat.id);
    }
    
});	
