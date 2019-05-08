app.controller('cartController',function ($scope,cartService) {
    //页面一加载 就应该调用一个方法  发送请求获取购物车的列表 循环遍历显示

    $scope.findCartList=function () {
        cartService.findCartList().success(
            function (response) {//List<cart>
                $scope.cartList=response;
                $scope.total=0;
                $scope.totalMoney=0;
                for(var i=0;i< $scope.cartList.length;i++){
                    var cart = $scope.cartList[i];
                    for(var j=0;j<cart.orderItemList.length;j++){
                        var item = cart.orderItemList[j];//商品的对象
                        $scope.totalMoney+=item.totalFee;//小计
                        $scope.total+=item.num;//数量
                    }
                }
            }
        )
    }


    $scope.addGoodsToCartList=function (itemId,num) {
        cartService.addGoodsToCartList(itemId,num).success(
            function (response) {//result
                if(response.success){
                    $scope.findCartList();
                }
            }
        );
    }


    //页面初始化的的时候调用
    $scope.findAdressList=function () {
        cartService.findAdressList().success(
            function (response) {//List<tbaddress>
                $scope.addressList=response;

                //循环遍历数组  判断数组中的对象的isDefault是否为1 如果是，循环到的对象付给当前变量

                for(var i=0;i<$scope.addressList.length;i++){
                    var obj = $scope.addressList[i];//地址对象
                    if(obj.isDefault=='1'){
                        $scope.address=obj;
                        break;
                    }
                }

            }
        )
    }
    
    $scope.address={};
    
    
    $scope.selectAddress=function (address) {
        $scope.address=address;
    }

    $scope.isSelected=function (address) {
        //判断 循环到的地址对象是否和当前的变量的对象一致 如果一致就是要勾选
        if(address==$scope.address){
            return true;
        }
        return false;
    }

    $scope.order={paymentType:'1'};

    $scope.changeType=function (type) {
        $scope.order.paymentType=type;
    }
    
    //写一个方法 当点击提交订单的时候调用  发送请求 保存订单
    
    $scope.submitOrder=function () {
        //
        $scope.order.receiverMobile= $scope.address.mobile;
        $scope.order.receiverAreaName= $scope.address.address;
        $scope.order.receiver= $scope.address.contact;

        cartService.submitOrder($scope.order).success(
            function (response) {//result
                if(response.success){
                    //支付
                    window.location.href="pay.html";
                }else{
                    alert("失败");
                }
            }
        )
    }


})