app.controller('seckillGoodsController',function ($scope,$interval,$location,seckillGoodsService) {
    //写一个方法 当页面加载的时候调用获取所有的商品的列表数据 展示

    $scope.findAll=function () {
        seckillGoodsService.findAll().success(
            function (response) {//List
                $scope.list=response;
            }
        )
    }

    //该方法 在 详情页面的加载的时候调用   获取URL中的参数 发送请求获取数据
    $scope.findOne=function () {
        var id = $location.search()['id'];
        if(id!=null  && id!=undefined){
            seckillGoodsService.findOne(id).success(
                function (response) {//商品的数据
                    $scope.entity=response;
                }
            )
        }
    }


    $scope.submitOrder=function (seckillId) {
        seckillGoodsService.submitOrder(seckillId).success(
            function (response) {
                if(response.success){
                    //跳转到支付的页面
                   window.location.href="pay.html";
                }else{
                    if(response.message=='401'){
                        alert("请登录");
                        var url = window.location.href;//http://localhost:9109/seckill-item.html#?id=5
                        window.location.href="/page/login.do?url="+encodeURIComponent(url);
                    }else{
                        alert(response.message);

                    }
                }
            }
        )
    }

    $scope.second = 10;
    time= $interval(function(){

        if($scope.second>0){
            $scope.second =$scope.second-1;
            console.log($scope.second);
        }else{
            $interval.cancel(time);
            alert("秒杀服务已结束");
        }
    },1000);


})