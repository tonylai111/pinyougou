var app = angular.module('pinyougou', []);//定义模块


app.filter('trustHtml',function ($sce) {
    return function (data) {//data 就是 那个html标签对应的文本
        return $sce.trustAsHtml(data);
    }
})