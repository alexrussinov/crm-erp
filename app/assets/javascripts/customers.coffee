
 $.get "/getcustomers", (data) ->
  $.each data, (index, item) ->
   $("#societe").append $("<option value='"+ item.id + "'>").text item.nom
