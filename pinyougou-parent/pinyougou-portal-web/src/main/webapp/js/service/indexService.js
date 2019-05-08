app.service('indexService',function ($http) {
    this.getContentList=function (categoryId) {
        return $http.get('/portal/getContentList.do?categoryId='+categoryId);
    }
})