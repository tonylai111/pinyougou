//控制层
app.controller('sellerController', function ($scope, $controller, sellerService) {

    $controller('baseController', {$scope: $scope});//继承

    $scope.register = function () {
        sellerService.add($scope.entity).success(
            function (response) {//result
			    if(response.success){
			        //跳转到登录页面
                    alert("要登录");
                }else{
			        alert(response.message);
                }
            }
        )
    }

});	
