app.service('userService',function ($http) {
    this.register=function (code,entity) {
        return $http.post('/user/register.do?code='+code,entity);
    }

    this.createCode=function (phone) {
        return $http.get('/user/createCode.do?phone='+phone);
    }

    this.getInfo=function () {
        return $http.get('/login/getInfo.do');
    }
})