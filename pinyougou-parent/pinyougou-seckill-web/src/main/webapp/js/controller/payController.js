app.controller('payController',function ($scope, $interval,payService) {
    //写一个方法 当页面加载的时候调用 ：发送请求获取二维码的连接信息 调用Qrious生成二维码

    $scope.createNative=function () {
        payService.createNative().success(
            function (response) {//Map

                $scope.total_fee=response.total_fee/100;//单位是元
                $scope.out_trade_no=response.out_trade_no;

                //生成二维码
                var qrious = new QRious({
                    element: document.getElementById("qrious"),
                    size:250,
                    level:'H',
                    value:response.code_url
                });

                //调用方法  查询该支付订单的状态
                $scope.queryStatus($scope.out_trade_no);

            }
        )
    }

    //页面中 定时 发送请求 调用查询状态。SetInterval



    $scope.queryStatus=function (out_trade_no) {

        $scope.second = 100;//5分钟就是超时 了

        time= $interval(function(){

            payService.queryStatus(out_trade_no).success(
                function (response) {
                    if(response.success){
                        //支付成功
                        window.location.href="paysuccess.html#?money="+$scope.total_fee;
                    }else{
                        if(response.message=='501'){

                        }else{
                            window.location.href="payfail.html";
                        }
                    }
                }
            )
            if($scope.second>0){
                $scope.second =$scope.second-1;
                console.log($scope.second);
            }else{
                $interval.cancel(time);
                alert("超时");
                // 恢复库存  删除预订单===》请求
            }
        },3000);

    }
})