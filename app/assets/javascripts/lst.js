function searchProducts()
{

var x=document.getElementById("search_value").value;

$("#products").replaceWith('<div id="products"></div>');
/* $.get("/searchproducts/"+ x, function(data){

   $("#products_list").replaceWith(generateTable(data));


});*/

$.get("/searchproducts/"+ x, function(data){
         generateTableProducts(data);
});

}

function generateTableProducts(data){

 var newTableProducts = document.createElement('table');
 newTableProducts.id =  'products_list';
 newTableProducts.setAttribute("class", "table table-striped");
 newTableProducts.innerHTML="<tr><th>Reference</th><th>Label</th><th>Price</th></tr>";
 var parentElement = document.getElementById("products");
 parentElement.appendChild(newTableProducts);
     $.each(data, function(index, item) {

          $("#products_list").append( $("<tr>").append("<td>" + item.ref + "</td>" + "<td>" + item.label + "</td>" +
          "<td>" + item.price + "</td> <td> <a href='zzzz' >Commande</a> </td> </tr>"));

       });
}
/* $(function(){
$("#product_id").on("keyup", function(event){
  $('#search_result').empty();
  $.get( "/searchproducts/"+ $('#product_id').val(), function(data){
         $.each(data, function(index, item) {
          $('#search_kbresult').append("<tr><td>" + item.label + "</td</tr>");
         });
  });
});

});*/

/*
$(function(){
$('#product_id').autocomplete ({ source : "/searchproducts/" });
}); */


// Generate autocomplete List of the products to add
$(function(){
  $('#product').autocomplete ({ source : function(req, resp){

     $.get("/searchproducts/?term="+ req.term+"&customer_id="+$('#customer_id').val(), req, function(data){
           var suggestions = [];
                       $.each(data, function(index, val) {
                         var obj = {};
                         //obj.label = val.ref+"-"+val.label+"-"+val.price;
                         obj.label = val._2+"-"+val._3+"-"+val._4;
                         obj.id = val._1;
                         suggestions.push(obj);
                       });
           resp(suggestions);
     });
            },
            select: function( event, ui ) {
                                    $('#product_id').val(ui.item.id);
//                                    alert(ui.item.id);
                                }



 });
});


//jsRoutes.controllers.Orders.searchProducts