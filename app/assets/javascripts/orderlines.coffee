$ ->
 $.get "/lines"+$("#order_id").val(), (data) ->
  kg = 0
  piece = 0
  total_qty = 0
  $("input.edite-line").remove()
  $('#total_qty').text(total_qty)
  if !$.isEmptyObject(data)
#   $("#lines").append('<table class="table table-bordered" id="order_lines"></table>')
   $("#no_record").remove()
#  $("#order_lines").append($("<tr>").append("<th>S.N.</th><th>Ref.</th><th>Label</th><th>Tva</th><th>Qty</th><th>Unite</th><th>Prix HT</th><th>Total HT</th>"))
   $.each data, (index, item) ->
#     TODO Add a button that will show a photo of a product
#     TODO Add a button that will show additional info about a product
    $("#order_lines").append($("</tr><tr id="+item.id+">").append("<td>"+(index+1)+"</td><td class='line-ref'>"+item.product_ref+"</td><td>"+item.label+"</td><td class='line-tva'>"+item.tva+"</td><td class='line-qty'>"+item.qty+"</td><td class='line-unity'>"+item.unity+
    "</td><td class='line-prix_ht'
    >"+(item.prix_ht).toFixed(2)+"</td><td>"+(item.prix_ht*item.qty).toFixed(2)+"</td><th class='control-buttons' ><button  class='btn editline'><i class='icon-edit'></i></button> <button class='btn deleteline'><i class='icon-trash'></i> </button></th>"))
    if item.unity is "kg"
      kg+=item.qty
    else
      piece+=item.qty
    if kg > 0 and piece is 0
     total_qty = kg+" KG"
    else if kg is 0 and piece > 0
     total_qty = piece + " Piece"
    else
      total_qty = kg+" KG"+" et "+piece+" Piece"
    $('#total_qty').text(total_qty)
