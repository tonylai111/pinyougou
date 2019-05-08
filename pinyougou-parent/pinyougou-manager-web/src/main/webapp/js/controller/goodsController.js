 //控制层 
app.controller('goodsController' ,function($scope,$controller   ,goodsService,itemCatService){
	
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
	$scope.findOne=function(id){				
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
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
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
                    $scope.selectIds=[];
					$scope.reloadList();//刷新列表
				}						
			}		
		);				
	}
	
	$scope.searchEntity={auditStatus:'0'};//定义搜索对象
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
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

		//写一个方法  当点击审核的时候调用  发送请求 更新

		$scope.updateStatus=function () {
			goodsService.updateStatus($scope.selectIds).success(
				function (response) {
					if(response.success){
						$scope.selectIds=[];
						$scope.reloadList();
					}else{
						alert(response.message);
					}
                }
			);
        }


    }
    
});	
