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