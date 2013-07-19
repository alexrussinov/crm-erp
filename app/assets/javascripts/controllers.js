function MainCtrl($scope,$http){

}

function GetLinesCtrl($scope, $http){

  var kg=0;
  var piece=0;
  $scope.total_qty=0;


  $http.get('/lines'+ $("#order_id").val()).success(function(data) {
       if (!$.isEmptyObject(data)){
             $("#no_record").remove();
         }
      $scope.lines = data;
      $.each(data, function(index,item){
                  $scope.index = index+1;
                  if (item.unity == "kg")
                        kg += item.qty;
                      else
                        piece += item.qty;
                      if (kg > 0 && piece == 0)
                       $scope.total_qty = kg +" KG";
                      else if (kg == 0 && piece > 0)
                       $scope.total_qty = piece + " Piece" ;
                      else
                        $scope.total_qty = kg+" KG"+" et "+piece+" Piece";
                  });

    });


}

// Retrieve products from database
function GetProductsCtrl($scope, $http, $filter){
    $scope.itemsPerPage = 20;
    $scope.pagedItems = [];
    $scope.currentPage = 0;

    // ajax request for products in database
    // TODO user id is hard coded, must correspond for current user loged in
    $http.get('/catalogue/json?id='+$scope.customer_id).success(function(data){
    $scope.products =  data;
        $scope.search();
    });



    // search a needle in a haystack OK <> true, KO <> false
    var searchMatch = function (haystack, needle) {
        if (!needle) {
            return true;
        }
        return normalize2((''+ haystack).toLowerCase()).indexOf(normalize2(needle.toLowerCase())) !== -1;
    };

// init the filtered items
    $scope.search = function () {
        $scope.filteredItems = $filter('filter')($scope.products, function (item) {
            for(var attr in item) {
                if (searchMatch(item[attr], $scope.query))
                    return true;
            }
            return false;
        });
        $scope.currentPage = 0;
        // now group by pages
        $scope.groupToPages();
    }

   /*Catalog search result Pagination*/
    $scope.groupToPages = function () {
        $scope.pagedItems = [];

        for (var i = 0; i < $scope.filteredItems.length; i++) {
            if (i % $scope.itemsPerPage === 0) {
                $scope.pagedItems[Math.floor(i / $scope.itemsPerPage)] = [ $scope.filteredItems[i] ];
            } else {
                $scope.pagedItems[Math.floor(i / $scope.itemsPerPage)].push($scope.filteredItems[i]);
            }
        }
    };
//    $scope.groupToPages();
    $scope.range = function (start, end) {
        var ret = [];
        if (!end) {
            end = start;
            start = 0;
        }
        for (var i = start; i < end; i++) {
            ret.push(i);
        }
        return ret;
    };

    $scope.prevPage = function () {
        if ($scope.currentPage > 0) {
            $scope.currentPage--;
        }
    };

    $scope.nextPage = function () {
        if ($scope.currentPage < $scope.pagedItems.length - 1) {
            $scope.currentPage++;
        }
    };

    $scope.setPage = function () {
        $scope.currentPage = this.n;
    };

    //$scope.search();
   /*add product action from catalog*/
    $scope.addProduct = function (product) {
        $scope.orders=[];

        $scope.current_product_id = product._1
        if($scope.isAdmin == 1){
        $http.get('/getorders').success(function(data){

            if (!$.isEmptyObject(data)){
//               for (var item in data ){
//                   if(item.fk_statut == 0)
//                   $scope.orders.push(item);
//               }
                $scope.orders=data
                $('#orderChoice').modal('toggle');
            }
            else
                $('#addAlertModal').modal('toggle');
        });
        }
        else {
            $http.get('/getorders/customer?id='+$scope.customer_id).success(function(data){

                if (!$.isEmptyObject(data)){
//               for (var item in data ){
//                   if(item.fk_statut == 0)
//                   $scope.orders.push(item);
//               }
                    $scope.orders=data
                    $('#orderChoice').modal('toggle');
                }
                else
                    $('#addAlertModal').modal('toggle');
            });
        }
    }
 /*insert line to order from catalog*/
    $scope.insertProduct = function (){
        $scope.insert_product_json =
            { user_id: $scope.order.fk_soc,
              order_id: $scope.order.id,
              product_id: $scope.current_product_id,
              qty: $scope.current_order_product_qty };
//        alert("Client id:" + $scope.insert_product_json.user_id + "Order id:"+$scope.insert_product_json.order_id+
//        "Product id:"+$scope.insert_product_json.product_id+"Qty :"+$scope.insert_product_json.qty);
        $http.post("/addLineJson", $scope.insert_product_json)
            .success(function(data, status, headers, config) {
                $('#orderChoice').modal('hide');
            }).error(function(data, status, headers, config) {
                $scope.status = status;
                alert('Error'+status)
            });
    }

}

