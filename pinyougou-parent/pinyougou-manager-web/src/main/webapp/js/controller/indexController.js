app.controller('indexController',function ($scope,$http,indexService) {
    //页面加载就发送请求获取数据
    $scope.getLoinInfo=function () {
        indexService.getLoinInfo().success(
            function (response) {//map
                $scope.info=response.loginName;
            }
        )
    }

    $scope.getLogin=function () {
        $http.get('/userinfo/hello.do').success(
            function (response) {
                $scope.xxxx=response;

            }
        )
    }
})