google.charts.load('current', { 'packages': ['corechart', 'bar'] });
google.charts.setOnLoadCallback(drawChart);
function drawChart() {
    var data = google.visualization.arrayToDataTable(report[0]);

    var options = {
        title: 'BIỂU ĐỒ CÁC SẢN PHẨM BÁN CHẠY NHẤT'
    };

    var chart = new google.visualization.PieChart(document.getElementById('piechart'));

    chart.draw(data, options);

    // Chart report 2 
    var data = google.visualization.arrayToDataTable(report2[0]);

      var options = {
        chart: {
            title: 'THỐNG KÊ THEO ĐƠN HÀNG',
            subtitle: 'THỐNG KÊ SỐ LƯỢNG CÁC ĐƠN HÀNG TRONG NĂM',
        }
    };

      var chart = new google.charts.Bar(document.getElementById('barchart_material'));

      chart.draw(data, google.charts.Bar.convertOptions(options));
    

   
}