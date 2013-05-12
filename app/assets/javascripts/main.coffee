$("#order_lines").on("click", ".btn.editline",  (event)->
  id = $(this).closest('tr').attr('id')
  tva = $(this).closest('tr').children('td.line-tva').text()
  qty = $(this).closest('tr').children('td.line-qty').text()
  unity = $(this).closest('tr').children('td.line-unity').text()
  prix_ht = $(this).closest('tr').children('td.line-prix_ht').text()
  $(this).closest('tr').append("<input type='hidden' class='edite-line' name='id' form='update_line'  value="+id+">")

  $(this).closest('tr').children('td.line-qty').replaceWith(
    "<td class='lien-qty'><input class='smallInput' type='text' name='qty' form='update_line'  value="+qty+"></td>")
  $(this).closest('tr').children('th.control-buttons').replaceWith(
    "<th class='control-buttons'><button type='submit' form='update_line' class='btn' ><i class='icon-ok'></i></button>" +
    "<button id='cancel' class='btn cancel' ><i class='icon-remove'></i> </button></th>")

)


$("#order_lines").on("click", ".btn.deleteline",  (event)->

  $.ajax {
  url: "/line?id="+$(this).closest('tr').attr('id')+"&order_id="+$("#order_id").val(),
  type: 'DELETE',
  success:  (result) ->  location.reload()

  }

)

$("#order_lines").on("click", ".btn.cancel",  (event)->
  location.reload() )

