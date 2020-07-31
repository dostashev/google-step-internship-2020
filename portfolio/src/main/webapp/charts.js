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
    height: 400,
    hAxis: {
      title: "Date",
    },
    vAxis: {
      title: "Sentiment score",
      ticks: emojis.map((emoji, index) => {
        return {v: (2 * (index + 0.5) / emojis.length - 1), f: emoji};
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

async function drawWordTree() {

  let comments = await fetch("/comments?all=true")
    .then(response => response.json())
    .then(json => json.flatMap(
      comment => comment.text
        .replace(/[^ -~]/g, " ") // Filter out all non-printable chars, replace with space so words don't get joined
        .split(/[\.;?!]/) // Split into sentences
        .filter(sentence => sentence.trim().length > 0) // Filter out empty and whitespace-only sentences
    ));

  let data = new google.visualization.DataTable()

  data.addColumn("string", "Comments")

  data.addRows(comments.map(x => [x]));

  let options = {
    height: 1500,
    wordtree: {
      format: 'implicit',
    }
  };

  let chart = new google.visualization.WordTree(document.getElementById("wordtree"));

  chart.draw(data, options);
}

function drawAllCharts() {
  drawSentimentChart();
  drawWordTree();
}

window.onload = () => {
  google.charts.load("current", {"packages": ["corechart", "wordtree"]});
  google.charts.setOnLoadCallback(drawAllCharts);
}
