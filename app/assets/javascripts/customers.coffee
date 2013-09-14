
 $.get "/getcustomers", (data) ->
  $.each data, (index, item) ->
   $("#societe").append $("<option value='"+ item.id + "'>").text item.name
   # set actual date for the field order date
   dt= new Date()
   $("#order_date").val(dt.getFullYear()+"-"+(dt.getMonth()+1)+"-"+dt.getDate())