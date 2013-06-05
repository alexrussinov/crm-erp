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

    // ajax request for data in database
    $http.get('/catalogue/json?id=1').success(function(data){
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

//pagination
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

}

//GetProductsCtrl.$inject = ['$scope','$http','$filter'];

// TODO may be it would be better to realise search et pagination on server side
// TODO add filter by group, by manufacturer