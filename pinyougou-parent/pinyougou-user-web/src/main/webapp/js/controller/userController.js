app.controller('userController',function ($scope,userService) {
    //写一个方法 当点击按钮 时候调用 发送请求完成注册

    $scope.register=function () {

        //判断确认密码是否和输入密码一致
        if($scope.entity.password!=$scope.confirmpassword){
            return;
        }

        userService.register($scope.code,$scope.entity).success(
            function (response) {//result
                if(response.success){
                    //跳转到登录的页面
                    window.location.href="/home-index.html";
                }else{
                    alert(response.message);
                }
            }
        );
    }
    
    $scope.createCode=function () {
        userService.createCode($scope.entity.phone).success(
            function (response) {
                alert(response.message);
            }
        )
    }


    //写一个方法  当页面加载的时候调用获取

    $scope.getInfo=function () {
        userService.getInfo().success(
            function (response) {//Map
                $scope.loginName=response.loginName;
            }
        )
    }
})