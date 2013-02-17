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
$(function(){
$('#product_id').autocomplete ({source : javascriptRoutes.controllers.Orders.searchProducts
 });
});