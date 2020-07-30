async function drawSentimentChart() {

  let sentiments = await fetch("/data?dataset=sentiments")
    .then(response => response.json())
    .then(json => json.map(x => [new Date(x.timestamp), x.sentimentScore]));

  let data = new google.visualization.DataTable();

  data.addColumn("date", "Hours Studied");
  data.addColumn("number", "Final");

  data.addRows(sentiments);

  let emojis = Comment.sentimentEmojis;

  let options = {
    title: "Feedback by date",
    height: 400,
    hAxis: {
      title: "Date",
    },
    vAxis: {
      title: "Sentiment score",
      ticks: emojis.map((emoji, index) => {
        return {v: (2 * (index + 0.5) / emojis.length - 1), f: emoji };
      }),
      minValue: -1,
      maxValue: 1,
      gridlines: {color: "transparent"},
    },
    legend: "none",
  };

  let chart = new google.visualization.ScatterChart(document.getElementById("sentiment-chart"));

  chart.draw(data, options);
}

function drawAllCharts() {
  drawSentimentChart();
}

window.onload = () => {
  google.charts.load("current", {"packages": ["corechart"]});
  google.charts.setOnLoadCallback(drawAllCharts);
}
