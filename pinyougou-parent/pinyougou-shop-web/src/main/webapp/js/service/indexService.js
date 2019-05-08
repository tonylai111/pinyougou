app.service('indexService',function ($http) {
    this.getLoinInfo=function () {
        return $http.get('/userinfo/getLoginInfo.do');
    }
})