GetProductsCtrl.$inject = ['$scope','$http','$filter'];

// regroupe order fiche functionality
function OrderCtrl($scope,$http, calculateTotalQtyService){
    $scope.total_piece = 0;
    $scope.total_kg = 0;
    $scope.line_editable = false;
    $scope.order_editable = true;
    $scope.editMode = false;

   //initialising order scope
    $http.get('/order?id='+$scope.current_order_id).success(function(data){

        $scope.order = data.ord;
        $scope.order.customer = data.customer;
        $scope.order.lines = data.lines;
        $scope.setTotalQty = setTotalQty(data.lines);

    });

    // function insert line in the current order
    $scope.insertLine = function (){
        $scope.insert_product_json =
        { user_id: $scope.order.customer.id,
            order_id: $scope.order.id,
            product_id: $scope.product_id,
            qty: $scope.qty };
//        alert("Client id:" + $scope.insert_product_json.user_id + "Order id:"+$scope.insert_product_json.order_id+
//        "Product id:"+$scope.insert_product_json.product_id+"Qty :"+$scope.insert_product_json.qty);
        $http.post("/addLineJson", $scope.insert_product_json)
            .success(function(data, status, headers, config) {
                $scope.order = data.ord;
                $scope.order.customer = data.customer;
                $scope.order.lines = data.lines;
                $scope.request = '';
                $scope.qty = '';
                setTotalQty(data.lines)

            }).error(function(data, status, headers, config) {
                $scope.status = status;
                alert('Error'+status);
            });
    }

    $scope.updateLine = function(line,id){
        var update_data = {order_id : id, line_id : line.id, qty : line.qty};
        $http.post('/updateline/json',update_data).success(function(data){
            $scope.order = data.ord;
            $scope.order.customer = data.customer;
            $scope.order.lines = data.lines;
            var res = calculateTotalQtyService.set(data.lines);
            $scope.total_kg = res[0];
            $scope.total_piece = res[1];
            $scope.editMode= false;
        }).error(function(data,status){
                alert('Error: '+status);
            })
    }

    function setTotalQty(lines){
          var total_kg =0;
          var total_piece = 0;
        for(var i = 0; i<lines.length; i++){
            if(lines[i].unity == "piece")
                total_piece+=lines[i].qty ;
            if(lines[i].unity == "kg")
                total_kg+=lines[i].qty ;
        }
        $scope.total_kg = total_kg ;
        $scope.total_piece = total_piece;

    }
}
// controller for order pdf  template generation

function OrderPdfTplCtrl($scope, $http){
    $scope.total_piece = 0;
    $scope.total_kg = 0;

    //initialising order scope
    $http.get('/order?id='+$scope.order_id).success(function(data){

        $scope.order = data.ord;
        $scope.order.customer = data.customer;
        $scope.order.lines = data.lines;
        $scope.setTotalQty = setTotalQty(data.lines);

    });

    function setTotalQty(lines){
        var total_kg =0;
        var total_piece = 0;
        for(var i = 0; i<lines.length; i++){
            if(lines[i].unity == "piece")
                total_piece+=lines[i].qty ;
            if(lines[i].unity == "kg")
                total_kg+=lines[i].qty ;
        }
        $scope.total_kg = total_kg ;
        $scope.total_piece = total_piece;

    }
}

// List Orders Controller, fetch orders of the customer or all orders if admin

function ListOrdersCtrl($scope,$http){
    if($scope.user.admin == 1){
        $http.get('/getorders').success(function(data){
               $scope.orders=data;
        });
    }
    else {
        $http.get('/getorders/customer?id='+$scope.user.customer_id).success(function(data){

                $scope.orders=data

        });
    }
}

function SupplierCtrl($http,$scope){
    $http.get('/suppliers/json').success(function(data){
        $scope.suppliers = data;
    })
}

// TODO may be it would be better to realise search et pagination on server side
// TODO add filter by group, by manufacturer
// TODO Action create new order is possible only if there are no unvalidated orders, or Alert!!!