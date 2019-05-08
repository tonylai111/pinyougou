 //品牌控制层 
app.controller('baseController' ,function($scope){	
	
    //重新加载列表 数据
    $scope.reloadList=function(){
    	//切换页码  
    	$scope.search( $scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);	   	
    }
    
	//分页控件配置 
	$scope.paginationConf = {
         currentPage: 1,
         totalItems: 10,
         itemsPerPage: 10,
         perPageOptions: [10, 20, 30, 40, 50],
         onChange: function(){
        	 $scope.reloadList();//重新加载
     	 }
	}; 
	
	$scope.selectIds=[];//选中的ID集合 

	//更新复选
	$scope.updateSelection = function($event, id) {		
		if($event.target.checked){//如果是被选中,则增加到数组
			$scope.selectIds.push( id);			
		}else{
			var idx = $scope.selectIds.indexOf(id);
            $scope.selectIds.splice(idx, 1);//删除 
		}
	}

    //写一个方法 用于转换数据 返回固定的格式：a,b,c
    //循环遍历 json 获取text的值 进行拼接 返回

    $scope.jsonToString=function (list,key) {
        //先获取list
        //先将字符串转换成JSON
        var jsonObj=angular.fromJson(list);//[{id:1,text:'123'}]
        var str="";
        for(var i=0;i<jsonObj.length;i++){
            //{id:1,text:'123'}
            var obj = jsonObj[i];
            str+=obj[key]+",";
        }
        if(str.length>=1){
            str = str.substring(0,str.length-1);
        }

        return str;
    }

    /**
     *
     * @param list  [{"attributeValue":["移动3G","移动4G"],"attributeName":"网络"}]
     * @param specName  网络
     */
    $scope.searchObjectByKey=function (list,specName,attributeName) {
        for(var i=0;i<list.length;i++){
            var obj =(list[i]) //{"attributeValue":["移动3G","移动4G"],"attributeName":"网络"};
            if(obj[attributeName]==specName){
                return obj;
            }
        }
        return null;
    }
	
});	