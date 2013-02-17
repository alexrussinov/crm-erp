$.get "/getorders", (data) ->
  $.each data, (index, item) ->
   $("#list_orders").append ($("<tr>").append("<td><a href='/order" + item.id + "'>" + item.ref + "</a></td>" +
   "<td>" + item.order_date + "</td>" + "<td>" + item.fk_soc + "</td>" + "<td>" + item.total_ht + "</td>" + "<td>" + item.total_ttc + "</td>" +
     "<td>" + item.fk_statut + "</td>"))