app.controller('itemController',function($scope,$http){
	//定一个方法 当点击+ - 的时候调用   为了 影响变量的值
	$scope.num=1;
	
	$scope.add=function(paramnum){
		
		paramnum=parseInt(paramnum);
		$scope.num=parseInt($scope.num);
		
		$scope.num=$scope.num+paramnum;
		
		if($scope.num<1){
			$scope.num=1;
		}
	}
	
	
	//定义一个变量 用于存储当前点击的 规格的数据
//	angular.fromJson(angular.toJson(skuList[0].spec))
	
	$scope.specificationItems=angular.fromJson(angular.toJson(skuList[0].spec)); //深克隆  {};//获取SKU的列表中的第一个元素中的spec的属性
	
	
	
	$scope.sku=skuList[0];
	
	
	
	//定义一个方法  用于点击的时候调用 用于影响变量specificationItems的值
	$scope.selectSpecifcation=function(specName,specValue){
		$scope.specificationItems[specName]=specValue;
		$scope.search();
	}
	
	//定义一个方法  用于判断 循环到的选项【移动3G】 是否在当前的变量中存在 ，如果存在 返回true ,否则 false
	
	$scope.isSelected=function(specName,specValue){
		
		if($scope.specificationItems[specName]==specValue){
			return true;
		}
		return false;
	}
	
	$scope.search=function(){
		for(var i=0;i<skuList.length;i++){
//			angular.toJson(skuList[i].spec)
//			angular.toJson($scope.specificationItems);
			
			if(angular.toJson(skuList[i].spec)==angular.toJson($scope.specificationItems)){
				$scope.sku=skuList[i];
				break;
			}
		}
	}

	//定义一个方法 作用就是当点击加入购物车的时候 调用  发送请求 添加购物车

	$scope.addGoodsToCartList=function () {

		// $http({
         //    method:'post',
         //    url:'http://localhost:9107/cart/addGoodsToCartList.do?itemId='+$scope.sku.id+'&num='+$scope.num,
         //
		// })


        $http.post('http://localhost:9107/cart/addGoodsToCartList.do?itemId='+$scope.sku.id+'&num='+$scope.num,{},{withCredentials:true}).success(
        	function (response) {//result
				if(response.success){
					//跳转到购物车的列表页面
					window.location.href="http://localhost:9107/cart.html";
				}else{
					alert("添加购物车失败");
				}
            }
		)
    }
	
	
	
	
	
	
})
