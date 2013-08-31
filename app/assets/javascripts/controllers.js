function MainCtrl($scope,$http,$filter){


    $scope.getUserActiveOrder = function(){
        if($scope.user.admin == 1){
            $http.get('/getorders').success(function(data){

                if (!$.isEmptyObject(data)){
                    for(var i=0; i<data.length; i++){
                        if(data[i].fk_statut == 0)
                            $scope.activeorder= data[i];
                    }
                }
                else $scope.activeorder={};

            });
        }
        else {
            $http.get('/getorders/customer?id='+$scope.user.customer_id).success(function(data){

                if (!$.isEmptyObject(data)){
                    for(var i=0; i<data.length; i++){
                        if(data[i].fk_statut == 0)
                            $scope.activeorder= data[i];
                    }
                }
                else $scope.activeorder={};

            });
        }

    }

    $scope.getUserActiveOrder();

    // catalog action, wee need to declare them in parent scope!
    //$scope.search();
    /*add product action from catalog*/
    $scope.addProduct = function (product) {
        $scope.orders=[];
        $scope.customers = getCustomers();
        //we use this with old implementation of json response from the server
        //$scope.current_product_id = product._1

        //for new json implementation
        $scope.current_product_id = product.id;

        if($scope.user.admin == 1){
            $http.get('/getorders').success(function(data){

                if (!$.isEmptyObject(data)){
//               for (var item in data ){
//                   if(item.fk_statut == 0)
//                   $scope.orders.push(item);
//               }
                    $scope.orders= data;
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
                    $scope.orders= data;
                    $('#orderChoice').modal('toggle');

                }
                else
                    $('#addAlertModal').modal('toggle');
            });
        }
    }
    /*insert line to order from catalog*/
    $scope.insertProduct = function (form){

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

    $scope.addProductNotAdmin = function(product){

        $scope.current_product_id = product.id;
        if (!$.isEmptyObject($scope.activeorder)){

            $scope.current_order_product_qty="";
            $('#CatalogAddProductNotAdmin').modal('toggle');
        }
        else
            $('#addAlertModal').modal('toggle');
    }

    // function that toggle new order creation modal
    $scope.newOrder = function(){

        if($scope.user.admin == 1){
          $scope.customers=getCustomers();

          $('#createOrder').modal('toggle');
          $('#datepicker').datepicker({
            dateFormat: "yy-mm-dd"
          });

        }
        else{
             $http.get('/getorders/customer?id='+$scope.user.customer_id).success(function(data){
                  var activeorder = {};
                 if (!$.isEmptyObject(data)){
                     for(var i=0; i<data.length; i++){
                         if(data[i].fk_statut == 0)
                             activeorder= data[i];
                     }
                 }
                 if(!$.isEmptyObject(activeorder) && activeorder.fk_statut == 0){
                     //console.log("validate order"+$scope.activeorder.ref);
                     console.log("From controller new order function activeorder : "+activeorder.fk_statut);
                     // console.log("From controller new order function order : "+$scope.order.fk_statut);
                     $('#createOrderAlert').modal('toggle');
                 }
                 else {
                     $('#createOrder').modal('toggle');
                     $('#datepicker').datepicker({
                         dateFormat: "yy-mm-dd"
                     });
                     console.log("From controller new order function activeorder : "+activeorder.fk_statut);
                 }
             });


        }

    }

    function getCustomers(){
        var result = {};
        $http.get('/getcustomers').success(function(data){
            for(var i=0; i< data.length; i++){
                result[data[i].id]=data[i];
            }
        });
        return result;
    }


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

// Data and data manipulations for catalog view //  Retrieve products from database
function CatalogCtrl($scope, $http, $filter){
    // initialize data for catalog

    $scope.itemsPerPage = 15;
    $scope.pagedItems = [];
    $scope.currentPage = 0;


    // ajax request for products in database
    // ajax request for products in database
    // TODO user id is hard coded, must correspond for current user loged in
    $http.get('/catalogue/json?id='+$scope.user.customer_id).success(function(data){
        $scope.products =  data;
        $scope.search();
        getMarques();
    });


    // search a needle in a haystack OK <> true, KO <> false
    var searchMatchString = function (haystack, needle) {
        if (!needle) {
            return true;
        }

        return normalize2((''+ haystack).toLowerCase()).indexOf(normalize2(needle.toLowerCase())) !== -1;

    };

    var searchMatchInt = function(haystack, needle){

        for(var i = 0; i<haystack.length; i++){
        if(haystack[i].id == needle)
        return true;

        }
        return false;
    }

// init the filtered items
    $scope.search = function (query) {
        $scope.filteredItems = $filter('filter')($scope.products, function (item) {

            for(var attr in item) {
                if (searchMatchString(item[attr], query))
                    return true;
            }
            return false;

        });
        $scope.currentPage = 0;
        // now group by pages
        $scope.groupToPages();


        getMarques();
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


    /**
     *  Side bar
     */
    $scope.marques_first = [];
    $scope.marques_rest = [];
    $scope.rest = false;
    $scope.marque_selection = {
        marque:{}
    };

    $scope.request=[];
    //$scope.pagedItems=[];


    // initialize data for side bar

    $http.get('/categories').success(function(data){
        $scope.categories = data;
    });

    $http.get('catalogue/numberproductsbycategory').success(function(data){
       $scope.numberOfProductsByCategory = data;
    });

    /* we get all marques from the sever */          //Old implementation
//    $http.get('/manufacturers').success(function(data){
//        //we take first 10 marque
//        for(var i=0; i<data.length; i++){
//            if(i<10)
//                $scope.marques_first.push(data[i]);
//            else
//                $scope.marques_rest.push(data[i]);
//
//        }
//    });


    // in this version we form our marque list(for side bar...) from filtered items array  instead of get all marques from database
    function getMarques(){
      if (typeof $scope.filteredItems != 'undefined'){
          //reinitialize marque every time to get marque for current array of filtered items
          $scope.marques_first = [];
          $scope.marques_rest = [];
        for(var i = 0; i< $scope.filteredItems.length; i++ ){
            if($scope.marques_first.length <=10){
                if($scope.marques_first.indexOf($scope.filteredItems[i].manufacture) == -1)
                $scope.marques_first.push($scope.filteredItems[i].manufacture);

            }
            else
                if($scope.marques_rest.indexOf($scope.filteredItems[i].manufacture) == -1)
                $scope.marques_rest.push($scope.filteredItems[i].manufacture);

        }
      }
    }

    // function to un check all checked Marque checkboxes
    $scope.uncheckAllMArques =  function(){
//        angular.forEach($scope.marque_selection.marque, function(value,key){
//            $scope.marque_selection.marque[key]=false;
//        });
        $scope.marque_selection.marque = [];
    }



    $scope.manufacturerFilter = function(mrq){

        if($scope.marque_selection.marque[mrq]){
            $scope.request.push(mrq);
            $scope.search($scope.query);
        }

        else {
            var idx =  $scope.request.indexOf(mrq);
            if (idx !== -1){
                $scope.request.splice(idx,1);
                $scope.search($scope.query);
            }

        }

        $scope.filteredItems = $filter('marqueFilter')($scope.filteredItems,$scope.request);

        $scope.currentPage = 0;
        // now group by pages
        $scope.groupToPages();

        window.scrollTo(0,0);



    }

    // represents catalog side-bar by category filter
    $scope.filterByCategory = function (category){
        $scope.filteredItems = $filter('filter')($scope.products, function (item) {
            if(typeof(category)=== 'undefined')
            return true;
            else
            // if there are subcategories we loop over and return true if any of them contain given item
             if(category.subcategory.length >0){

                for(var i=0; i < category.subcategory.length; i++){
                    if(category.subcategory[i].id == item.category_id)
                    return true;
                }
                return false;

             }
             else
             return item.category_id == category.id;
        });

        $scope.currentPage = 0;
        // now group by pages
        $scope.groupToPages();

        $scope.uncheckAllMArques();
        // clear search input
        $scope.query="";
        getMarques();
    }
}

//CatalogCtrl.$inject = ['$scope','$http','$filter'];

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

function ListOrdersCtrl($scope,$http,getCustomersService){
    if($scope.user.admin == 1){
        $scope.customers = getCustomersService.set();
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
    });
}

function ImportProductsFromCsvCtrl($http,$scope){
    $http.get('/products/import/files').success(function(data){

        $scope.filesToImport = data;
    });
}

/*
*   Controller to manage Products categories
 */
function ManageCategoriesCtrl($http, $scope){
    $http.get('/categories').success(function(data){
        $scope.categories = data;
    });
}

function ProductsImagesUploadCtrl($http, $scope){
    $http.get('/categories').success(function(data){
        $scope.categories = data;
    });
}


/*
* Controller to manage Catalog
 */

// Data and data manipulations for catalog view //  Retrieve products from database
function ManageCatalogCtrl($scope, $http, $filter){
    // initialize data for catalog

    $scope.itemsPerPage = 15;
    $scope.pagedItems = [];
    $scope.currentPage = 0;


    // ajax request for products in database
    // ajax request for products in database
    // TODO user id is hard coded, must correspond for current user loged in
    $http.get('/catalogue/json/admin').success(function(data){
        $scope.products =  data;
        $scope.search();
        getMarques();
    });


    // search a needle in a haystack OK <> true, KO <> false
    var searchMatchString = function (haystack, needle) {
        if (!needle) {
            return true;
        }

        return normalize2((''+ haystack).toLowerCase()).indexOf(normalize2(needle.toLowerCase())) !== -1;

    };

    var searchMatchInt = function(haystack, needle){

        for(var i = 0; i<haystack.length; i++){
            if(haystack[i].id == needle)
                return true;

        }
        return false;
    }

// init the filtered items
    $scope.search = function (query) {
        $scope.filteredItems = $filter('filter')($scope.products, function (item) {

            for(var attr in item) {
                if (searchMatchString(item[attr], query))
                    return true;
            }
            return false;

        });
        $scope.currentPage = 0;
        // now group by pages
        $scope.groupToPages();


        getMarques();
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


    /**
     *  Side bar
     */
    $scope.marques_first = [];
    $scope.marques_rest = [];
    $scope.rest = false;
    $scope.marque_selection = {
        marque:{}
    };

    $scope.request=[];
    //$scope.pagedItems=[];


    // initialize data for side bar

    $http.get('/categories').success(function(data){
        $scope.categories = data;
    });

    $http.get('/catalogue/numberproductsbycategory').success(function(data){
        $scope.numberOfProductsByCategory = data;
    });

    // in this version we form our marque list(for side bar...) from filtered items array  instead of get all marques from database
    function getMarques(){
        if (typeof $scope.filteredItems != 'undefined'){
            //reinitialize marque every time to get marque for current array of filtered items
            $scope.marques_first = [];
            $scope.marques_rest = [];
            for(var i = 0; i< $scope.filteredItems.length; i++ ){
                if($scope.marques_first.length <=10){
                    if($scope.marques_first.indexOf($scope.filteredItems[i].manufacture) == -1)
                        $scope.marques_first.push($scope.filteredItems[i].manufacture);

                }
                else
                if($scope.marques_rest.indexOf($scope.filteredItems[i].manufacture) == -1)
                    $scope.marques_rest.push($scope.filteredItems[i].manufacture);

            }
        }
    }

    // function to un check all checked Marque checkboxes
    $scope.uncheckAllMArques =  function(){
//        angular.forEach($scope.marque_selection.marque, function(value,key){
//            $scope.marque_selection.marque[key]=false;
//        });
        $scope.marque_selection.marque = [];
    }



    $scope.manufacturerFilter = function(mrq){

        if($scope.marque_selection.marque[mrq]){
            $scope.request.push(mrq);
            $scope.search($scope.query);
        }

        else {
            var idx =  $scope.request.indexOf(mrq);
            if (idx !== -1){
                $scope.request.splice(idx,1);
                $scope.search($scope.query);
            }

        }

        $scope.filteredItems = $filter('marqueFilter')($scope.filteredItems,$scope.request);

        $scope.currentPage = 0;
        // now group by pages
        $scope.groupToPages();

        window.scrollTo(0,0);



    }

    // represents catalog side-bar by category filter
    $scope.filterByCategory = function (category){
        $scope.filteredItems = $filter('filter')($scope.products, function (item) {
            if(typeof(category)=== 'undefined')
                return true;
            else
            // if there are subcategories we loop over and return true if any of them contain given item
            if(category.subcategory.length >0){

                for(var i=0; i < category.subcategory.length; i++){
                    if(category.subcategory[i].id == item.category_id)
                        return true;
                }
                return false;

            }
            else
                return item.category_id == category.id;
        });

        $scope.currentPage = 0;
        // now group by pages
        $scope.groupToPages();

        $scope.uncheckAllMArques();
        // clear search input
        $scope.query="";
        getMarques();
    }
}

function ProductFicheCtrl($scope, $http, $filter){
    $scope.value ="";
    $http.get('/product/json?id='+$scope.product_id).success(function(data){
          $scope.product = data;
    });
}

// TODO may be it would be better to realise search et pagination on server side

// TODO Tests for catalog view and controller
