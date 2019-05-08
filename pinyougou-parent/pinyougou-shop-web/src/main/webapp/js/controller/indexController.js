app.controller('indexController',function ($scope,indexService) {
    //页面加载就发送请求获取数据
    $scope.getLoinInfo=function () {
        indexService.getLoinInfo().success(
            function (response) {//map
                $scope.info=response.loginName;
            }
        )
    }
})