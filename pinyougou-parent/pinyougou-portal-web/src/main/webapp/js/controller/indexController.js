app.controller('indexController',function ($scope,indexService) {
    //页面一加载 应该调用一个方法 发送请求 获取轮播图的列表 展示

    $scope.contentList=[];
    $scope.getContentList=function (categoryId) {
        indexService.getContentList(categoryId).success(
            function (response) {//List<tbcontent>----1
                // $scope.contentList=response;
                $scope.contentList[categoryId]=response;
            }
        )
    }

    //点击搜索框的时候调用
    $scope.doSearch=function () {
        window.location.href="http://localhost:9104/search.html#?keywords="+$scope.keywords;
    }
})